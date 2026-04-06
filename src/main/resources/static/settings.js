document.addEventListener("DOMContentLoaded", () => {
    const preferences = MyListPreferences.getPreferences();
    document.getElementById("themeSelect").value = preferences.theme;
    document.getElementById("fontSelect").value = preferences.font;
    document.getElementById("languageSelect").value = preferences.language;
    updatePreviewDate();

    document.getElementById("themeSelect").addEventListener("change", previewSettings);
    document.getElementById("fontSelect").addEventListener("change", previewSettings);
    document.getElementById("languageSelect").addEventListener("change", previewSettings);
});

function previewSettings() {
    const preview = {
        theme: document.getElementById("themeSelect").value,
        font: document.getElementById("fontSelect").value,
        language: document.getElementById("languageSelect").value
    };

    document.documentElement.dataset.theme = preview.theme;
    document.documentElement.dataset.font = preview.font;

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
        theme: document.getElementById("themeSelect").value,
        font: document.getElementById("fontSelect").value,
        language: document.getElementById("languageSelect").value
    };

    MyListPreferences.savePreferences(preferences);
    MyListPreferences.applyPreferences();
    showSettingsToast(MyListPreferences.translate("savedToast"));
}

function resetSettings() {
    MyListPreferences.savePreferences({ ...MyListPreferences.DEFAULT_PREFERENCES });
    document.getElementById("themeSelect").value = MyListPreferences.DEFAULT_PREFERENCES.theme;
    document.getElementById("fontSelect").value = MyListPreferences.DEFAULT_PREFERENCES.font;
    document.getElementById("languageSelect").value = MyListPreferences.DEFAULT_PREFERENCES.language;
    MyListPreferences.applyPreferences();
    showSettingsToast(MyListPreferences.translate("resetToast"));
}

function updatePreviewDate() {
    document.getElementById("previewDate").textContent = new Date().toLocaleDateString();
}

function showSettingsToast(message) {
    const existing = document.querySelector(".toast");
    if (existing) {
        existing.remove();
    }

    const toast = document.createElement("div");
    toast.className = "toast visible";
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.remove("visible");
        setTimeout(() => toast.remove(), 200);
    }, 2200);
}
