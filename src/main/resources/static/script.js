const API_URL = "/api/todos";
const VIEW_STATE_KEY = "mylist.view-state";

const state = {
    tasks: [],
    editingId: null,
    quickFilter: "all",
    isLoading: false,
    recentlyDeleted: null
};

const ui = {};

document.addEventListener("DOMContentLoaded", async () => {
    cacheUi();
    restoreViewState();
    bindControls();
    syncFormLabels();

    if (window.MyListAuth) {
        const user = await MyListAuth.requireAuth();
        if (!user) return;
        MyListAuth.populateUserUi(user);
    }

    await loadTasks();
});

function cacheUi() {
    ui.taskForm = document.getElementById("taskForm");
    ui.taskList = document.getElementById("taskList");
    ui.searchInput = document.getElementById("searchInput");
    ui.statusFilter = document.getElementById("statusFilter");
    ui.sortOrder = document.getElementById("sortOrder");
    ui.title = document.getElementById("title");
    ui.desc = document.getElementById("desc");
    ui.dueDate = document.getElementById("dueDate");
    ui.priority = document.getElementById("priority");
    ui.formHeading = document.getElementById("formHeading");
    ui.submitBtn = document.getElementById("submitBtn");
    ui.cancelEditBtn = document.getElementById("cancelEditBtn");
    ui.quickFilters = document.getElementById("quickFilters");
    ui.boardStatus = document.getElementById("boardStatus");
    ui.liveRegion = document.getElementById("liveRegion");
}

function bindControls() {
    ui.taskForm.addEventListener("submit", submitTaskForm);
    ui.searchInput.addEventListener("input", onViewChanged);
    ui.statusFilter.addEventListener("change", onViewChanged);
    ui.sortOrder.addEventListener("change", onViewChanged);
    ui.taskList.addEventListener("click", onTaskActionClick);
    ui.quickFilters.addEventListener("click", onQuickFilterClick);
    document.addEventListener("click", onDocumentActionClick);
    document.addEventListener("keydown", onGlobalShortcuts);
}

function onViewChanged() {
    if (state.quickFilter !== "all") {
        state.quickFilter = "all";
        setActiveQuickFilter();
    }
    persistViewState();
    renderTasks();
}

function onDocumentActionClick(event) {
    const actionTarget = event.target.closest("[data-action]");
    if (!actionTarget) return;

    const { action } = actionTarget.dataset;
    if (action === "logout") {
        logout();
    } else if (action === "cancel-edit") {
        cancelEdit();
    } else if (action === "reset-form") {
        resetForm();
    } else if (action === "clear-completed") {
        clearCompleted();
    } else if (action === "undo-delete") {
        undoDelete();
    }
}

function onTaskActionClick(event) {
    const button = event.target.closest("[data-task-action]");
    if (!button) return;
    const taskId = Number(button.dataset.id);

    if (button.dataset.taskAction === "toggle") {
        toggleTask(taskId);
    } else if (button.dataset.taskAction === "edit") {
        editTask(taskId);
    } else if (button.dataset.taskAction === "delete") {
        deleteTask(taskId);
    }
}

function onQuickFilterClick(event) {
    const button = event.target.closest("[data-action='quick-filter']");
    if (!button) return;
    state.quickFilter = button.dataset.filter || "all";
    persistViewState();
    setActiveQuickFilter();
    renderTasks();
}

function onGlobalShortcuts(event) {
    const targetTag = event.target.tagName;
    const typing = targetTag === "INPUT" || targetTag === "TEXTAREA" || targetTag === "SELECT";

    if (event.key === "/" && !typing) {
        event.preventDefault();
        ui.searchInput.focus();
    } else if (event.key.toLowerCase() === "n" && !typing) {
        event.preventDefault();
        ui.title.focus();
    } else if (event.key === "Escape" && state.editingId !== null) {
        cancelEdit();
    } else if ((event.ctrlKey || event.metaKey) && event.key === "Enter" && typing) {
        ui.taskForm.requestSubmit();
    }
}

