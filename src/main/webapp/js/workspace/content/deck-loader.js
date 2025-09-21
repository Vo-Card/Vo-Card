// @ts-check

import { fetchWithAuth } from "/js/auth/auth.js";
import { insertEventActions } from "../event/card-event-handler.js";

const templateCache = new Map();

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
 *
 * @param {String} html
 * @param {Array} classes
 * @param {Object} attributes
 * @return {HTMLDivElement} the element that have been created
 */
function createElementFromHTML(html, classes = [], attributes = {}) {
    const el = document.createElement("div");
    el.innerHTML = html;
    classes.filter((cls) => cls).forEach((cls) => el.classList.add(cls));
    Object.entries(attributes).forEach(([key, value]) =>
        el.setAttribute(key, value)
    );
    return el;
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
            .replace(/{secondary_color}/g, item.secondary_color);

        switch (type) {
            case "deck":
                cardHTML = cardHTML
                    .replace(
                        /{ii}/g,
                        ownership === "forked"
                            ? ""
                            : ownership === "owned"
                            ? ""
                            : ""
                    )
                    .replace(/{deck_id}/g, item.deck_id_PK)
                    .replace(/{deck_name}/g, item.deck_name);
                break;
            case "level":
                cardHTML = cardHTML
                    .replace(/{card_id}/g, item.level_id_PK)
                    .replace(/{top_indicator}/g, item.deck_name)
                    .replace(
                        /{ii}/g,
                        ownership === "forked"
                            ? ""
                            : ownership === "owned"
                            ? ""
                            : ""
                    )
                    .replace(/{word_content}/g, item.level_name);
                break;
            case "card":
                cardHTML = cardHTML
                    .replace(/{card_id}/g, item.card_id_PK)
                    .replace(/{top_indicator}/g, item.level_name)
                    .replace(
                        /{ii}/g,
                        ownership === "forked"
                            ? ""
                            : ownership === "owned"
                            ? ""
                            : ""
                    )
                    .replace(/{word_content}/g, item.card_word);
                break;
            default:
                console.warn("Unknown type:", type);
                break;
        }

        const container = createElementFromHTML(
            cardHTML,
            [
                "card-item-container",
                `${type}-container-indicator`,
                "item-interactable",
            ],
            {
                "item-id":
                    type === "deck"
                        ? item.deck_id_PK
                        : type === "level"
                        ? item.level_id_PK
                        : item.card_id_PK,
                "item-type": type,
                "item-data": JSON.stringify(item),
            }
        );

        const hoverOverlay = document.createElement("div");
        hoverOverlay.className = "hoverOverlay";
        container.appendChild(hoverOverlay);

        target.appendChild(container);
        console.log(item);
    }
}

/**
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
        console.log("Error Fetching Page");
    }
}

/**
 *
 * @param {HTMLElement} target
 * @param {String} url
 * @param {String} id A string of ID (String because javascript sucks)
 * @param {String} additionalClass
 */
async function appendTemplate(target, url, id, additionalClass = "") {
    const html = await loadTemplate(url);
    const el = createElementFromHTML(html, [additionalClass], { id });
    const overlay = document.createElement("div");
    overlay.className = "hoverOverlay";
    el.appendChild(overlay);
    target.appendChild(el);
}

export async function deckLoader() {
    const forkedDeckContainer = document.getElementById(
        "forked-decks-container"
    );
    const ownedDeckContainer = document.getElementById("owned-decks-container");

    const data = await safeFetch("/api/decks/getDecks");
    console.log(data);
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

    await appendTemplate(
        ownedDeckContainer,
        "/components/template/create_deck.svg",
        "createDeckButton",
        "card-item-container"
    );

    insertEventActions(document, {
        interactableSelector: ".item-interactable, .level-container-indicator",
        rippleSelector: ".card-item-container",
    });
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

    await appendTemplate(
        levelContainer,
        "/components/template/new_card.svg",
        null,
        "card-item-container"
    );

    insertEventActions(document, {
        interactableSelector: ".item-interactable, .level-container-indicator",
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

    await appendTemplate(
        cardContainer,
        "/components/template/new_card.svg",
        null,
        "card-item-container"
    );

    insertEventActions(document, {
        interactableSelector: ".item-interactable, .level-container-indicator",
        rippleSelector: ".card-item-container",
        deckId: match[1],
        levelId: match[2],
    });
}

/**
 *
 */
export async function loadVoteCards() {
    const voteCardContainer = document.getElementById("vote-cards");

    const cards = [
        { id: "vote-fail", url: "/components/template/vote-fail.svg" },
        { id: "vote-hard", url: "/components/template/vote-hard.svg" },
        { id: "vote-good", url: "/components/template/vote-good.svg" },
        { id: "vote-easy", url: "/components/template/vote-easy.svg" },
    ];

    try {
        const responses = await Promise.all(cards.map((c) => fetch(c.url)));

        responses.forEach((res, i) => {
            if (!res.ok)
                throw new Error(
                    `Failed to fetch ${cards[i].url} (${res.status})`
                );
        });

        const svgs = await Promise.all(responses.map((res) => res.text()));

        svgs.forEach((svgText, i) => {
            const el = createElementFromHTML(svgText, ["card-item-container"], {
                "item-id": cards[i].id,
            });
            const overlay = document.createElement("div");
            overlay.className = "hoverOverlay";
            el.appendChild(overlay);
            voteCardContainer.appendChild(el);
        });
        console.log("All cards loaded");
    } catch (err) {
        console.error("Error loading cards:", err);
    }
}

async function showBasicDeckinfomation() {
    const infomationContainer = document.querySelector("deck-data-container");
}
