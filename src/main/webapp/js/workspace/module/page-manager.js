import { snowfallEffect } from "/js/workspace/snowfall.js";
import {
    loadWeeklyChart,
    statsLoader,
} from "/js/workspace/handler/charts-handler.js";
import { fetchWithAuth, TokenManager } from "/js/auth/auth.js";
import {
    deckLoader,
    deckDetailLoader,
    levelDetailLoader,
    loadAllDecksToReview,
} from "../content/deck-loader.js";
import { displayPublicDecks } from "../handler/explorer-handler.js";
import { createPopupBox } from "../handler/popup-handler.js";
import { loadUserInformation } from "../handler/home-handler.js";
import { rtDropdowns } from "../dropdown.js";

// Use to cache pages
const cache = new Map();
// Cache for CSS
const cssCache = new Set();

let currentController = null;

// Map top-level pages to optional JS + CSS
const pageComponents = {
    "/workspace/home": {
        js: () => {
            loadWeeklyChart();
            loadUserInformation();
        },
        css: "/css/workspace/home.css",
    },
    "/workspace/stats": { js: statsLoader, css: "/css/workspace/stats.css" },
    "/workspace/playground": {
        js: () => {
            snowfallEffect();
        },
        css: "/css/workspace/playground.css",
    },
    "/workspace/explore": {
        js: displayPublicDecks,
        css: "/css/workspace/explore.css",
    },
    "/workspace/review": {
        js: loadAllDecksToReview,
        css: "/css/workspace/review.css",
    },
    "/workspace/decks": { js: null, css: "/css/workspace/decks.css" },
    "/workspace/root": { js: rtDropdowns, css: "/css/workspace/root.css" },
};

/**
 *
 * @param {String} path
 */
async function displayRightSidebar(path) {
    const informationPages = {
        "/workspace/decks": {
            template: "/components/content/card-selection.jsp",
        },
        "/workspace/review": {
            template: "/components/layout/workspace-rightbar.jsp",
        },
    };

    let match = false;
    const sidebarId = document.getElementById("information-container");

    for (const pageKey of Object.keys(informationPages)) {
        if (path.startsWith(pageKey) && !match) {
            sidebarId.setAttribute("active", "");
            match = true;

            // Get the template
            const template = informationPages[pageKey].template;
            const fragment = await fetchPage(template);

            // Clear old content
            while (sidebarId.firstChild) {
                sidebarId.removeChild(sidebarId.firstChild);
            }

            // Append new fragment
            sidebarId.appendChild(fragment);
            sidebarId.style.visibility = "visible";
            break; // stop after the first match
        }
    }

    if (!match) {
        sidebarId.removeAttribute("active");
        sidebarId.style.visibility = "hidden";
    }
}

/**
 * Dynamically load CSS if not already loaded (returns a Promise)
 * @param {String} href
 * @returns {Promise}
 */
