// This module will be handling displaying page metadata

import { loadPage } from "../module/page-manager.js";
import { createConfirmationPopup } from "./popup-handler.js";
import { fetchWithAuth } from "/js/auth/auth.js";

function toDate(arr) {
    // @ts-ignore
    return Array.isArray(arr) ? new Date(...arr) : null;
}

// Create element to display datas
function formatDate(date) {
    // @ts-ignore
    if (!(date instanceof Date) || isNaN(date)) return "";
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const yyyy = date.getFullYear();
    return `${mm}/${dd}/${yyyy}`;
}

/**
 * @param {HTMLElement} container
 * @param {Object} deckData
 */
export function showDeckInformation(
    container,
    deckData,
    target,
    ownershipType
) {
    const dataContainer = container.querySelector("deck-data-container");

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    const dataToStore = {
        "Deck Name": deckData.deck_name,
        "Contained Cards": deckData.deck_contain_card,
        "Deck Description": deckData.deck_description,
        "Created Date": formatDate(toDate(deckData.deck_created_date)),
        "Last Update": formatDate(toDate(deckData.deck_lastest_updated)),
    };
    // "Is Public": deckData.deck_is_public,
    // "Allow Cloning": deckData.deck_clone_perm,

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });

    //hr
    const hr = document.createElement("hr");
    hr.classList.add("my-2", "w-100");
    hr.style.borderTop = "1px solid #ccc";
    dataContainer.append(hr);

    // add check box for public and clone perm
    if (ownershipType === "owned") {
        const publicLabel = document.createElement("label");
        publicLabel.classList.add("d-flex", "w-100");

        const publicCheckbox = document.createElement("input");
        publicCheckbox.type = "checkbox";
        publicCheckbox.checked = deckData.deck_is_public;
        publicCheckbox.classList.add("form-checkbox", "h-5", "w-5");

        publicCheckbox.addEventListener("change", async () => {
            const response = await fetchWithAuth(
                `/api/decks/${deckData.deck_id_PK}/togglePublic`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );
            const data = await response.json();

            if (response.ok) {
                console.log(data.message);
                loadPage(window.location.pathname);
            } else {
                console.error(data.message);
                // Revert the checkbox state on error
                publicCheckbox.checked = !publicCheckbox.checked;
            }
        });

        const publicSpan = document.createElement("span");
        publicSpan.classList.add("w-100", "self-center");
        publicSpan.textContent = "Set Public";
        publicLabel.appendChild(publicSpan);

        dataContainer.appendChild(publicLabel);
        publicLabel.appendChild(publicCheckbox);

        // clone perm
        const cloneLabel = document.createElement("label");
        cloneLabel.classList.add("d-flex", "w-100", "mt-2");

        const cloneCheckbox = document.createElement("input");
        cloneCheckbox.type = "checkbox";
        cloneCheckbox.checked = deckData.deck_clone_perm;
        cloneCheckbox.classList.add("form-checkbox", "h-5", "w-5");

        cloneCheckbox.addEventListener("change", async () => {
            const response = await fetchWithAuth(
                `/api/decks/${deckData.deck_id_PK}/toggleClonePerm`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );
            const data = await response.json();

            if (response.ok) {
                console.log(data.message);
                loadPage(window.location.pathname);
            } else {
                console.error(data.message);
                // Revert the checkbox state on error
                cloneCheckbox.checked = !cloneCheckbox.checked;
            }
        });

        const cloneSpan = document.createElement("span");
        cloneSpan.classList.add("w-100", "self-center");
        cloneSpan.textContent = "Allow Cloning";
        cloneLabel.appendChild(cloneSpan);

        dataContainer.appendChild(cloneLabel);
        cloneLabel.appendChild(cloneCheckbox);
    }

    const actionBtn = document.createElement("button");
    actionBtn.textContent =
        ownershipType === "owned" ? "Delete Deck" : "UnFork Deck";

    actionBtn.classList.add("btn", "btn-danger", "w-100", "mt-2");

    actionBtn.addEventListener("click", () => {
        createConfirmationPopup(
            "50vw",
            "fit-content",
            ownershipType === "owned" ? "Delete Deck" : "UnFork Deck",
            ownershipType === "owned"
                ? "Are you sure you want to delete this deck? This action cannot be undone."
                : "Are you sure you want to unfork this deck? This action cannot be undone.",
            async () => {
                const response = await fetchWithAuth(
                    `/api/decks/${deckData.deck_id_PK}/${
                        ownershipType === "owned" ? "delete" : "unfork"
                    }`,
                    {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    }
                );

                const data = await response.json();

                if (response.ok) {
                    console.log(data.message);
                    loadPage(window.location.pathname);
                } else {
                    console.error(data.message);
                }
            }
        );
    });
    dataContainer.appendChild(actionBtn);

    if (ownershipType === "forked" && deckData.deck_clone_perm) {
        // Add clone button
        const cloneBtn = document.createElement("button");
        cloneBtn.textContent = "Clone Deck";

        cloneBtn.classList.add("btn", "btn-primary", "w-100", "mt-2");

        cloneBtn.addEventListener("click", () => {
            createConfirmationPopup(
                "50vw",
                "fit-content",
                "Clone Deck",
                "Are you sure you want to clone this deck? This will create a copy of the deck in your account.",
                async () => {
                    const response = await fetchWithAuth(
                        `/api/decks/${deckData.deck_id_PK}/clone`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                        }
                    );

                    const data = await response.json();

                    if (response.ok) {
                        console.log(data.message);
                        loadPage(window.location.pathname);
                    } else {
                        console.error(data.message);
                    }
                }
            );
        });
        dataContainer.appendChild(cloneBtn);
    }
}

