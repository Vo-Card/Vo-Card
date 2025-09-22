// This is a module Script

const backdrop = document.getElementById("backdrop");
const popupBox = document.getElementById("popup-container");
const contentContainer = document.getElementById("popup-content-container");
const popupTitleBox = document.getElementById("popup-title-text");

// Contain popup metadata
const popupOrder = [];

/**
 *
 * @param {String} width
 * @param {String} height
 * @param {String} title
 * @param {Element} contentElement
 */
export async function createPopupBox(width, height, title, contentElement) {
    backdrop.classList.add("active");

    popupTitleBox.textContent = title;

    popupBox.style.width = width;
    popupBox.style.height = height;

    contentContainer.appendChild(contentElement);
}

export async function updatePopup(width, height, title, contentElement) {
    popupTitleBox.textContent = title;

    popupBox.style.width = width;
    popupBox.style.height = height;

    contentContainer.innerHTML = "";
    contentContainer.appendChild(contentElement);
}

// Javasceipt was tweaking with me so i had to come up with this

let mouseDownOnBackdrop = false;

backdrop.addEventListener("mousedown", (e) => {
    if (e.target === backdrop) {
        mouseDownOnBackdrop = true;
    }
});

backdrop.addEventListener("mouseup", (e) => {
    if (
        mouseDownOnBackdrop &&
        e.target === backdrop &&
        popupOrder.length === 0
    ) {
        backdrop.classList.remove("active");
        popupBox.removeAttribute("style");
        contentContainer.innerHTML = "";
    }
    popupOrder.pop();
    mouseDownOnBackdrop = false;
});

export function closePopup() {
    backdrop.classList.remove("active");
    popupBox.removeAttribute("style");
    contentContainer.innerHTML = "";
    popupOrder.length = 0;
    mouseDownOnBackdrop = false;
}
