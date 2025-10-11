// @ts-check

import { fetchWithAuth, TokenManager } from "/js/auth/auth.js";
import {
    insertCreateEventActions,
    insertDataEventActions,
} from "../event/card-event-handler.js";
import { cardSessionPlay } from "../review.js";
import { loadPage } from "../module/page-manager.js";

const templateCache = new Map();

const FORCE_CREATE = 1n << 4n;
const IS_ROOT = 1n << 63n;

/* ------------- */
/*   Utilities   */
/* ------------- */

/**
 * Load the template from the given URL
 *
 * If there is no template with the URL, load the template from the given url,
 * and if failed sent the user back to login page(placeholder)
 *
 * If the URL exist in `templateCache` then use the template instead of fetch it again.
 *
 * @param {String} themeUrl
 * @returns {Promise<String>} template HTML text
 */
export async function loadTemplate(themeUrl) {
    if (templateCache.has(themeUrl)) return templateCache.get(themeUrl);

    try {
        const template = await (await fetch(themeUrl)).text();
        templateCache.set(themeUrl, template);
        return template;
    } catch {
        console.error("Error loading template");
    }
}

/**
 *
 * @param {String} elementType
 * @param {String} html
 * @param {Array} classes
 * @param {Object} attributes
 * @return {HTMLElement} the element that have been created
 */
function createElementFromHTML(
    elementType,
    html,
    classes = [],
    attributes = {}
) {
    const el = document.createElement(elementType);
    el.innerHTML = html;
    classes.filter((cls) => cls).forEach((cls) => el.classList.add(cls));
    Object.entries(attributes).forEach(([key, value]) =>
        el.setAttribute(key, value)
    );
    return el;
}

/**
 * Fetch the API using auth with error handling
 *
 * @param {String} url
 * @returns {Promise<Object>}
 */
async function safeFetch(url) {
    try {
        const res = await fetchWithAuth(url);
        if (!res.ok) throw new Error("Unauthorized");
        return await res.json();
    } catch {
        // Currently debug
        console.error("Error Fetching Page");
    }
}

/**
 * Append the HTML template using link
 *
 * @param {HTMLElement} target
 * @param {String} url
 * @param {String} id A string of ID (String because javascript sucks)
 * @param {String} additionalClass
 */
export async function appendTemplate(target, url, id, additionalClass = "") {
    const html = await loadTemplate(url);
    const el = createElementFromHTML("div", html, [additionalClass], { id });
    const overlay = document.createElement("div");
    overlay.className = "hoverOverlay";
    el.appendChild(overlay);
    target.appendChild(el);
}

/**
 *
 * @param {Object} data
 * @param {HTMLElement} target
 * @param {String} type
 * @param {String} ownership
 */
export async function populateContainer(
    data,
    target,
    type = "deck",
    ownership = null
) {
    for (const item of data) {
        const template = await loadTemplate(item.theme_url);

        let cardHTML = template
            .replace(/{primary_color}/g, item.primary_color)
            .replace(/{secondary_color}/g, item.secondary_color)
            .replace(
                /{ii}/g,
                ownership === "forked"
                    ? ""
                    : ownership === "owned"
                    ? ""
                    : ownership === "public"
                    ? ""
                    : ""
            );
        switch (type) {
            case "deck":
                cardHTML = cardHTML
                    .replace(/{deck_id}/g, item.deck_id_PK)
                    .replace(/{deck_name}/g, item.deck_name);
                break;
            case "level":
                cardHTML = cardHTML
                    .replace(/{card_id}/g, item.level_id_PK)
                    .replace(/{top_indicator}/g, item.deck_name)
                    .replace(/{word_content}/g, item.level_name);
                break;
            case "card":
                cardHTML = cardHTML
                    .replace(/{card_id}/g, item.card_id_PK)
                    .replace(/{top_indicator}/g, item.level_name)
                    .replace(/{word_content}/g, item.card_word);
                break;
            default:
                console.warn("Unknown type:", type);
                break;
        }

        const container = createElementFromHTML(
            "div",
            cardHTML,
            ["card-item-container", "item-interactable"],
            {
                "item-id":
                    type === "deck"
                        ? item.deck_id_PK
                        : type === "level"
                        ? item.level_id_PK
                        : item.card_id_PK,
                "item-type": type,
                "item-data": JSON.stringify(item),
                "owner-type": ownership != null ? ownership : "Unknown",
            }
        );

        const hoverOverlay = document.createElement("div");
        hoverOverlay.className = "hoverOverlay";
        container.appendChild(hoverOverlay);

        target.appendChild(container);
    }
}

