// This module will be handling displaying page metadata

import { loadPage } from "../module/page-manager.js";
import { editLoader } from "./form-handler.js";
import { createConfirmationPopup, createPopupBox } from "./popup-handler.js";
import { fetchWithAuth, TokenManager } from "/js/auth/auth.js";

// Permission bitmask
const FORCE_DELETE = 1n << 2n;
const FORCE_UPDATE = 1n << 3n;
const IS_ROOT = 1n << 63n;

/**
 *
 * @param {Array} arr
 * @returns
 */
function toDate(arr) {
    // @ts-ignore
    return Array.isArray(arr) ? new Date(...arr) : null;
}

/**
 *
 * @param {Date} date
 * @returns
 */
function formatDate(date) {
    const mm = String(date.getMonth()).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const yyyy = date.getFullYear();
    return `${mm}/${dd}/${yyyy}`;
}

/**
 *
 * @param {HTMLElement} target
 * @param {String} type
 * @param {String|null} infoName
 * @param {String|Array|Boolean} infoData
 */
function createInfo(target, type, infoName, infoData) {
    switch (type) {
        case "description": {
            const desc = document.createElement("p");
            desc.classList.add("description-info");
            desc.textContent = /**@type {String} */ (infoData);
            target.appendChild(desc);
            break;
        }
        case "date": {
            const wraper = document.createElement("div");
            wraper.className = "info-wraper";

            const dateTitle = document.createElement("p");
            dateTitle.textContent = infoName;

            const date = document.createElement("p");
            date.textContent = formatDate(
                toDate(/**@type {Array} */ (infoData))
            );
            date.className = "info-date";
            date.title = toDate(/**@type {Array} */ (infoData)).toDateString();

            wraper.appendChild(dateTitle);
            wraper.appendChild(date);
            target.appendChild(wraper);
            break;
        }
        case "boolean": {
            const wraper = document.createElement("div");
            wraper.className = "info-wraper";

            const boolTitle = document.createElement("p");
            boolTitle.textContent = infoName;

            const bool = document.createElement("p");
            bool.className = `bool-${infoData}`;
            bool.textContent = infoData ? "Yes" : "No";

            wraper.appendChild(boolTitle);
            wraper.appendChild(bool);
            target.appendChild(wraper);
            break;
        }
        case "text": {
            const wraper = document.createElement("div");
            wraper.className = "info-wraper";

            const textTitle = document.createElement("p");
            textTitle.textContent = infoName;

            const text = document.createElement("p");
            text.textContent = /**@type {String} */ (infoData);

            wraper.appendChild(textTitle);
            wraper.appendChild(text);
            target.appendChild(wraper);
            break;
        }
        default:
            break;
    }
}

