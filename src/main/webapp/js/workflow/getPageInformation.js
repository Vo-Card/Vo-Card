import { TokenManager, fetchWithAuth } from '/js/auth/auth.js';

//TODO: Make a proper documentation for this file

async function loadPage(path, addHistory = true) {
    try {
        const res = await fetch(path, { headers: { "X-Requested-With": "XMLHttpRequest" } });
        if (!res.ok) {
            window.location.href = "/error/404";
            return;
        }

        const html = await res.text();
        const contentEl = document.getElementById("content");
        if (contentEl) contentEl.innerHTML = html;

        if (path === "/workflow/home") initChartz();

        if (addHistory) {
            window.history.pushState({ path }, "", path);
        }

    } catch (err) {
        console.error(err);
        window.location.href = "/error/404";
    }
}


document.addEventListener("click", e => {
    const link = e.target.closest("a[data-workflow]");
    if (link) {
        e.preventDefault();
        loadPage(link.getAttribute("href"));
    }
});

window.addEventListener("popstate", (event) => {
    const path = event.state?.path || window.location.pathname;
    loadPage(path, false); // false = don’t pushState again
});

function runPageInit() {
    const page = document.location.pathname.split("/").pop();
    if (page === "home" || page === "" ) initChartz();
}


document.addEventListener('DOMContentLoaded', async () => {
    const mainContent = document.getElementById('content');
    
    if (!TokenManager.getAccessToken()) {
        window.location.replace("/login");
        return;
    }
    
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
    runPageInit();
});