import { createPopupBox, updatePopup } from "../handler/popup-handler.js";
import { loadPage } from "../module/page-manager.js";
import { fetchWithAuth } from "../../auth/auth.js";
import { loadTemplate } from "../content/deck-loader.js";

// Cache until update
const contentCatch = new Map();

/**
 *
 * @param {String} path  - path to template file
 * @param {Object} data - object like your Java result (card_word, pos, definitions, etc.)
 * @returns {Promise<HTMLElement>}
 */
async function viewerLoader(path, data) {
    console.log(data);

    const cardData = data.card_data;
    const temp = document.createElement("div");
    const template = await (await fetch(path)).text();
    temp.innerHTML = template;

    // Fill top-level fields
    temp.querySelector("[card-word]").textContent = cardData.card_word ?? "";
    temp.querySelector("[card-level]").textContent = cardData.level_name ?? "";
    temp.querySelector("[is-owned]").textContent =
        data.ownership_type ?? "Unknown";

    // Definitions
    const defContainer = temp.querySelector("[definition-container]");
    if (cardData.pos) {
        cardData.pos.forEach((pos) => {
            const details = document.createElement("details");
            const summary = document.createElement("summary");
            summary.classList.add("part-of-speech");
            summary.textContent = pos.part_of_speech;
            details.appendChild(summary);

            const ol = document.createElement("ol");
            ol.setAttribute("part-of-speech", pos.pos_id_PK);

            pos.definitions.forEach((def) => {
                const li = document.createElement("li");
                li.classList.add("definition");
                li.textContent = def.definition;
                li.dataset.defId = def.definition_id_PK;
                ol.appendChild(li);
                details.appendChild(ol);
            });
            defContainer.append(details);
        });
    }

    const EContent =
        /**@type {HTMLElement} */ temp.querySelector("[edit-content]");
    if (data.ownership_type != "owned") {
        EContent.remove();
    } else {
        EContent.addEventListener("click", async () => {
            updatePopup(
                "60vw",
                "60vh",
                "Card Information: Edit Mode",
                await editLoader(
                    "/components/content/popup/card-edit.jsp",
                    data
                )
            );
        });
    }

    return temp;
}

/**
 *
 * @param {String} path  - path to template file
 * @param {Object} data - object like your Java result (card_word, pos, definitions, etc.)
 * @returns {Promise<HTMLElement>}
 */
async function editLoader(path, data) {
    console.log(data);

    const cardData = data.card_data;
    const temp = document.createElement("div");
    const template = await (await fetch(path)).text();
    temp.innerHTML = template;

    // Fill top-level fields
    temp.querySelector("[card-word]").textContent = cardData.card_word ?? "";
    temp.querySelector("[is-owned]").textContent =
        data.ownership_type ?? "Unknown";
    temp.querySelector("[card-level]").textContent = cardData.level_name ?? "";

    // Definitions
    const defContainer = temp.querySelector("[definition-container]");
    if (cardData.pos) {
        cardData.pos.forEach((pos) => {
            const details = document.createElement("details");
            const summary = document.createElement("summary");
            summary.classList.add("part-of-speech");
            summary.textContent = pos.part_of_speech;
            details.appendChild(summary);

            const ol = document.createElement("ol");
            ol.setAttribute("part-of-speech", pos.pos_id_PK);

            pos.definitions.forEach((def) => {
                const li = document.createElement("li");
                li.classList.add("definition");
                li.textContent = def.definition;
                li.dataset.defId = def.definition_id_PK;
                ol.appendChild(li);
                details.appendChild(ol);
            });
            defContainer.append(details);
        });
    }

    const EContent =
        /**@type {HTMLElement} */ temp.querySelector("[edit-content]");
    if (data.ownership_type != "owned") {
        EContent.remove();
    }

    return temp;
}

/**
 *
 * @param {*} containerSelector
 * @param {*} options
 */
export function insertEventActions(containerSelector = document, options = {}) {
    const interactableSelector =
        options.interactableSelector || ".item-interactable";
    const rippleSelector = options.rippleSelector || ".deck-container";
    const doubleClickThreshold = options.doubleClickThreshold || 250;

    const deckId = options.deckId;
    const levelId = options.levelId;

    let clickTimer = null;
    let clickedElement = null;
    // Handle single / double click
    Array.from(
        containerSelector.querySelectorAll(interactableSelector)
    ).forEach((element) => {
        element.addEventListener("click", async () => {
            const itemType = element.getAttribute("item-type");
            const itemData = JSON.parse(element.getAttribute("item-data"));
            const selectionContainer = document.getElementById(
                "card-selection-container"
            );

            switch (itemType) {
                case "deck":
                    showDeckInformation(selectionContainer, itemData, element);
                    break;
                case "level":
                    showLevelInformation(selectionContainer, itemData, element);
                    break;
                case "card":
                    showCardInformation(selectionContainer, itemData, element);
                    break;
                default:
                    console.error(
                        "Failed to load the metadata type of : " + itemType
                    );
                    break;
            }

            if (clickTimer === null) {
                clickedElement = element;
                clickTimer = setTimeout(async () => {
                    clickTimer = null;
                }, doubleClickThreshold);
            } else {
                if (clickedElement != element) {
                    clearTimeout(clickTimer);
                    clickTimer = null;
                    return;
                }

                clearTimeout(clickTimer);

                if (itemType != "card") {
                    loadPage(
                        document.location.pathname +
                            "/" +
                            element.getAttribute("item-id")
                    );
                } else if (deckId != null && levelId != null) {
                    const cardId = element.getAttribute("item-id");
                    const dataResponse = await fetchWithAuth(
                        `/api/decks/${deckId}/${levelId}/${cardId}`
                    );

                    const data = await dataResponse.json();
                    console.log(data);
                    createPopupBox(
                        "60vw",
                        "60vh",
                        "Card Information: View Mode",
                        await viewerLoader(
                            "/components/content/popup/card-detail.jsp",
                            data
                        )
                    );
                }
                clickTimer = null;
            }
        });
    });

    // Handle ripple effect
    Array.from(containerSelector.querySelectorAll(rippleSelector)).forEach(
        (element) => {
            element.addEventListener(
                "click",
                /**
                 *
                 * @param {MouseEvent} event
                 */
                (event) => {
                    const ripple = document.createElement("span");
                    ripple.className = "ripple";

                    const rect = element.getBoundingClientRect();
                    const size = Math.max(rect.width, rect.height);
                    ripple.style.width = ripple.style.height = size + "px";
                    ripple.style.left = `${
                        event.clientX - rect.left - size / 2
                    }px`;
                    ripple.style.top = `${
                        event.clientY - rect.top - size / 2
                    }px`;

                    element.appendChild(ripple);

                    ripple.addEventListener("animationend", () =>
                        ripple.remove()
                    );
                }
            );
        }
    );
}

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
async function showDeckInformation(container, deckData, target) {
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
        "Is Public": deckData.deck_is_public,
        "Allow Cloning": deckData.deck_clone_perm,
    };

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });
}

/**
 * @param {HTMLElement} container
 * @param {Object} levelData
 */
function showLevelInformation(container, levelData, target) {
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
}

function showCardInformation(container, cardData, target) {
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
}
