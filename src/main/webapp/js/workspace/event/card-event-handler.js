import {
    closePopup,
    createPopupBox,
    updatePopup,
} from "../handler/popup-handler.js";
import { loadPage } from "../module/page-manager.js";
import { fetchWithAuth } from "../../auth/auth.js";

// Meta data handler
import {
    showCardInformation,
    showDeckInformation,
    showLevelInformation,
} from "/js/workspace/handler/deck-metadata-handler.js";
import { loadTemplate } from "../content/deck-loader.js";

// Cache until update
// @ts-ignore
// @ts-ignore
const contentCatch = new Map();

/**
 *
 * @param {String} path  - path to template file
 * @param {Object} data - object like your Java result (card_word, pos, definitions, etc.)
 * @returns {Promise<HTMLElement>}
 */
async function viewerLoader(path, data) {
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

function createForm(
    inputOverride = "input",
    formElement,
    labelValue,
    inputType = "text",
    inputValue = "",
    inputname,
    attributes = {},
    updateFunction = () => {}
) {
    const div = document.createElement("div");
    const label = document.createElement("label");

    if (labelValue != null) {
        label.textContent = labelValue;
        label.setAttribute("for", inputname);
        div.appendChild(label);
    }

    const input = document.createElement(inputOverride);

    if (inputOverride !== "textarea") {
        // @ts-ignore
        input.type = inputType;
    }

    // @ts-ignore
    input.value = inputValue;
    input.oninput = updateFunction;
    // @ts-ignore
    input.name = inputname;
    input.id = inputname;

    Object.entries(attributes).forEach(([key, value]) => {
        input.setAttribute(key, value);
    });

    div.appendChild(document.createElement("br"));
    div.appendChild(input);
    formElement.appendChild(div);
    return input;
}

async function createLoader(path, itemType) {
    const temp = document.createElement("div");
    const popupTemplate = await (await fetch(path)).text();

    temp.style.height = "100%";
    temp.innerHTML = popupTemplate;

    const inpArea = temp.querySelector("create-input-area");
    const cardDisplay = temp.querySelector(".card-item-container");
    const form = /**@type {HTMLFormElement} */ document.createElement("form");

    const winPath = document.location.pathname;

    const callCreate = (apiEndpoint) => {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(form);
            // @ts-ignore
            const data = Object.fromEntries(Array.from(formData.entries()));
            try {
                const response = await fetchWithAuth(apiEndpoint, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(data),
                });
                if (response.ok) {
                    const message = await response.json();
                    closePopup();
                    console.log(message);
                } else {
                    alert("Failed to update deck.");
                }
            } catch (err) {
                console.error(err);
                alert("Error updating deck.");
            }

            // Refresh the page to show the new data
            loadPage(document.location.pathname);
        });
    };

    switch (itemType) {
        case "Deck":
            const theme = await loadTemplate(
                "/components/template/deck_template.svg"
            );

            // Insert SVG into display
            cardDisplay.innerHTML = theme.replace(/{ii}/g, "");

            // Get all linearGradient elements inside this SVG
            const gradients = cardDisplay.querySelectorAll("linearGradient");

            const svgTitle = cardDisplay.querySelector("#fit-text");
            svgTitle.textContent = "New Deck";
            createForm(
                "input",
                form,
                "Deck Name :",
                "text",
                "New Deck",
                "deckName",
                {},
                (e) => {
                    if (svgTitle) svgTitle.textContent = e.target.value;
                }
            );

            createForm(
                "textarea",
                form,
                "Description Color :",
                "textarea",
                "Description",
                "deckDescription"
            );

            const priColor = createForm(
                "input",
                form,
                "Primary Color :",
                "text",
                "#000000",
                "primaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                // @ts-ignore
                (e) => {
                    gradients.forEach((gradient) => {
                        const stops = gradient.querySelectorAll("stop");
                        stops.forEach((stop, index) => {
                            if (index === 0)
                                // @ts-ignore
                                stop.setAttribute("stop-color", priColor.value);
                        });
                    });
                }
            );

            const secColor = createForm(
                "input",
                form,
                "Secondary Color :",
                "text",
                "#000000",
                "secondaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                // @ts-ignore
                (e) => {
                    gradients.forEach((gradient) => {
                        const stops = gradient.querySelectorAll("stop");
                        stops.forEach((stop, index) => {
                            if (index === 1)
                                // @ts-ignore
                                stop.setAttribute("stop-color", secColor.value);
                        });
                    });
                }
            );

            createForm("input", form, null, "submit", "Create Deck");

            callCreate("/api/decks/create");

            break;

        case "Level":
            const cardId = "level_" + Date.now(); // or UUID
            const levelTheme = (
                await loadTemplate("/components/template/card_template.svg")
            )
                .replace(/{card_id}/g, cardId)
                .replace(/{top_indicator}/g, "Deck's Name")
                .replace(/{ii}/g, "");

            const match = winPath.match(/^\/workspace\/decks\/([^/]+)/);
            if (!match) return temp;

            const deckId = match[1];

            // Insert SVG into display
            cardDisplay.innerHTML = levelTheme;

            // Get all linearGradient elements inside this SVG
            const lev_radgradient = cardDisplay.querySelector("radialGradient");
            const lev_svgPriColor = cardDisplay.querySelector(
                `#primary_indicator_${cardId}`
            );

            const levSvgTitle = cardDisplay.querySelector("#fit-text");
            levSvgTitle.textContent = "New Level";
            createForm(
                "input",
                form,
                "Level Name :",
                "text",
                "New Level",
                "levelValue",
                {},
                (e) => {
                    if (levSvgTitle) levSvgTitle.textContent = e.target.value;
                }
            );

            createForm(
                "input",
                form,
                "Level Weight :",
                "number",
                "0",
                "levelWeight"
            );

            // Primary color
            const levPriColor = createForm(
                "input",
                form,
                "Primary Color :",
                "text",
                "#000000",
                "primaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                (e) => {
                    // @ts-ignore
                    lev_svgPriColor.setAttribute("fill", levPriColor.value);
                }
            );
            // @ts-ignore
            lev_svgPriColor.setAttribute("fill", levPriColor.value);

            // Secondary color
            const levSecColor = createForm(
                "input",
                form,
                "Secondary Color :",
                "text",
                "#000000",
                "secondaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                (e) => {
                    const stops = lev_radgradient.querySelectorAll("stop");
                    // @ts-ignore
                    stops[1].setAttribute("stop-color", levSecColor.value);
                }
            );

            const stops = lev_radgradient.querySelectorAll("stop");
            // @ts-ignore
            stops[1].setAttribute("stop-color", levSecColor.value);

            createForm("input", form, null, "submit", "Create Level");
            callCreate(`/api/decks/${deckId}/create`);

            break;
        case "Card":
            const cardTheme2 = await loadTemplate(
                "/components/template/card_template.svg"
            );

            const cardId2 = "card_" + Date.now(); // or UUID

            const match2 = winPath.match(
                /^\/workspace\/decks\/([^/]+)\/([^/]+)/
            );
            if (!match2) return temp;

            const deckId2 = match2[1];
            const levelId2 = match2[2];

            const container = document.getElementById("cards-container");
            // Insert SVG into display
            cardDisplay.innerHTML = cardTheme2
                .replace(/{card_id}/g, cardId2)
                .replace(/{top_indicator}/g, "Level's Name")
                .replace(/{ii}/g, "")
                .replace(
                    /{primary_color}/g,
                    container.getAttribute("primary-color")
                )
                .replace(
                    /{secondary_color}/g,
                    container.getAttribute("secondary-color")
                );

            const svgTitle2 = cardDisplay.querySelector("#fit-text");
            svgTitle2.textContent = "New Card";
            createForm(
                "input",
                form,
                "Card Word :",
                "text",
                "New Card",
                "cardWord",
                {},
                (e) => {
                    if (svgTitle2) svgTitle2.textContent = e.target.value;
                }
            );

            createForm("input", form, null, "submit", "Create Card");
            callCreate(`/api/decks/${deckId2}/${levelId2}/create`);

            break;
        default:
            console.error("Incorrect type");
            break;
    }
    inpArea.appendChild(form);
    // @ts-ignore
    Coloris.setInstance("[data-coloris]", {
        theme: "polaroid",
        themeMode: "dark",
        alpha: false,
        formatToggle: true,
        swatches: ["#264653", "#2a9d8f", "#e9c46a"],
    });

    return temp;
}

/**
 *
 * @param {*} containerSelector
 * @param {*} options
 */
export function insertDataEventActions(
    containerSelector = document,
    options = {}
) {
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
                    showDeckInformation(
                        selectionContainer,
                        itemData,
                        element,
                        element.getAttribute("owner-type")
                    );
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

/**
 *
 * @param {Document|Element} containerSelector
 * @param {String} interactableSelector
 * @param {String} createType
 */
export function insertCreateEventActions(
    containerSelector = document,
    interactableSelector,
    createType
) {
    const createElem = containerSelector.querySelector(interactableSelector);

    createElem.addEventListener("click", async () => {
        createPopupBox(
            "50vw",
            "50vh",
            "Create " + createType,
            await createLoader(
                "/components/content/popup/create-item.jsp",
                createType
            )
        );
    });
}
