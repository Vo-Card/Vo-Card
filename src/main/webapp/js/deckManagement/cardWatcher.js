import { createPopupBox } from "/js/workspace/popupManager.js";
import { loadPage } from "/js/workspace/pageManager.js";
import { fetchWithAuth } from "../auth/auth.js";

// Cache until update
const contentCatch = new Map();

/**
 *
 * @param {String} path  - path to template file
 * @param {Object} data - object like your Java result (card_word, pos, definitions, etc.)
 * @returns {Promise<HTMLElement>}
 */
async function templateLoader(path, data) {
    const cardData = data.card_data;
    const temp = document.createElement("div");
    const template = await (await fetch(path)).text();
    temp.innerHTML = template;

    // Fill top-level fields
    temp.querySelector("[card-word]").textContent = cardData.card_word ?? "";
    temp.querySelector("[is-owned]").textContent = cardData.is_owned ?? "false";
    temp.querySelector("[card-level]").textContent = cardData.level_name ?? "";

    // POS buttons
    const posBox = temp.querySelector("[post-selector]");
    if (cardData.pos && cardData.pos.length > 0) {
        cardData.pos.forEach((pos) => {
            const btn = document.createElement("button");
            btn.setAttribute("part-of-speech-selector", "");
            btn.textContent = pos.part_of_speech;
            btn.dataset.posId = pos.pos_id_PK;
            posBox.appendChild(btn);
        });
    }

    // Definitions
    const defContainer = temp.querySelector("[definition-container]");
    if (cardData.pos) {
        cardData.pos.forEach((pos) => {
            const div = document.createElement("div");
            div.setAttribute("part-of-speech", pos.pos_id_PK);
            pos.definitions.forEach((def) => {
                const p = document.createElement("p");
                p.classList.add("definition");
                p.textContent = def.definition;
                p.dataset.defId = def.definition_id_PK;
                defContainer.appendChild(p);
            });
        });
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
                        await templateLoader(
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
