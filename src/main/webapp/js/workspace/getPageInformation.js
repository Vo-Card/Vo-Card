import { TokenManager, fetchWithAuth } from '/js/auth/auth.js';

import { deckLoader, deckDetailLoader } from '/js/deckManagement/deckLoader.js';
import { statsLoader } from '/js/workspace/statsLoader.js';
//TODO: Make a proper documentation for this file

// Use to cache pages
const cache = new Map();

let currentController = null;

document.addEventListener("click", e => {
    const link = e.target.closest("a[data-workspace]");
    if (link) {
        e.preventDefault();
        loadPage(link.getAttribute("href"), document.location.pathname == link.getAttribute("href") ? false : true);
    }
});

// Preload on hover
document.addEventListener("mouseover", e => {
    const link = e.target.closest("a[data-workspace]");
    if (link && !cache.has(link.getAttribute("href"))) {
        fetchPage(link.getAttribute("href")); // preload
    }
});

async function fetchPage(path, signal) {
    if (cache.has(path)) return cache.get(path);

    try {
        const res = await fetch(path, {
            headers: { "X-Requested-With": "XMLHttpRequest" },
            signal
        });

        if (!res.ok) throw new Error("404");

        const html = await res.text();
        cache.set(path, html);
        return html;
    } catch (err) {
        if (err.name === "AbortError") 
            return null;
        console.error(err);
        return null;
    }
}

async function loadPage(path, addHistory = true) {
    // Cancel previous request
    if (currentController) currentController.abort();
    currentController = new AbortController();

    const html = await fetchPage(path, currentController.signal);

    if (!html) {
        window.location.href = "/error/404";
        return;
    }

    const contentEl = document.getElementById("content");
    if (contentEl) contentEl.innerHTML = html;

    if (path === "/workspace/home") initChartz();
    if (path === "/workspace/stats") statsLoader();
    if (path.startsWith("/workspace/decks")) {
        // /workspace/decks or /workspace/decks/
        if (/^\/workspace\/decks\/?$/.test(path)) {
            deckLoader();
        }
        // /workspace/decks/{deckId} or /workspace/decks/{deckId}/
        else if (/^\/workspace\/decks\/[^/]+\/?$/.test(path)) {
            deckDetailLoader(path);
        }
        // /workspace/decks/{deckId}/{levelId} or with trailing slash
        else if (/^\/workspace\/decks\/[^/]+\/[^/]+\/?$/.test(path)) {
            levelDetailLoader(path);
        }
        // /workspace/decks/{deckId}/{levelId}/{cardId} or with trailing slash
        else if (/^\/workspace\/decks\/[^/]+\/[^/]+\/[^/]+\/?$/.test(path)) {
            cardDetailLoader(path);
        }
    }

        console.log(path)

    if (addHistory) {
        window.history.pushState({ path }, "", path);
    }
}

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
    temporalInterceptor()
    loadPage(document.location.pathname);
});