async function submitTaskForm(event) {
    event.preventDefault();
    const title = ui.title.value.trim();
    if (!title) {
        announce(MyListPreferences.translate("validationTitle"));
        showToast(MyListPreferences.translate("validationTitle"), "error");
        return;
    }

    const payload = {
        title,
        taskDescription: ui.desc.value.trim(),
        completed: false,
        dueDate: ui.dueDate.value || null,
        priority: ui.priority.value
    };

    if (state.editingId !== null) {
        const existing = state.tasks.find(task => task.id === state.editingId);
        payload.completed = existing ? existing.completed : false;
        await mutateTask(`${API_URL}/${state.editingId}`, "PUT", payload, MyListPreferences.translate("updateToast"), true);
        return;
    }

    await mutateTask(API_URL, "POST", payload, MyListPreferences.translate("addToast"), false);
}

async function mutateTask(url, method, payload, successMessage, editingFlow) {
    try {
        await requestJson(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
        showToast(successMessage, "success");
        announce(successMessage);
        if (editingFlow) {
            cancelEdit();
        } else {
            resetForm();
        }
        await loadTasks();
    } catch (error) {
        showToast(error.message, "error");
    }
}

async function loadTasks() {
    setLoading(true, MyListPreferences.translate("loadingTasks"));
    try {
        const tasks = await requestJson(API_URL);
        state.tasks = Array.isArray(tasks) ? tasks : [];
        renderTasks();
        updateStats();
    } catch (error) {
        state.tasks = [];
        renderTasks();
        updateStats();
        showToast(error.message, "error");
    } finally {
        setLoading(false);
    }
}

function setLoading(isLoading, message = "") {
    state.isLoading = isLoading;
    ui.boardStatus.classList.toggle("hidden", !isLoading);
    ui.boardStatus.textContent = isLoading ? message : "";
}

function renderTasks() {
    const tasks = getVisibleTasks();
    ui.taskList.innerHTML = "";
    const fragment = document.createDocumentFragment();

    if (tasks.length === 0) {
        const empty = document.createElement("article");
        empty.className = "empty-card";
        empty.innerHTML = `
            <p class="panel-label">${MyListPreferences.translate("emptyTitle")}</p>
            <h3>${MyListPreferences.translate("emptyDescription")}</h3>
            <p class="muted-copy">${MyListPreferences.translate("emptyHint")}</p>
        `;
        fragment.appendChild(empty);
        ui.taskList.appendChild(fragment);
        return;
    }

    tasks.forEach(task => {
        const article = document.createElement("article");
        article.className = `task-card ${task.completed ? "completed" : ""}`;
        article.innerHTML = buildTaskMarkup(task);
        fragment.appendChild(article);
    });

    ui.taskList.appendChild(fragment);
}

function buildTaskMarkup(task) {
    const toggleText = task.completed
        ? MyListPreferences.translate("reopenAction")
        : MyListPreferences.translate("completeAction");
    return `
        <div class="task-card-topline">
            <span class="priority-orb ${task.priority || "medium"}"></span>
            <span class="panel-label">${formatPriority(task.priority)}</span>
            <span class="task-date">${formatDueDate(task.dueDate)}</span>
        </div>
        <div class="task-card-body">
            <h3>${escapeHtml(task.title)}</h3>
            <p>${escapeHtml(task.taskDescription || "")}</p>
        </div>
        <div class="task-card-actions">
            <button type="button" class="chip action-chip" data-task-action="toggle" data-id="${task.id}">${toggleText}</button>
            <button type="button" class="chip action-chip" data-task-action="edit" data-id="${task.id}">${MyListPreferences.translate("editAction")}</button>
            <button type="button" class="chip action-chip danger-chip" data-task-action="delete" data-id="${task.id}">${MyListPreferences.translate("deleteAction")}</button>
        </div>
    `;
}

function getVisibleTasks() {
    const search = document.getElementById("searchInput").value.trim().toLowerCase();
    const filter = document.getElementById("statusFilter").value;
    const sort = document.getElementById("sortOrder").value;
    const today = new Date().toISOString().slice(0, 10);

    const filtered = state.tasks.filter(task => {
        const text = `${task.title} ${task.taskDescription || ""}`.toLowerCase();
        if (search && !text.includes(search)) {
            return false;
        }
        if (state.quickFilter === "high" && task.priority !== "high") {
            return false;
        }
        if (state.quickFilter === "dueSoon" && !isDueSoon(task.dueDate)) {
            return false;
        }
        if (state.quickFilter === "completed" && !task.completed) {
            return false;
        }

        switch (filter) {
            case "active":
                return !task.completed;
            case "completed":
                return task.completed;
            case "today":
                return task.dueDate === today;
            case "overdue":
                return Boolean(task.dueDate) && task.dueDate < today && !task.completed;
            default:
                return true;
        }
    });

    filtered.sort((left, right) => {
        switch (sort) {
            case "priorityDesc":
                return priorityRank(right.priority) - priorityRank(left.priority);
            case "titleAsc":
                return left.title.localeCompare(right.title);
            case "recent":
                return right.id - left.id;
            default:
                return compareDueDates(left.dueDate, right.dueDate);
        }
    });

    return filtered;
}

function updateStats() {
    const total = state.tasks.length;
    const completed = state.tasks.filter(task => task.completed).length;
    const active = total - completed;
    const dueSoon = state.tasks.filter(task => !task.completed && isDueSoon(task.dueDate)).length;

    document.getElementById("totalCount").textContent = String(total);
    document.getElementById("activeCount").textContent = String(active);
    document.getElementById("completedCount").textContent = String(completed);
    document.getElementById("dueSoonCount").textContent = String(dueSoon);
}

function editTask(id) {
    const task = state.tasks.find(item => item.id === id);
    if (!task) {
        return;
    }
    state.editingId = id;
    ui.title.value = task.title || "";
    ui.desc.value = task.taskDescription || "";
    ui.dueDate.value = task.dueDate || "";
    ui.priority.value = task.priority || "medium";
    syncFormLabels();
    ui.title.focus();
}

function cancelEdit() {
    state.editingId = null;
    resetForm();
    syncFormLabels();
}

function resetForm() {
    ui.taskForm.reset();
    ui.priority.value = "medium";
}

function syncFormLabels() {
    const editing = state.editingId !== null;
    ui.formHeading.textContent = editing
        ? MyListPreferences.translate("updateTaskButton")
        : MyListPreferences.translate("taskComposerTitle");
    ui.submitBtn.textContent = editing
        ? MyListPreferences.translate("updateTaskButton")
        : MyListPreferences.translate("addTaskButton");
    ui.cancelEditBtn.classList.toggle("hidden", !editing);
}

async function deleteTask(id) {
    try {
        const existing = state.tasks.find(task => task.id === id);
        await requestJson(`${API_URL}/${id}`, { method: "DELETE" });
        state.recentlyDeleted = existing || null;
        if (state.editingId === id) {
            cancelEdit();
        }
        showToast(MyListPreferences.translate("deleteToast"), "success", state.recentlyDeleted ? {
            label: MyListPreferences.translate("undoAction"),
            action: "undo-delete"
        } : null);
        await loadTasks();
    } catch (error) {
        showToast(error.message, "error");
    }
}

async function toggleTask(id) {
    try {
        await requestJson(`${API_URL}/toggle/${id}`, { method: "PUT" });
        await loadTasks();
    } catch (error) {
        showToast(error.message);
    }
}

async function clearCompleted() {
    const done = state.tasks.filter(task => task.completed);
    if (done.length === 0) {
        return;
    }

    try {
        await Promise.all(done.map(task => requestJson(`${API_URL}/${task.id}`, { method: "DELETE" })));
        showToast(MyListPreferences.translate("clearToast"), "success");
        await loadTasks();
    } catch (error) {
        showToast(error.message, "error");
    }
}

function setActiveQuickFilter() {
    document.querySelectorAll("[data-action='quick-filter']").forEach(chip => {
        chip.classList.toggle("is-active", chip.dataset.filter === state.quickFilter);
    });
}

function priorityRank(priority) {
    switch (priority) {
        case "high":
            return 3;
        case "medium":
            return 2;
        default:
            return 1;
    }
}

function compareDueDates(left, right) {
    if (!left && !right) return 0;
    if (!left) return 1;
    if (!right) return -1;
    return left.localeCompare(right);
}

function isDueSoon(dueDate) {
    if (!dueDate) return false;
    const today = new Date();
    const due = new Date(dueDate);
    const msPerDay = 1000 * 60 * 60 * 24;
    const diff = Math.ceil((due - new Date(today.toDateString())) / msPerDay);
    return diff >= 0 && diff <= 3;
}

function formatDueDate(dueDate) {
    if (!dueDate) {
        return MyListPreferences.translate("taskMetaNoDue");
    }
    const today = new Date().toISOString().slice(0, 10);
    if (dueDate < today) {
        return `${MyListPreferences.translate("taskMetaOverdue")} ${dueDate}`;
    }
    if (dueDate === today) {
        return `${MyListPreferences.translate("taskMetaToday")} ${dueDate}`;
    }
    return dueDate;
}

function formatPriority(priority) {
    switch (priority) {
        case "high":
            return MyListPreferences.translate("priorityHigh");
        case "low":
            return MyListPreferences.translate("priorityLow");
        default:
            return MyListPreferences.translate("priorityMedium");
    }
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#39;");
}

function showToast(message, type = "default", action = null) {
    const existing = document.querySelector(".toast");
    if (existing) {
        existing.remove();
    }

    const toast = document.createElement("div");
    toast.className = `toast visible ${type}`;
    toast.innerHTML = `<span>${escapeHtml(message)}</span>`;
    if (action) {
        toast.innerHTML += `<button type="button" class="ghost-action" data-action="${action.action}">${escapeHtml(action.label)}</button>`;
    }
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.remove("visible");
        setTimeout(() => toast.remove(), 250);
    }, 2200);
}

