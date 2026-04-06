const API_URL = "http://localhost:8080/todos";

/* ===== LOAD TASKS ===== */
function loadTasks() {
    fetch(API_URL)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("taskList");
            list.innerHTML = "";

            if (data.length === 0) {
                list.innerHTML = `<div class="empty">No tasks yet. Stay productive.</div>`;
                return;
            }

            data.forEach(task => {
                const div = document.createElement("div");
                div.className = "task";

                if (task.completed) {
                    div.classList.add("completed");
                }

                div.innerHTML = `
                    <div class="task-info">
                        <input type="checkbox" class="checkbox"
                            ${task.completed ? "checked" : ""}
                            onclick="toggleTask(${task.id})">

                        <div class="task-text">
                            <strong>${task.title}</strong>
                            <span>${task.taskDescription || ""}</span>
                        </div>
                    </div>

                    <button class="delete-btn" onclick="deleteTask(${task.id})">✕</button>
                `;

                list.appendChild(div);
            });
        });
}

/* ===== ADD TASK ===== */
function addTask() {
    const titleInput = document.getElementById("title");
    const descInput = document.getElementById("desc");

    const title = titleInput.value.trim();
    const desc = descInput.value.trim();

    if (!title) return;

    fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title: title,
            taskDescription: desc,
            completed: false
        })
    }).then(() => {
        titleInput.value = "";
        descInput.value = "";
        loadTasks();
    });
}

/* ===== DELETE TASK ===== */
function deleteTask(id) {
    fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    }).then(() => {
        loadTasks();
    });
}

/* ===== TOGGLE COMPLETE ===== */
function toggleTask(id) {
    fetch(`${API_URL}/toggle/${id}`, {
        method: "PUT"
    }).then(() => {
        loadTasks();
    });
}

/* ===== ENTER KEY SUPPORT ===== */
document.addEventListener("keydown", function(e) {
    if (e.key === "Enter") {
        addTask();
    }
});

/* ===== INITIAL LOAD ===== */
loadTasks();