function addedOwnedButtonActions(
    target,
    type,
    data,
    delText,
    delConfirm,
    delEndPoint,
    editText,
    editTemplate
) {
    const actionWraper = document.createElement("div");
    actionWraper.className = "action-wraper";

    if (!(delConfirm === null || delText === null)) {
        const removeAct = document.createElement("button");
        removeAct.textContent = "Delete";
        removeAct.classList = "btn-warned";

        removeAct.addEventListener("click", () => {
            createConfirmationPopup(
                "50vw",
                "fit-content",
                delText,
                delConfirm,
                async () => {
                    const response = await fetchWithAuth(delEndPoint, {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    });
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
        actionWraper.appendChild(removeAct);
    }

    if (!(editText === null || editTemplate === null)) {
        const editAct = document.createElement("button");
        editAct.textContent = "Edit";
        editAct.classList = "btn-act";

        editAct.addEventListener("click", async () => {
            await createPopupBox(
                "60vw",
                "60vh",
                editText,
                await editLoader(editTemplate, type, data)
            );
        });
        actionWraper.appendChild(editAct);
    }

    target.appendChild(actionWraper);
}

/**
 * @param {HTMLElement} container
 * @param {Object} deckData
 * @param {HTMLElement} target
 * @param {String} ownershipType - "owned" or "forked"
 */
export function showDeckInformation(
    container,
    deckData,
    target,
    ownershipType
) {
    const dataContainer = /**@type {HTMLElement} */ (
        container.querySelector("deck-data-container")
    );

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    dataContainer.innerHTML = "";

    createInfo(dataContainer, "description", null, deckData.deck_description);
    createInfo(
        dataContainer,
        "date",
        "Created Date",
        deckData.deck_created_date
    );
    createInfo(
        dataContainer,
        "date",
        "Last Update",
        deckData.deck_lastest_updated
    );
    createInfo(
        dataContainer,
        "text",
        "Contained",
        `${deckData.deck_contain_card} Cards`
    );
    createInfo(
        dataContainer,
        "boolean",
        "Public Deck",
        deckData.deck_is_public
    );
    createInfo(
        dataContainer,
        "boolean",
        "Allow Cloning",
        deckData.deck_clone_perm
    );

    //hr
    const hr = document.createElement("hr");
    hr.classList = "sep-info";
    dataContainer.append(hr);

    // Check if has permission to force Update Deck
    const permission = TokenManager.getPrtmissionBit();

    if (
        ((permission & IS_ROOT) != 0n ||
            (permission & (FORCE_DELETE | FORCE_UPDATE)) ===
                (FORCE_DELETE | FORCE_UPDATE)) &&
        ownershipType !== "owned"
    ) {
        addedOwnedButtonActions(
            dataContainer,
            "deck",
            deckData,
            "Delete Deck",
            "Are you sure you want to delete this deck? This action cannot be undone.",
            `/api/decks/${deckData.deck_id_PK}/delete`,
            "Deck Information: Edit Mode",
            "/components/content/popup/edit-item.jsp"
        );
    } else if ((permission & FORCE_DELETE) != 0n && ownershipType !== "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "deck",
            deckData,
            "Delete Deck",
            "Are you sure you want to delete this deck? This action cannot be undone.",
            `/api/decks/${deckData.deck_id_PK}/delete`,
            null,
            null
        );
    } else if ((permission & FORCE_UPDATE) != 0n && ownershipType !== "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "deck",
            deckData,
            null,
            null,
            null,
            "Deck Information: Edit Mode",
            "/components/content/popup/edit-item.jsp"
        );
    }

    if (ownershipType === "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "deck",
            deckData,
            "Delete Deck",
            "Are you sure you want to delete this deck? This action cannot be undone.",
            `/api/decks/${deckData.deck_id_PK}/delete`,
            "Deck Information: Edit Mode",
            "/components/content/popup/edit-item.jsp"
        );
    } else {
        const actionWraper = document.createElement("div");
        actionWraper.className = "action-wraper";

        const removeAct = document.createElement("button");
        removeAct.textContent = "UnFork";

        removeAct.classList.add("btn-warned", "w-100");
        removeAct.addEventListener("click", () => {
            createConfirmationPopup(
                "50vw",
                "fit-content",
                "UnFork Deck",
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

        if (ownershipType === "forked" && deckData.deck_clone_perm) {
            removeAct.style.borderRadius = "5px 0 0 5px";

            const cloneBtn = document.createElement("button");
            cloneBtn.textContent = "Clone";
            cloneBtn.classList.add("w-100", "btn-act");
            cloneBtn.style.borderRadius = "0 5px 5px 0";

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
            actionWraper.appendChild(removeAct);
            actionWraper.appendChild(cloneBtn);
        }
        dataContainer.appendChild(actionWraper);
    }
}

/**
 * @param {HTMLElement} container
 * @param {Object} levelData
 * @param {HTMLElement} target
 * @param {String} ownershipType - "owned" or "forked"
 */
export async function showLevelInformation(
    container,
    levelData,
    target,
    ownershipType
) {
    const dataContainer = /**@type {HTMLElement} */ (
        container.querySelector("deck-data-container")
    );

    const match = window.location.pathname.match(
        /^\/workspace\/decks\/([^/]+)/
    );
    const deckId = match ? match[1] : null;

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");
    if (rip != null) rip.remove();

    dataContainer.innerHTML = "";

    createInfo(
        dataContainer,
        "text",
        "Level Difficulty",
        levelData.level_weight
    );
    createInfo(
        dataContainer,
        "date",
        "Created Date",
        levelData.level_created_date
    );
    createInfo(
        dataContainer,
        "text",
        "Contained Cards",
        `${levelData.level_contain_card} Cards`
    );

    // Check if has permission to force Update Deck
    const permission = TokenManager.getPrtmissionBit();

    if (
        ((permission & IS_ROOT) != 0n ||
            (permission & (FORCE_DELETE | FORCE_UPDATE)) ===
                (FORCE_DELETE | FORCE_UPDATE)) &&
        ownershipType !== "owned"
    ) {
        addedOwnedButtonActions(
            dataContainer,
            "level",
            levelData,
            "Delete Level",
            "Are you sure you want to delete this level? This action cannot be undone.",
            `/api/decks/${deckId}/${levelData.level_id_PK}/delete`,
            "Level Information: Edit Mode",
            "/components/content/popup/edit-item.jsp"
        );
    } else if ((permission & FORCE_DELETE) != 0n && ownershipType !== "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "level",
            levelData,
            "Delete Level",
            "Are you sure you want to delete this level? This action cannot be undone.",
            `/api/decks/${deckId}/${levelData.level_id_PK}/delete`,
            null,
            null
        );
    } else if ((permission & FORCE_UPDATE) != 0n && ownershipType !== "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "level",
            levelData,
            null,
            null,
            null,
            "Level Information: Edit Mode",
            "/components/content/popup/edit-item.jsp"
        );
    }

    if (ownershipType === "owned") {
        const hr = document.createElement("hr");
        hr.className = "sep-info";
        dataContainer.append(hr);

        // Add delete level button
        addedOwnedButtonActions(
            dataContainer,
            "level",
            levelData,
            "Delete Level",
            "Are you sure you want to delete this level? This action cannot be undone.",
            `/api/decks/${deckId}/${levelData.level_id_PK}/delete`,
            "Level Information: Edit Mode",
            "/components/content/popup/edit-item.jsp"
        );
    }
}

/**
 *
 * @param {HTMLElement} container
 * @param {Object} cardData
 * @param {HTMLElement} target
 * @param {String} ownershipType
 */
export async function showCardInformation(
    container,
    cardData,
    target,
    ownershipType
) {
    const dataContainer = /**@type {HTMLElement} */ (
        container.querySelector("deck-data-container")
    );

    const sel = container.querySelector("selection");

    const match = window.location.pathname.match(
        /^\/workspace\/decks\/([^/]+)\/([^/]+)/
    );
    const deckId = match ? match[1] : null;
    const levelId = match ? match[2] : null;

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    dataContainer.innerHTML = "";

    createInfo(dataContainer, "text", "Word", cardData.card_word);
    createInfo(
        dataContainer,
        "date",
        "Created Date",
        cardData.card_created_date
    );

    // Check if has permission to force Update Deck
    const permission = TokenManager.getPrtmissionBit();

    if (
        ((permission & IS_ROOT) != 0n ||
            (permission & (FORCE_DELETE | FORCE_UPDATE)) ===
                (FORCE_DELETE | FORCE_UPDATE)) &&
        ownershipType !== "owned"
    ) {
        addedOwnedButtonActions(
            dataContainer,
            "card",
            await (
                await fetchWithAuth(
                    `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}`
                )
            ).json(),
            "Delete Card",
            "Are you sure you want to delete this card? This action cannot be undone.",
            `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}/delete`,
            "Card Information: Edit Mode",
            "/components/content/popup/card-edit.jsp"
        );
    } else if ((permission & FORCE_DELETE) != 0n && ownershipType !== "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "card",
            await (
                await fetchWithAuth(
                    `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}`
                )
            ).json(),
            "Delete Card",
            "Are you sure you want to delete this card? This action cannot be undone.",
            `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}/delete`,
            null,
            null
        );
    } else if ((permission & FORCE_UPDATE) != 0n && ownershipType !== "owned") {
        addedOwnedButtonActions(
            dataContainer,
            "card",
            await (
                await fetchWithAuth(
                    `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}`
                )
            ).json(),
            null,
            null,
            null,
            "Card Information: Edit Mode",
            "/components/content/popup/card-edit.jsp"
        );
    }

    if (ownershipType === "owned") {
        const hr = document.createElement("hr");
        hr.className = "sep-info";
        dataContainer.append(hr);

        addedOwnedButtonActions(
            dataContainer,
            "card",
            await (
                await fetchWithAuth(
                    `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}`
                )
            ).json(),
            "Delete Card",
            "Are you sure you want to delete this card? This action cannot be undone.",
            `/api/decks/${deckId}/${levelId}/${cardData.card_id_PK}/delete`,
            "Card Information: Edit Mode",
            "/components/content/popup/card-edit.jsp"
        );
    }
}