export async function deckLoader() {
    const forkedDeckContainer = document.getElementById(
        "forked-decks-container"
    );
    const ownedDeckContainer = document.getElementById("owned-decks-container");

    const data = await safeFetch("/api/decks");
    if (!data) return;

    await populateContainer(
        data.forkedDecks,
        forkedDeckContainer,
        "deck",
        "forked"
    );
    await populateContainer(
        data.ownedDecks,
        ownedDeckContainer,
        "deck",
        "owned"
    );

    await appendTemplate(
        forkedDeckContainer,
        "/components/template/search_decks.svg",
        "searchDeckButton",
        "card-item-container"
    );

    forkedDeckContainer
        .querySelector("#searchDeckButton")
        .addEventListener("click", () => {
            loadPage("/workspace/explore");
        });

    await appendTemplate(
        ownedDeckContainer,
        "/components/template/create_deck.svg",
        "createDeckButton",
        "card-item-container"
    );

    insertDataEventActions(document.getElementById("loadDecks"), {
        interactableSelector: ".item-interactable",
        rippleSelector: ".card-item-container",
    });

    insertCreateEventActions(
        document.getElementById("loadDecks"),
        "#createDeckButton",
        "Deck"
    );
}

/**
 *
 * @param {String} path
 * @returns
 */
export async function deckDetailLoader(path) {
    const levelContainer = document.getElementById("cards-container");
    const match = path.match(/^\/workspace\/decks\/([^/]+)/);
    if (!match) return console.error("Invalid path:", path);

    const data = await safeFetch(`/api/decks/${match[1]}`);
    if (!data) return;

    levelContainer.innerHTML = "";
    if (data.deckLevels)
        await populateContainer(
            data.deckLevels,
            levelContainer,
            "level",
            data.ownership_type
        );

    const permission = TokenManager.getPrtmissionBit();

    if (
        data.ownership_type === "owned" ||
        (permission & (FORCE_CREATE | IS_ROOT)) != 0n
    ) {
        await appendTemplate(
            levelContainer,
            "/components/template/new_card.svg",
            "create-level-card",
            "card-item-container"
        );

        insertCreateEventActions(levelContainer, "#create-level-card", "Level");
    }

    insertDataEventActions(levelContainer, {
        interactableSelector: ".item-interactable",
        rippleSelector: ".card-item-container",
    });
}

/**
 *
 * @param {String} path
 * @returns
 */
export async function levelDetailLoader(path) {
    const cardContainer = document.getElementById("cards-container");
    const match = path.match(/^\/workspace\/decks\/([^/]+)\/([^/]+)/);
    if (!match) return console.error("Invalid path:", path);

    const data = await safeFetch(`/api/decks/${match[1]}/${match[2]}`);
    if (!data) return;

    cardContainer.innerHTML = "";
    if (data.cards)
        await populateContainer(
            data.cards,
            cardContainer,
            "card",
            data.ownership_type
        );
    const permission = TokenManager.getPrtmissionBit();

    if (
        data.ownership_type === "owned" ||
        (permission & (FORCE_CREATE | IS_ROOT)) != 0n
    ) {
        await appendTemplate(
            cardContainer,
            "/components/template/new_card.svg",
            "create-card",
            "card-item-container"
        );
        insertCreateEventActions(cardContainer, "#create-card", "Card");
    }

    cardContainer.setAttribute("primary-color", data.theme.primary_color);
    cardContainer.setAttribute("secondary-color", data.theme.secondary_color);

    insertDataEventActions(cardContainer, {
        interactableSelector: ".item-interactable",
        rippleSelector: ".card-item-container",
        deckId: match[1],
        levelId: match[2],
    });
}

export async function loadAllDecksToReview() {
    const deckContainer = document.getElementById("decks-container");
    const selectedContainer = document.querySelector("selected-card-container");
    const data = await safeFetch("/api/decks");
    if (!data) return;

    await populateContainer(data.forkedDecks, deckContainer, "deck", "forked");
    await populateContainer(data.ownedDecks, deckContainer, "deck", "owned");

    const selectedDecks = new Set();
    const maxDecks = 10;

    const selectedDeckDisplay = document.querySelector("selected-decks");
    const configForm = /**@type {HTMLFormElement} */ document.getElementById(
        "workspace-right-bar"
    );

    configForm.addEventListener("submit", (e) => {
        e.preventDefault();
        // @ts-ignore
        const formData = new FormData(configForm);
        // @ts-ignore
        const data = Object.fromEntries(Array.from(formData.entries()));
        if (selectedDecks.size >= 1)
            cardSessionPlay(
                Array.from(selectedDecks),
                // @ts-ignore
                data["total-session"],
                // @ts-ignore
                data["timelimit"] * 60
            );
    });

    const handleClick = (event) => {
        const element = event.target.closest(".card-item-container");
        if (!element) return;

        if (deckContainer.contains(element) && selectedDecks.size < maxDecks) {
            selectedContainer.appendChild(element);
            selectedDecks.add(element.getAttribute("item-id"));
        } else if (selectedContainer.contains(element)) {
            deckContainer.appendChild(element);
            selectedDecks.delete(element.getAttribute("item-id"));
        }
        selectedDeckDisplay.textContent = selectedDecks.size.toString();
    };

    deckContainer.addEventListener("click", handleClick);
    selectedContainer.addEventListener("click", handleClick);
}