export function loadCSS(href) {
    return new Promise((resolve) => {
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
 *
 * @param {String} headerName
 */
function changePageHeader(headerName) {
    headerName = headerName.charAt(0).toUpperCase() + headerName.slice(1);

    const pagePlaceholder = document.querySelector(
        "[current-page-placeholder]"
    );
    if (pagePlaceholder) {
        pagePlaceholder.textContent = headerName;
    }
}

/**
 * Fetch page content (AJAX) and cache as DocumentFragment
 * @param {String} path
 * @param {AbortController} controller
 * @returns {Promise<DocumentFragment|Node|null>}
 */
async function fetchPage(path, controller = null) {
    if (cache.has(path)) return cache.get(path).cloneNode(true);

    try {
        const res = await fetch(path, {
            headers: { "X-Requested-With": "XMLHttpRequest" },
            signal: controller?.signal,
        });

        if (!res.ok) throw new Error("404");

        const html = await res.text();
        const template = document.createElement("template");
        template.innerHTML = html.trim();
        const fragment = template.content;

        cache.set(path, fragment);
        return fragment.cloneNode(true);
    } catch (err) {
        if (err.name === "AbortError") return null;
        console.error(err);
        return null;
    }
}

/**
 * Load a page with AJAX, inject CSS + JS, prevent race conditions
 * @param {String} path
 * @param {Boolean} addHistory
 */
export async function loadPage(path, addHistory = true) {
    // Cancel previous request
    if (currentController) currentController.abort();
    currentController = new AbortController();
    const thisController = currentController;

    const fragment = await fetchPage(path, thisController);
    if (!fragment || thisController !== currentController) return;

    const contentEl = document.getElementById("content");
    if (!contentEl) return;

    // Find matching component for CSS + JS
    const compKey = Object.keys(pageComponents).find((p) => path.startsWith(p));
    let cssPromise = Promise.resolve();
    let jsPromise = Promise.resolve();

    if (compKey) {
        const { js, css } = pageComponents[compKey];
        if (css) cssPromise = loadCSS(css);
        if (js) jsPromise = Promise.resolve().then(() => js(path));
    }

    // Inject content immediately (don’t wait for CSS)
    while (contentEl.firstChild) contentEl.removeChild(contentEl.firstChild);
    contentEl.appendChild(fragment);
    await displayRightSidebar(path);

    // Run CSS + JS in parallel
    Promise.allSettled([cssPromise, jsPromise]);

    // Deck-specific AJAX loader
    if (path.startsWith("/workspace/decks")) {
        if (/^\/workspace\/decks\/?$/.test(path)) deckLoader();
        else if (/^\/workspace\/decks\/[^/]+\/?$/.test(path))
            deckDetailLoader(path);
        else if (/^\/workspace\/decks\/[^/]+\/[^/]+\/?$/.test(path))
            levelDetailLoader(path);
    }

    const match = path.match(/^\/workspace\/([^\/]+)/);
    changePageHeader(match ? match[1] + " Page" : "unknown Page");

    if (addHistory) window.history.pushState({ path }, "", path);
}

// Preload pages on hover
let hoverTimeout;

const navbar = document.getElementById("workspace-sidebar");

navbar.addEventListener("mouseover", (event) => {
    clearTimeout(hoverTimeout);
    hoverTimeout = setTimeout(() => {
        // @ts-ignore
        const target = event.target.closest("a[data-workspace]");
        if (target) {
            const href = target.getAttribute("href");
            if (href && !cache.has(href)) {
                fetchPage(href);
                const comp = Object.keys(pageComponents).find((p) =>
                    href.startsWith(p)
                );
                if (
                    comp &&
                    pageComponents[comp].css &&
                    !cssCache.has(pageComponents[comp].css)
                ) {
                    const preload = document.createElement("link");
                    preload.rel = "preload";
                    preload.as = "style";
                    preload.href = pageComponents[comp].css;
                    document.head.appendChild(preload);
                }
            }
        }
    }, 120); // only trigger if hovered ~0.1s+
});

/**
 *
 * @param {*} path
 */
async function userLoader(path) {
    const displayName = TokenManager.getDisplayName();
    const username = TokenManager.getUsername();

    const temp = document.createElement("div");
    const template = await (await fetch(path)).text();
    temp.innerHTML = template;

    const settingForm = /**@type {HTMLFormElement} */ (
        temp.querySelector("#user-setting")
    );
    const usernameInput = /**@type {HTMLInputElement} */ (
        temp.querySelector("#setting-username")
    );
    const displayNameInput = /**@type {HTMLInputElement} */ (
        temp.querySelector("#setting-display-name")
    );
    const passwordInput = /**@type {HTMLInputElement} */ (
        settingForm.querySelector("#setting-new-password")
    );
    // setting-message
    const sMessage = /**@type {HTMLInputElement} */ (
        settingForm.querySelector("#setting-message")
    );

    const deleteAccBtn = /**@type {HTMLElement} */ (
        settingForm.querySelector("delete-user")
    );

    deleteAccBtn.addEventListener("click", async (e) => {
        const formData = new FormData(settingForm);

        // @ts-ignore
        const data = Object.fromEntries(Array.from(formData.entries()));
        console.log(data);

        const res = await fetchWithAuth("/api/user/deleteUser", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });
        const message = (await res.json()).message;

        if (res.ok) {
            // Update TokenManager with new user data
            TokenManager.setUserData({
                username: usernameInput.value,
                display_name: displayNameInput.value,
            });
            sMessage.textContent = message;
            setTimeout(() => location.reload(), 500);
        } else {
            sMessage.textContent = message;
        }
    });

    usernameInput.value = username;
    displayNameInput.value = displayName;

    settingForm.onsubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(settingForm);
        // Remove unchanged fields from formData
        if (usernameInput.value === username) {
            formData.delete("username");
        }
        if (displayNameInput.value === displayName) {
            formData.delete("displayName");
        }
        if (passwordInput && !passwordInput.value) {
            formData.delete("newPassword");
        }

        // @ts-ignore
        const data = Object.fromEntries(Array.from(formData.entries()));
        console.log(data);

        const res = await fetchWithAuth("/api/user/updateProfile", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });

        const message = (await res.json()).message;

        if (res.ok) {
            // Update TokenManager with new user data
            TokenManager.setUserData({
                username: usernameInput.value,
                display_name: displayNameInput.value,
            });
            sMessage.textContent = message;
            setTimeout(() => location.reload(), 500);
        } else {
            sMessage.textContent = message;
        }
    };

    return temp;
}
document.addEventListener("click", async (event) => {
    const target = event.target;
    if (target instanceof Element) {
        const link = target.closest("a[data-workspace]");
        if (link) {
            event.preventDefault();
            loadPage(
                link.getAttribute("href"),
                document.location.pathname == link.getAttribute("href")
                    ? false
                    : true
            );
        }
        const popup = target.closest("a[sidebar-popup]");
        if (popup) {
            const popupType = popup.getAttribute("popup-type");
            switch (popupType) {
                case "setting":
                    createPopupBox(
                        "60vw",
                        "fit-content",
                        "User Setting : ",
                        await userLoader(
                            "/components/content/popup/setting.jsp"
                        )
                    );
                    break;

                case "session":
                    console.log("SEssion in");

                    createPopupBox(
                        "60vw",
                        "60vh",
                        "Session Setting : ",
                        await userLoader(
                            "/components/content/popup/session.jsp"
                        )
                    );
                    break;
                default:
                    console.error("Invalid popup type.");
                    break;
            }
        }
    }
});
