const API_URL = "http://localhost:8080/todos";

function loadTasks() {
    fetch(API_URL)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("taskList");
            list.innerHTML = "";

            data.forEach(task => {
                const div = document.createElement("div");
                div.className = "task";

                div.innerHTML = `
                    <div class="task-info">
                        <strong>${task.title}</strong>
                        <span>${task.taskDescription}</span>
                    </div>
                    <button class="delete-btn" onclick="deleteTask(${task.id})">Delete</button>
                `;

                list.appendChild(div);
            });
        });
}

function addTask() {
    const title = document.getElementById("title").value;
    const desc = document.getElementById("desc").value;

    fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title: title,
            taskDescription: desc
        })
    }).then(() => {
        loadTasks();
    });
}

function deleteTask(id) {
    fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    }).then(() => loadTasks());
}

loadTasks();