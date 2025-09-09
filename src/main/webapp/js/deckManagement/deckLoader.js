import { fetchWithAuth } from '/js/auth/auth.js';

import { insertEventActions } from '/js/deckManagement/cardWatcher.js';

const templateCache = new Map();

async function loadTemplate(themeUrl) {
    if (templateCache.has(themeUrl))
        return templateCache.get(themeUrl)

    try {
        const templateJson = await fetch(themeUrl);
        const template = await templateJson.text();
        templateCache.set(themeUrl, template);
        return template;
    } catch (err) {
        window.location.replace("/login");
    }
}

async function insertDeckContainer(data, target) {
    for (let i = 0; i < data.length; i++) {
        console.log(data)
        const template = await loadTemplate(data[i]["theme_url"])

        let cardHTML = template
            .replace(/{primary_color}/g, data[i]["primary_color"])
            .replace(/{secondary_color}/g, data[i]["secondary_color"])
            .replace(/{deck_id}/g, data[i]["deck_id_PK"]);

        const wrapDeck = document.createElement("div");
        wrapDeck.innerHTML = cardHTML;
        wrapDeck.classList.add('deck-container', "item-interactable");
        wrapDeck.setAttribute('item-id', data[i]["deck_id_PK"]);
        wrapDeck.style.position = "relative";

        const hoverOverlay = document.createElement("div")
        hoverOverlay.className = "hoverOverlay"
        hoverOverlay.style.width = "100%";
        hoverOverlay.style.height = "100%";

        const deckName = document.createElement("p");
        deckName.textContent = data[i]["deck_name"];
        deckName.className = 'deck-name'
        deckName.style.position = "absolute";
        deckName.style.bottom = "-5%";
        deckName.style.left = "50%";
        deckName.style.transform = "translate(-50%, -50%)";
        deckName.style.color = "white";
        deckName.style.fontWeight = "bold";
        deckName.style.pointerEvents = "auto";

        wrapDeck.appendChild(hoverOverlay)
        wrapDeck.appendChild(deckName);
        target.appendChild(wrapDeck)

        console.log(data[i]);
    }
}

async function insertCardContainer(data, target, id, name) {
    for (let i = 0; i < data.length; i++) {

        const template = await loadTemplate(data[i]["theme_url"])

        let cardHTML = template
            .replace(/{primary_color}/g, data[i]["primary_color"])
            .replace(/{secondary_color}/g, data[i]["secondary_color"])
            .replace(/{card_id}/g, data[i][id]);

        const wrapDeck = document.createElement("div");
        wrapDeck.innerHTML = cardHTML;
        wrapDeck.classList.add('deck-container', "item-interactable");
        wrapDeck.setAttribute('item-id', data[i][id]);
        wrapDeck.style.position = "relative";

        const hoverOverlay = document.createElement("div")
        hoverOverlay.className = "hoverOverlay"
        hoverOverlay.style.width = "100%";
        hoverOverlay.style.height = "100%";

        const deckName = document.createElement("p");
        deckName.textContent = data[i][name];
        deckName.className = 'deck-name'
        deckName.style.position = "absolute";
        deckName.style.margin = "0";
        deckName.style.color = "white";
        deckName.style.fontWeight = "bold";
        deckName.style.pointerEvents = "auto";
        deckName.style.fontSize = "35px";

        wrapDeck.appendChild(hoverOverlay)
        wrapDeck.appendChild(deckName);
        target.appendChild(wrapDeck)

        console.log(data[i]);
    }

}


export async function deckLoader() {
    const forkedDeckContainer = document.getElementById('forked-decks-container')
    const ownedDeckContainer = document.getElementById('owned-decks-container')

    try {
        const response = await fetchWithAuth("/api/decks/getDecks");

        const searchDecks = await fetch("/components/template/search_decks.svg", { headers: { "X-Requested-With": "XMLHttpRequest" } });
        const createDecks = await fetch("/components/template/create_deck.svg", { headers: { "X-Requested-With": "XMLHttpRequest" } });
        if (response.ok) {

            const data = await response.json();
            const search = await searchDecks.text();
            const create = await createDecks.text();

            await insertDeckContainer(data["forkedDecks"], forkedDeckContainer)
            await insertDeckContainer(data["ownedDecks"], ownedDeckContainer)

            const searchDeck = document.createElement("div");
            searchDeck.innerHTML = search;
            searchDeck.className = 'deck-container';
            searchDeck.id = "searchDeckButton";
            searchDeck.style.position = "relative";

            const createDeck = document.createElement("div");
            createDeck.innerHTML = create;
            createDeck.className = 'deck-container';
            createDeck.id = "createDeckButton";
            createDeck.style.position = "relative";

            const hoverOverlay = document.createElement("div")
            hoverOverlay.className = "hoverOverlay"
            hoverOverlay.style.width = "100%";
            hoverOverlay.style.height = "100%";

            searchDeck.appendChild(hoverOverlay.cloneNode(true));
            createDeck.appendChild(hoverOverlay);
            forkedDeckContainer.appendChild(searchDeck);
            ownedDeckContainer.appendChild(createDeck);

            insertEventActions();
        } else {
            window.location.replace("/login");
        }
    } catch (error) {
        window.location.replace("/login");
    }
}

