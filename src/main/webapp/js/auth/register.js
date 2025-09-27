document
    .getElementById("registerForm")
    .addEventListener("submit", async function (e) {
        e.preventDefault();

        const payload = {
            displayName: /** @type {HTMLInputElement} */ (
                document.getElementById("displayName")
            ).value.trim(),

            username: /** @type {HTMLInputElement} */ (
                document.getElementById("username")
            ).value.trim(),

            password: /** @type {HTMLInputElement} */ (
                document.getElementById("password")
            ).value.trim(),

            confirmPassword: /** @type {HTMLInputElement} */ (
                document.getElementById("confirmPassword")
            ).value.trim(),
        };

        try {
            const response = await fetch("/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });

            const data = await response.json().catch(() => ({}));

            const messageDiv = document.getElementById("message");

            if (response.ok) {
                messageDiv.style.color = "green";
                messageDiv.textContent = data.message;
                setTimeout(() => window.location.replace("/login"), 500);
            } else {
                messageDiv.style.color = "red";
                messageDiv.textContent = data.error;
            }
        } catch (err) {
            document.getElementById("message").textContent =
                "Something went wrong!";
        }
    });
