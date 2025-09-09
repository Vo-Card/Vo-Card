import { fetchWithAuth } from '/js/auth/auth.js';
import { insertEventActions } from '/js/deckManagement/cardWatcher.js';

const templateCache = new Map();

async function loadTemplate(themeUrl)  {
    if (templateCache.has(themeUrl)) return templateCache.get(themeUrl);

    try {
        const template = await (await fetch(themeUrl)).text();
        templateCache.set(themeUrl, template);
        return template;
    } catch {
        window.location.replace("/login");
    }
}

function createElementFromHTML(html, classes = [], attributes = {}) {
    const el = document.createElement('div');
    el.innerHTML = html;
    classes.filter(cls => cls)
            .forEach(cls => el.classList.add(cls));
    Object.entries(attributes).forEach(([key, value]) => el.setAttribute(key, value));
    return el;
}

async function populateContainer(data, target, type = 'deck', ownership = null) {
    for (const item of data) {
        const template = await loadTemplate(item.theme_url);

        let cardHTML = template
            .replace(/{primary_color}/g, item.primary_color)
            .replace(/{secondary_color}/g, item.secondary_color);

        switch (type) {
            case 'deck':
                cardHTML = cardHTML
                    .replace(/{ii}/g, ownership === "forked" ? "" : "")
                    .replace(/{deck_id}/g, item.deck_id_PK)
                    .replace(/{deck_name}/g, item.deck_name);
                break;
            case 'level':
                cardHTML = cardHTML
                    .replace(/{card_id}/g, item.level_id_PK)
                    .replace(/{top_indicator}/g, item.deck_name)
                    .replace(/{ii}/g, ownership === "forked" ? "" : "")
                    .replace(/{word_content}/g, item.level_name);
                break;
            case 'card':
                cardHTML = cardHTML
                    .replace(/{card_id}/g, item.card_id_PK)
                    .replace(/{top_indicator}/g, item.level_name)
                    .replace(/{ii}/g, ownership === "forked" ? "" : "")
                    .replace(/{word_content}/g, item.card_word);
                break;
            default:
                console.warn('Unknown type:', type);
                break;
        }

        const container = createElementFromHTML(cardHTML, ['card-item-container', `${type}-container-indicator`, 'item-interactable'], {
            'item-id': type === 'deck' ? item.deck_id_PK : type === 'level' ? item.level_id_PK : item.card_id_PK
        });

        const hoverOverlay = document.createElement("div");
        hoverOverlay.className = "hoverOverlay";
        container.appendChild(hoverOverlay);

        target.appendChild(container);
        console.log(item);
    }
}

async function safeFetch(url) {
    try {
        const res = await fetchWithAuth(url);
        if (!res.ok) throw new Error('Unauthorized');
        return await res.json();
    } catch {
        window.location.replace("/login");
    }
}

async function appendTemplate(target, url, id, additionalClass = '') {
    const html = await (await fetch(url, { headers: { "X-Requested-With": "XMLHttpRequest" } })).text();
    const el = createElementFromHTML(html, [additionalClass], { id });
    const overlay = document.createElement("div");
    overlay.className = "hoverOverlay";
    el.appendChild(overlay);
    target.appendChild(el);
}

export async function deckLoader() {
    const forkedDeckContainer = document.getElementById('forked-decks-container');
    const ownedDeckContainer = document.getElementById('owned-decks-container');

    const data = await safeFetch("/api/decks/getDecks");
    if (!data) return;

    await populateContainer(data.forkedDecks, forkedDeckContainer, 'deck', 'forked');
    await populateContainer(data.ownedDecks, ownedDeckContainer, 'deck', 'owned');

    await appendTemplate(forkedDeckContainer, "/components/template/search_decks.svg", "searchDeckButton", "card-item-container");
    await appendTemplate(ownedDeckContainer, "/components/template/create_deck.svg", "createDeckButton", "card-item-container");

    insertEventActions(document, {
        interactableSelector: ".item-interactable, .level-container-indicator",
        rippleSelector: ".card-item-container"
    });
}

export async function deckDetailLoader(path) {
    const levelContainer = document.getElementById("cards-container");
    const match = path.match(/^\/workspace\/decks\/([^/]+)/);
    if (!match) return console.error("Invalid path:", path);

    const data = await safeFetch(`/api/decks/${match[1]}`);
    if (!data) return;

    levelContainer.innerHTML = "";
    if (data.deckLevels) await populateContainer(data.deckLevels, levelContainer, 'level', data.ownership_type);

    await appendTemplate(levelContainer, "/components/template/new_card.svg", null, 'deck-container');

    insertEventActions(document, {
        interactableSelector: ".item-interactable, .level-container-indicator",
        rippleSelector: ".card-item-container",
    });
}

export async function levelDetailLoader(path) {
    const cardContainer = document.getElementById("cards-container");
    const match = path.match(/^\/workspace\/decks\/([^/]+)\/([^/]+)/);
    if (!match) return console.error("Invalid path:", path);

    const data = await safeFetch(`/api/decks/${match[1]}/${match[2]}`);
    if (!data) return;

    cardContainer.innerHTML = "";
    if (data.cards) await populateContainer(data.cards, cardContainer, 'card');

    await appendTemplate(cardContainer, "/components/template/new_card.svg", null, 'deck-container');

    insertEventActions();
}

export async function cardDetailLoader(path) {
    const match = path.match(/^\/workspace\/decks\/([^/]+)\/([^/]+)\/([^/]+)/);
    if (!match) return console.error("Invalid path:", path);

    const data = await safeFetch(`/api/decks/${match[1]}/${match[2]}/${match[3]}`);
    if (!data) return;

    console.log(data);
}