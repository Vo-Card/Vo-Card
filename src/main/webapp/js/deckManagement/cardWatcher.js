import { loadPage } from "/js/workspace/pageManager.js";

/**
 * 
 * @param {*} containerSelector 
 * @param {*} options 
 */
export function insertEventActions(containerSelector = document, options = {}) {
    const interactableSelector = options.interactableSelector || ".item-interactable";
    const rippleSelector = options.rippleSelector || ".deck-container";
    const doubleClickThreshold = options.doubleClickThreshold || 250;

    let clickTimer = null;

    // Handle single / double click
    Array.from(containerSelector.querySelectorAll(interactableSelector)).forEach(element => {
        
        element.addEventListener("click", (event) => {
            if (clickTimer === null) {
                clickTimer = setTimeout(() => {
                    // Single click logic WIP
                    console.log("Single click detected:", element.getAttribute("item-id"));
                    clickTimer = null;
                }, doubleClickThreshold);
            } else {
                clearTimeout(clickTimer);
                console.log("Double click detected:", element.getAttribute("item-id"));
                loadPage(document.location.pathname + "/" + element.getAttribute("item-id"));
                clickTimer = null;
            }
        });
    });

    // Handle ripple effect
    Array.from(containerSelector.querySelectorAll(rippleSelector)).forEach(element => {
        element.addEventListener("click", (event) => {
            const ripple = document.createElement("span");
            ripple.className = "ripple";

            const rect = element.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            ripple.style.width = ripple.style.height = size + "px";
            ripple.style.left = `${event.clientX - rect.left - size / 2}px`;
            ripple.style.top = `${event.clientY - rect.top - size / 2}px`;

            element.appendChild(ripple);

            ripple.addEventListener("animationend", () => ripple.remove());
        });
    });
}
