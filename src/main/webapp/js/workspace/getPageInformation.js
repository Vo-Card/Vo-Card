import { TokenManager, fetchWithAuth } from "/js/auth/auth.js";

//TODO: Make a proper documentation for this file

import { loadPage } from "./module/page-manager.js";

import {} from "./handler/popup-handler.js";

const VIEW_ALL_USER = 1n << 8n;
const VIEW_AUDIT_LOG = 1n << 6n;
const IS_ROOT = 1n << 63n;

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
            console.log("response error : " + response);
            // window.location.replace("/login");
        }
    } catch (error) {
        console.log("fetch failed");
        // window.location.replace("/login");
    }

    const displayName = TokenManager.getDisplayName();

    const usernamePlaceholder = document.querySelector(
        "[username-placeholder]"
    );
    if (usernamePlaceholder) {
        usernamePlaceholder.textContent = displayName;
    }

    // Permission management

    const au = document.getElementById("authority");
    const as = document.getElementById("au-sep");

    const rm = document.getElementById("root-manager");
    const al = document.getElementById("staff-audit-log");
    const mo = document.getElementById("staff-moderation");

    const permission = TokenManager.getPrtmissionBit();

    if ((permission & (IS_ROOT | VIEW_AUDIT_LOG | VIEW_ALL_USER)) != 0n) {
        au.style.display = "block";
        as.style.display = "block";
    }

    if ((permission & IS_ROOT) != 0n) {
        rm.style.display = "block";
        al.style.display = "block";
        mo.style.display = "block";
    } else {
        if ((permission & VIEW_AUDIT_LOG) != 0n) {
            al.style.display = "block";
        }
        if ((permission & VIEW_ALL_USER) != 0n) {
            mo.style.display = "block";
        }
    }

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
