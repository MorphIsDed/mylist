const AUTH_ME_URL = "/api/auth/me";
const LOGIN_PAGE_URL = "/pages/login.html";
const HOME_PAGE_URL = "/pages/index.html";

let currentUser = null;

async function fetchCurrentUser() {
    const response = await fetch(AUTH_ME_URL, {
        headers: { "Accept": "application/json" }
    });

    if (response.status === 401) {
        return null;
    }

    if (!response.ok) {
        throw new Error("Failed to load current user");
    }

    currentUser = await response.json();
    return currentUser;
}

async function requireAuth() {
    const user = await fetchCurrentUser();
    if (!user) {
        window.location.href = LOGIN_PAGE_URL;
        return null;
    }
    return user;
}

function populateUserUi(user = currentUser) {
    if (!user) {
        return;
    }

    const name = user.name || user.email || "User";
    const initial = name.trim().charAt(0).toUpperCase();

    ["currentUserName", "settingsUserName"].forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = name;
        }
    });

    ["profileMark", "settingsProfileMark"].forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = initial;
        }
    });
}

function logout() {
    window.location.href = "/logout";
}

document.addEventListener("DOMContentLoaded", async () => {
    if (document.body.dataset.page === "login") {
        const user = await fetchCurrentUser();
        if (user) {
            window.location.href = HOME_PAGE_URL;
        }
    }
});

window.MyListAuth = {
    fetchCurrentUser,
    requireAuth,
    populateUserUi,
    logout
};
