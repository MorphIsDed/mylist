const ui = {};

document.addEventListener("DOMContentLoaded", async () => {
    cacheUi();
    if (window.MyListAuth) {
        const user = await MyListAuth.requireAuth();
        if (!user) return;
        MyListAuth.populateUserUi(user);
    }

    const preferences = MyListPreferences.getPreferences();
    ui.themeSelect.value = preferences.theme;
    ui.fontSelect.value = preferences.font;
    ui.languageSelect.value = preferences.language;
    ui.previewDate.textContent = new Date().toLocaleDateString();

    ui.themeSelect.addEventListener("change", previewSettings);
    ui.fontSelect.addEventListener("change", previewSettings);
    ui.languageSelect.addEventListener("change", previewSettings);
    ui.saveSettingsBtn.addEventListener("click", saveSettings);
    ui.resetSettingsBtn.addEventListener("click", resetSettings);
    ui.exportBackupBtn.addEventListener("click", exportBackup);
    document.addEventListener("click", onDocumentActionClick);
});

function cacheUi() {
    ui.themeSelect = document.getElementById("themeSelect");
    ui.fontSelect = document.getElementById("fontSelect");
    ui.languageSelect = document.getElementById("languageSelect");
    ui.previewDate = document.getElementById("previewDate");
    ui.saveSettingsBtn = document.getElementById("saveSettingsBtn");
    ui.resetSettingsBtn = document.getElementById("resetSettingsBtn");
    ui.exportBackupBtn = document.getElementById("exportBackupBtn");
    ui.status = document.getElementById("settingsStatus");
    ui.liveRegion = document.getElementById("liveRegion");
}

function onDocumentActionClick(event) {
    const actionTarget = event.target.closest("[data-action]");
    if (!actionTarget) return;
    if (actionTarget.dataset.action === "logout") {
        logout();
    }
}

function previewSettings() {
    const preview = {
        theme: ui.themeSelect.value,
        font: ui.fontSelect.value,
        language: ui.languageSelect.value
    };

    document.documentElement.dataset.theme = preview.theme;
    document.documentElement.dataset.font = preview.font;
    document.documentElement.lang = preview.language;

    const dict = MyListPreferences.TRANSLATIONS[preview.language] || MyListPreferences.TRANSLATIONS.en;
    document.querySelectorAll("[data-i18n]").forEach(element => {
        const key = element.dataset.i18n;
        if (dict[key]) {
            element.textContent = dict[key];
        }
    });
}

function saveSettings() {
    const preferences = {
        theme: ui.themeSelect.value,
        font: ui.fontSelect.value,
        language: ui.languageSelect.value
    };
    MyListPreferences.savePreferences(preferences);
    MyListPreferences.applyPreferences();
    showToast(MyListPreferences.translate("savedToast"), "success");
    announce(MyListPreferences.translate("savedToast"));
}

function resetSettings() {
    MyListPreferences.savePreferences({ ...MyListPreferences.DEFAULT_PREFERENCES });
    ui.themeSelect.value = MyListPreferences.DEFAULT_PREFERENCES.theme;
    ui.fontSelect.value = MyListPreferences.DEFAULT_PREFERENCES.font;
    ui.languageSelect.value = MyListPreferences.DEFAULT_PREFERENCES.language;
    MyListPreferences.applyPreferences();
    showToast(MyListPreferences.translate("resetToast"), "success");
}

async function exportBackup() {
    setStatus(MyListPreferences.translate("exportInProgress"));
    try {
        const response = await fetch("/api/todos/export", { headers: { Accept: "application/json" } });
        if (response.status === 401 && window.MyListAuth) {
            MyListAuth.logout();
            return;
        }
        if (!response.ok) {
            throw new Error(MyListPreferences.translate("exportFailed"));
        }
        const blob = await response.blob();
        const fallbackName = `mylist-backup-${new Date().toISOString().slice(0, 10)}.json`;
        const contentDisposition = response.headers.get("content-disposition") || "";
        const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/i);
        const filename = filenameMatch ? filenameMatch[1] : fallbackName;

        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = filename;
        link.click();
        URL.revokeObjectURL(link.href);

        setStatus("");
        showToast(MyListPreferences.translate("exportSuccess"), "success");
    } catch (_) {
        setStatus("");
        showToast(MyListPreferences.translate("exportFailed"), "error");
    }
}

function logout() {
    if (window.MyListAuth) {
        MyListAuth.logout();
    }
}

function showToast(message, type = "default") {
    const existing = document.querySelector(".toast");
    if (existing) {
        existing.remove();
    }

    const toast = document.createElement("div");
    toast.className = `toast visible ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.remove("visible");
        setTimeout(() => toast.remove(), 250);
    }, 2200);
}

function setStatus(message) {
    if (!ui.status) return;
    ui.status.classList.toggle("hidden", !message);
    ui.status.textContent = message;
}

function announce(message) {
    if (!ui.liveRegion) return;
    ui.liveRegion.textContent = "";
    setTimeout(() => {
        ui.liveRegion.textContent = message;
    }, 30);
}