export async function deckDetailLoader(path) {
    const levelContainer = document.getElementById("cards-container");

    const match = path.match(/^\/workspace\/decks\/([^/]+)/);
    if (!match) {
        console.error("Invalid path:", path);
        return;
    }

    const deckId = match[1];

    try {
        const response = await fetchWithAuth(`/api/decks/${deckId}`);

        const newCard = await fetch("/components/template/new_card.svg", { headers: { "X-Requested-With": "XMLHttpRequest" } });

        if (response.ok) {
            levelContainer.innerHTML = ""; // Clear innerHTML 

            const data = await response.json();
            const newCardData = await newCard.text();

            if (data["deckLevels"] !== undefined) {
                await insertCardContainer(data["deckLevels"], levelContainer, "level_id_PK", "level_name")
            }

            const wrapDeck = document.createElement("div");
            wrapDeck.innerHTML = newCardData;
            wrapDeck.className = 'deck-container';
            wrapDeck.style.position = "relative";

            const hoverOverlay = document.createElement("div")
            hoverOverlay.className = "hoverOverlay"
            hoverOverlay.style.width = "100%";
            hoverOverlay.style.height = "100%";

            wrapDeck.appendChild(hoverOverlay);
            levelContainer.appendChild(wrapDeck);

            insertEventActions();
        } else {
            window.location.replace("/login");
        }
    } catch (err) {
        window.location.replace("/login");
    }
}


export async function levelDetailLoader(path) {
    const levelContainer = document.getElementById("cards-container");

    const match = path.match(/^\/workspace\/decks\/([^/]+)\/([^/]+)/);
    // /^\/workspace\/decks\/[^/]+\/[^/]
    if (!match) {
        console.error("Invalid path:", path);
        return;
    }

    const deckId = match[1];
    const levelId = match[2];

    try {
        const response = await fetchWithAuth(`/api/decks/${deckId}/${levelId}`);

        const newCard = await fetch("/components/template/new_card.svg", { headers: { "X-Requested-With": "XMLHttpRequest" } });

        if (response.ok) {
            levelContainer.innerHTML = ""; // Clear innerHTML 

            const data = await response.json();
            const newCardData = await newCard.text();

            if (data["cards"] !== undefined) {
                await insertCardContainer(data["cards"], levelContainer, "card_id_PK", "card_word")
            }

            const wrapDeck = document.createElement("div");
            wrapDeck.innerHTML = newCardData;
            wrapDeck.className = 'deck-container';
            wrapDeck.style.position = "relative";

            const hoverOverlay = document.createElement("div")
            hoverOverlay.className = "hoverOverlay"
            hoverOverlay.style.width = "100%";
            hoverOverlay.style.height = "100%";

            wrapDeck.appendChild(hoverOverlay);
            levelContainer.appendChild(wrapDeck);

            insertEventActions();
        } else {
            window.location.replace("/login");
        }
    } catch (err) {
        window.location.replace("/login");
    }
}

export async function voteCardLoader() {

    const voteCardContainer = document.getElementById('vote-cards')
    const cards = [
        { id: "vote-fail", url: "/components/template/vote-fail.svg" },
        { id: "vote-hard", url: "/components/template/vote-hard.svg" },
        { id: "vote-good", url: "/components/template/vote-good.svg" },
        { id: "vote-easy", url: "/components/template/vote-easy.svg" },
    ];

    try {
        const responses = await Promise.all(cards.map(c => fetch(c.url)));

        responses.forEach((res, i) => {
            if (!res.ok) throw new Error(`Failed to fetch ${cards[i].url} (${res.status})`);
        });

        const svgs = await Promise.all(responses.map(res => res.text()));

        svgs.forEach((svgText, i) => {
            const wrap = document.createElement("div");
            wrap.classList.add("card-container");
            wrap.setAttribute("item-id", cards[i].id);
            wrap.style.position = "relative";
            wrap.innerHTML = svgText;

            const hoverOverlay = document.createElement("div");
            hoverOverlay.className = "hoverOverlay";
            hoverOverlay.style.width = "100%";
            hoverOverlay.style.height = "100%";

            wrap.appendChild(hoverOverlay);
            voteCardContainer.appendChild(wrap);
        });

        console.log("All cards loaded");
    } catch (err) {
        console.error("Error loading cards:", err);
    }
}