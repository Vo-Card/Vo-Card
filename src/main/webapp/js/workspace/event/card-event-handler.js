import { createPopupBox, updatePopup } from "../handler/popup-handler.js";
import { loadPage } from "../module/page-manager.js";
import { fetchWithAuth } from "../../auth/auth.js";

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

    // Handle single / double click
    Array.from(
        containerSelector.querySelectorAll(interactableSelector)
    ).forEach((element) => {
        element.addEventListener("click", async (event) => {
            if (clickTimer === null) {
                clickTimer = setTimeout(() => {
                    // Single click logic WIP
                    console.log(
                        "Single click detected:",
                        element.getAttribute("item-id")
                    );
                    clickTimer = null;
                }, doubleClickThreshold);
            } else {
                const itemType = element.getAttribute("item-type");

                clearTimeout(clickTimer);

                console.log(
                    "Double click detected:",
                    element.getAttribute("item-id")
                );

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
        }
    );
}
