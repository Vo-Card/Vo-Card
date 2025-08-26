document.getElementById("registerForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const payload = {
        displayName: document.getElementById("displayName").value,
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
        confirmPassword: document.getElementById("confirmPassword").value
    };

    try {
        const response = await fetch("/api/auth/register", {
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
        } else {
            messageDiv.style.color = "red";
            messageDiv.textContent = data.error;
        }

    } catch (err) {
        document.getElementById("message").textContent = "Something went wrong!";
    }
});
