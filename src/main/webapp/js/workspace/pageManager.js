import { deckLoader, deckDetailLoader, levelDetailLoader, cardDetailLoader, loadVoteCards } from '/js/deckManagement/deckLoader.js';
import { statsLoader } from '/js/workspace/statsLoader.js';
import { sendPackDeckId, cardSessionPlay } from '/js/workspace/review.js'
import { snowfallEffect } from '/js/workspace/snowfall.js';

// Use to cache pages
const cache = new Map();
// Cache for CSS
const cssCache = new Set();

let currentController = null;

// Map top-level pages to optional JS + CSS
const pageComponents = {
    "/workspace/home": { js: () => initChartz(), css: "/css/workspace/home.css" },
    "/workspace/stats": { js: statsLoader, css: "/css/workspace/stats.css" },
    "/workspace/playground": { js: () => { sendPackDeckId(["752111449966383104"]); loadVoteCards(); snowfallEffect(); }, css: "/css/workspace/playground.css" },
    "/workspace/review": { js: null, css: "/css/workspace/review.css" },
    "/workspace/decks": { js: null, css: "/css/workspace/decks.css" },
};

/**
 * Dynamically load CSS if not already loaded (returns a Promise)
 * @param {String} href
 * @returns {Promise}
 */
function loadCSS(href) {
    return new Promise(resolve => {
        if (cssCache.has(href)) {
            resolve();
            return;
        }
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = href;
        link.onload = () => {
            cssCache.add(href);
            resolve();
        };
        link.onerror = () => resolve();
        document.head.appendChild(link);
    });
}

/**
 * Fetch page content (AJAX)
 * @param {String} path 
 * @param {AbortController} signal 
 * @returns {Promise<string|null>}
 */
export async function fetchPage(path, signal = null) {
    if (cache.has(path)) return cache.get(path);

    try {
        const res = await fetch(path, {
            headers: { "X-Requested-With": "XMLHttpRequest" },
            signal: signal?.signal
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
 * Load a page with AJAX, inject CSS before showing content
 * @param {String} path 
 * @param {Boolean} addHistory 
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
    if (!contentEl) return;

    // Initialize JS + CSS based on mapping
    const component = Object.keys(pageComponents).find(p => path.startsWith(p));
    let cssPromise = Promise.resolve();
    let jsFn = null;

    if (component) {
        const { js, css } = pageComponents[component];
        if (css) cssPromise = loadCSS(css);
        if (js) jsFn = js;
    }

    // wait for CSS load (or cached)
    await cssPromise;

    contentEl.innerHTML = html;

    if (jsFn) jsFn(path);

    // Deck-specific AJAX loader
    if (path.startsWith("/workspace/decks")) {
        if (/^\/workspace\/decks\/?$/.test(path)) deckLoader();
        else if (/^\/workspace\/decks\/[^/]+\/?$/.test(path)) deckDetailLoader(path);
        else if (/^\/workspace\/decks\/[^/]+\/[^/]+\/?$/.test(path)) levelDetailLoader(path);
        else if (/^\/workspace\/decks\/[^/]+\/[^/]+\/[^/]+\/?$/.test(path)) cardDetailLoader(path);
    }

    if (addHistory) window.history.pushState({ path }, "", path);
    console.log("Loaded via AJAX:", path);
}

// Preload pages on hover
document.addEventListener("mouseover", event => {
    const target = event.target;
    if (target instanceof Element) {
        const link = target.closest("a[data-workspace]");
        if (link) {
            const href = link.getAttribute("href");
            if (href && !cache.has(href)) fetchPage(href);
        }
    }
});
