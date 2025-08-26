document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const payload = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    }

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json().catch(() => ({}));

        const messageDiv = document.getElementById("message");

        if (response.ok) {
            messageDiv.style.color = "green";
            messageDiv.textContent = data.message;
            window.location.replace("/home")
        } else {
            messageDiv.style.color = "red";
            messageDiv.textContent = data.error;
        }

    } catch (err) {
        document.getElementById("message").textContent = "Something went wrong!";
    }
});