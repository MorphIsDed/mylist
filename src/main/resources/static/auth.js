const AUTH_STORAGE_KEY = "mylist.authUser";

function getAuthUser() {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!raw) {
        return null;
    }
    try {
        return JSON.parse(raw);
    } catch (error) {
        return null;
    }
}

function requireAuth() {
    if (!getAuthUser()) {
        window.location.href = "login.html";
    }
}

function populateUserUi() {
    const user = getAuthUser();
    if (!user) {
        return;
    }

    const name = user.name || "User";
    const initial = name.trim().charAt(0).toUpperCase();
    const nameTargets = ["currentUserName", "settingsUserName"];
    const markTargets = ["profileMark", "settingsProfileMark"];

    nameTargets.forEach(id => {
        const node = document.getElementById(id);
        if (node) {
            node.textContent = name;
        }
    });

    markTargets.forEach(id => {
        const node = document.getElementById(id);
        if (node) {
            node.textContent = initial;
        }
    });
}

function logout() {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    window.location.href = "login.html";
}

document.addEventListener("DOMContentLoaded", () => {
    if (document.body.dataset.page === "login") {
        const user = getAuthUser();
        if (user) {
            window.location.href = "index.html";
            return;
        }

        const form = document.getElementById("loginForm");
        if (form) {
            form.addEventListener("submit", event => {
                event.preventDefault();

                const name = document.getElementById("loginName").value.trim();
                const email = document.getElementById("loginEmail").value.trim();
                const password = document.getElementById("loginPassword").value.trim();
                if (!name || !email || !password) {
                    return;
                }

                localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify({ name, email }));
                window.location.href = "index.html";
            });
        }
    }
});

window.MyListAuth = {
    getAuthUser,
    requireAuth,
    populateUserUi,
    logout
};
