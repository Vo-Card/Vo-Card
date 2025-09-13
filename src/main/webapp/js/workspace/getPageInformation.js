import { TokenManager, fetchWithAuth } from "/js/auth/auth.js";

//TODO: Make a proper documentation for this file

import { loadPage } from "/js/workspace/pageManager.js";

/**
 * Save the previous page URL
 */
window.addEventListener("popstate", (event) => {
    const path = event.state?.path || window.location.pathname;
    loadPage(path, false); // false = don’t pushState again
});

document.addEventListener("DOMContentLoaded", async () => {
    const mainContent = document.getElementById("content");

    try {
        const response = await fetchWithAuth("/api/ping");
        if (response.ok) {
            mainContent.style.display = "block";
        } else {
            window.location.replace("/login");
        }
    } catch (error) {
        window.location.replace("/login");
    }

    const userData = TokenManager.getUserData();

    const username = userData.username;
    const displayName = userData.display_name;
    const uid = userData.uid;

    const usernamePlaceholder = document.querySelector(
        "[username-placeholder]"
    );
    if (usernamePlaceholder) {
        usernamePlaceholder.textContent = displayName;
    }

    console.log(TokenManager.getUserData());
    loadPage(document.location.pathname);
});

function startHideTimer() {
    let hideTimeout = setTimeout(() => {
        document.getElementById("sort-options").style.display = "none";
    }, 3000);
}

// Change this later
// @ts-ignore
window.openSortOption = function openSortOption() {
    const el = /** @type {HTMLElement} */ (
        document.getElementById("sort-options")
    );
    el.style.display = el.style.display === "block" ? "none" : "block";
    startHideTimer();
};
