import { loadPage } from "/js/workspace/pageManager.js";

export function insertEventActions() {
    
    const interactables = document.getElementsByClassName("item-interactable");
    const deckContainers = document.getElementsByClassName("deck-container");
    let clickTimer = null;
    const doubleClickThreshold = 250; // milliseconds

    Array.from(interactables).forEach(element => {
        element.addEventListener("click", (event) => {

            if (clickTimer === null) {
                clickTimer = setTimeout(() => {
                    console.log('Single click detected');
                    clickTimer = null; 
                }, doubleClickThreshold);
            } else {
                clearTimeout(clickTimer);
                console.log('Double click detected');
                loadPage(document.location.pathname + '/' + element.getAttribute('item-id'))
                clickTimer = null;
            }
        });
    });

    Array.from(deckContainers).forEach(element => {
        element.addEventListener("click", (event) => {
            const ripple = document.createElement("span");
            ripple.className = "ripple";

            const rect = element.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            ripple.style.width = ripple.style.height = size + "px";
            ripple.style.left = (event.clientX - rect.left - size/2) + "px";
            ripple.style.top = (event.clientY - rect.top - size/2) + "px";

            element.appendChild(ripple);

            ripple.addEventListener("animationend", () => {
                ripple.remove();
            });
        })
    })
}