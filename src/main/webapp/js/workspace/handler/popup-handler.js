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

export async function createConfirmationPopup(
    width,
    height,
    title,
    message,
    onConfirm
) {
    const contentElement = document.createElement("div");
    contentElement.classList.add(
        "d-flex",
        "flex-column",
        "h-full",
        "p-4",
        "justify-content-between"
    );

    const messageBox = document.createElement("p");
    messageBox.textContent = message;
    messageBox.classList.add("flex-grow", "mb-4", "text-center");

    const buttonContainer = document.createElement("div");
    buttonContainer.classList.add(
        "d-flex",
        "justify-content-between",
        "space-x-2",
        "mt-auto"
    );

    const cancelButton = document.createElement("button");
    cancelButton.textContent = "Cancel";
    cancelButton.classList.add("btn", "btn-secondary");
    cancelButton.addEventListener("click", () => {
        closePopup();
    });

    const confirmButton = document.createElement("button");
    confirmButton.textContent = "Confirm";
    confirmButton.classList.add("btn", "btn-primary");
    confirmButton.addEventListener("click", () => {
        onConfirm();
        closePopup();
    });

    buttonContainer.appendChild(cancelButton);
    buttonContainer.appendChild(confirmButton);

    contentElement.appendChild(messageBox);
    contentElement.appendChild(buttonContainer);

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
