import { deckLoader, deckDetailLoader, levelDetailLoader, cardDetailLoader, loadVoteCards } from '/js/deckManagement/deckLoader.js';
import { statsLoader } from '/js/workspace/statsLoader.js';
// import { snowfallEffect } from '/js/workspace/snowfall.js';

// Use to cache pages
const cache = new Map();

let currentController = null;

/**
 * 
 * 
 * @param {String} path 
 * @param {AbortController} signal 
 * @returns 
 */
export async function fetchPage(path, signal = null) {
    if (cache.has(path)) return cache.get(path);

    try {
        const res = await fetch(path, {
            headers: { "X-Requested-With": "XMLHttpRequest" },
            signal: signal.signal
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

/**
 * 
 * @param {String} path 
 * @param {Boolean} addHistory 
 * @returns 
 */
export async function loadPage(path, addHistory = true) {
    // Cancel previous request
    if (currentController) currentController.abort();
    currentController = new AbortController();

    const html = await fetchPage(path, currentController);

    if (!html) {
        window.location.href = "/error/404";
        return;
    }

    const contentEl = document.getElementById("content");
    if (contentEl) contentEl.innerHTML = html;

    if (path === "/workspace/home") initChartz();
    if (path === "/workspace/stats") statsLoader();
    if (path === "/workspace/playground") loadVoteCards();
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

// Preload on hover
document.addEventListener("mouseover", e => {
    const target = e.target;
    if (target instanceof Element) {
        const link = target.closest("a[data-workspace]");
        if (link && !cache.has(link.getAttribute("href"))) {
            fetchPage(link.getAttribute("href")); // preload
        }
    }
});