/**
 * @param {HTMLElement} container
 * @param {Object} levelData
 */
export function showLevelInformation(container, levelData, target) {
    const dataContainer = container.querySelector("deck-data-container");

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    const dataToStore = {
        "Level Value": levelData.level_name,
        "Level Weight": levelData.level_weight,
        "Created Date": formatDate(toDate(levelData.level_created_date)),
        "Contained Cards": levelData.level_contain_card,
    };

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });

    // hr
    const hr = document.createElement("hr");
    hr.classList.add("my-2", "w-100");
    hr.style.borderTop = "1px solid #ccc";
    dataContainer.append(hr);

    //get deck id from url
    const match = window.location.pathname.match(
        /^\/workspace\/decks\/([^/]+)/
    );
    const deckId = match ? match[1] : null;

    // Add delete level button
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete Level";
    deleteBtn.classList.add("btn", "btn-danger", "w-100", "mt-2");

    deleteBtn.addEventListener("click", () => {
        createConfirmationPopup(
            "50vw",
            "fit-content",
            "Delete Level",
            "Are you sure you want to delete this level? This action cannot be undone.",
            async () => {
                const response = await fetchWithAuth(
                    `/api/decks/${deckId}/${levelData.level_id_PK}`,
                    {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    }
                );

                const data = await response.json();

                if (response.ok) {
                    console.log(data.message);
                    loadPage(window.location.pathname);
                } else {
                    console.error(data.message);
                }
            }
        );
    });

    dataContainer.appendChild(deleteBtn);
}

export function showCardInformation(container, cardData, target) {
    const dataContainer = container.querySelector("deck-data-container");

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    const dataToStore = {
        "Card Word": cardData.card_word,
        "Created Date": formatDate(toDate(cardData.card_created_date)),
    };

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });

    // hr
    const hr = document.createElement("hr");
    hr.classList.add("my-2", "w-100");
    hr.style.borderTop = "1px solid #ccc";
    dataContainer.append(hr);

    const match = window.location.pathname.match(
        /^\/workspace\/decks\/([^/]+)\/([^/]+)/
    );
    const deckId = match ? match[1] : null;
    const levelId = match ? match[2] : null;

    // Add delete card button
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete Card";
    deleteBtn.classList.add("btn", "btn-danger", "w-100", "mt-2");

    deleteBtn.addEventListener("click", () => {
        createConfirmationPopup(
            "50vw",
            "fit-content",
            "Delete Card",
            "Are you sure you want to delete this card? This action cannot be undone.",
            async () => {
                const response = await fetchWithAuth(
                    `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}`,
                    {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    }
                );

                const data = await response.json();

                if (response.ok) {
                    console.log(data.message);
                    loadPage(window.location.pathname);
                } else {
                    console.error(data.message);
                }
            }
        );
    });

    dataContainer.appendChild(deleteBtn);
}
