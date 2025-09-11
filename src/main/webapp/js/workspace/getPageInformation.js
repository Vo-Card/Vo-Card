import { TokenManager, fetchWithAuth } from '/js/auth/auth.js';

//TODO: Make a proper documentation for this file

import { loadPage } from '/js/workspace/pageManager.js';

/**
 * Save the previous page URL
 */
window.addEventListener("popstate", (event) => {
    const path = event.state?.path || window.location.pathname;
    loadPage(path, false); // false = don’t pushState again
});


document.addEventListener('DOMContentLoaded', async () => {
    const mainContent = document.getElementById('content');

    try {
        const response = await fetchWithAuth("/api/ping");
        if (response.ok) {
            mainContent.style.display = 'block';

        } else {
            window.location.replace("/login");
        }
    } catch (error) {
        window.location.replace("/login");
    }
    loadPage(document.location.pathname);
});

function startHideTimer() {
    let hideTimeout = setTimeout(() => {
        document.getElementById('sort-options').style.display = "none";
    }, 3000);
}

// @ts-ignore
window.openSortOption = function openSortOption() {
  const el = /** @type {HTMLElement} */ (document.getElementById('sort-options'));
  el.style.display = el.style.display === "block" ? "none" : "block";
  startHideTimer();
};