async function undoDelete() {
    if (!state.recentlyDeleted) return;
    const backup = state.recentlyDeleted;
    state.recentlyDeleted = null;
    await mutateTask(API_URL, "POST", {
        title: backup.title,
        taskDescription: backup.taskDescription || "",
        completed: backup.completed || false,
        dueDate: backup.dueDate || null,
        priority: backup.priority || "medium"
    }, MyListPreferences.translate("undoToast"), false);
}

function logout() {
    if (window.MyListAuth) MyListAuth.logout();
}

function persistViewState() {
    const viewState = {
        search: ui.searchInput.value,
        filter: ui.statusFilter.value,
        sort: ui.sortOrder.value,
        quickFilter: state.quickFilter
    };
    localStorage.setItem(VIEW_STATE_KEY, JSON.stringify(viewState));
}

function restoreViewState() {
    try {
        const raw = localStorage.getItem(VIEW_STATE_KEY);
        if (!raw) return;
        const saved = JSON.parse(raw);
        if (saved && typeof saved === "object") {
            if (typeof saved.search === "string") ui.searchInput.value = saved.search;
            if (typeof saved.filter === "string") ui.statusFilter.value = saved.filter;
            if (typeof saved.sort === "string") ui.sortOrder.value = saved.sort;
            if (typeof saved.quickFilter === "string") state.quickFilter = saved.quickFilter;
        }
    } catch (_) {
        // Ignore malformed local state.
    }
    setActiveQuickFilter();
}

function announce(message) {
    if (!ui.liveRegion) return;
    ui.liveRegion.textContent = "";
    setTimeout(() => {
        ui.liveRegion.textContent = message;
    }, 30);
}

async function requestJson(url, options = {}) {
    let response;
    try {
        response = await fetch(url, options);
    } catch (_) {
        throw new Error(MyListPreferences.translate("networkError"));
    }
    if (!response.ok) {
        const message = await buildApiErrorMessage(response);
        if (response.status === 401 && window.MyListAuth) {
            setTimeout(() => MyListAuth.logout(), 300);
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type") || "";
    if (contentType.includes("application/json")) {
        return response.json();
    }

    return null;
}

async function buildApiErrorMessage(response) {
    try {
        const contentType = response.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            const payload = await response.json();
            if (payload && typeof payload.error === "string" && payload.error.trim()) {
                return payload.error;
            }
        } else {
            const message = await response.text();
            if (message && message.trim()) {
                return message.trim();
            }
        }
    } catch (_) {
        // Fall through to generic message.
    }

    if (response.status === 401) return MyListPreferences.translate("authExpired");

    return `Request failed (${response.status})`;
}
