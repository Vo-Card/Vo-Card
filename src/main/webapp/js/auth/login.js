import { TokenManager } from "/js/auth/auth.js";

document
    .getElementById("loginForm")
    .addEventListener("submit", async function (e) {
        e.preventDefault();
        const messageDiv = document.getElementById("message");

        const submitBtn = /** @type {HTMLButtonElement} */ (
            document.getElementById("loginButton")
        );

        submitBtn.disabled = true;

        const payload = {
            username: /** @type {HTMLInputElement} */ (
                document.getElementById("username")
            ).value.trim(),

            password: /** @type {HTMLInputElement} */ (
                document.getElementById("password")
            ).value.trim(),

            rememberMe: /** @type {HTMLInputElement} */ (
                document.getElementById("rememberMe")
            ).checked,
        };

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            const data = await response.json();

            if (response.ok) {
                TokenManager.setAccessToken(data.access_token);

                messageDiv.style.color = "green";
                messageDiv.textContent = "Login successful!";
                setTimeout(() => window.location.replace("/home"), 500);
            } else {
                messageDiv.style.color = "red";
                messageDiv.textContent =
                    data.error || "An unknown error occurred.";
            }
        } catch (err) {
            console.error(err);
            messageDiv.style.color = "red";
            messageDiv.textContent =
                "Network or server error. Please try again.";
        } finally {
            submitBtn.disabled = false;
        }
    });
