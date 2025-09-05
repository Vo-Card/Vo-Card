import { fetchWithAuth } from '/js/auth/auth.js';

function insertDeckContainer(data, target, template){
    for (let i = 0; i < data.length; i++) {
        target.innerHTML = ""; // Clear innerHTML 
        let cardHTML = template
            .replace(/{primary_color}/g, "#0f1114")
            .replace(/{secondary_color}/g, "#0f1114")
            .replace(/{deck_id}/g, data[i]["deck_id_PK"]);

        const wrapDeck = document.createElement("div");
        wrapDeck.innerHTML = cardHTML;
        wrapDeck.className = 'deck-container';
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

export async function deckLoader() {
    const forkedDeckContainer = document.getElementById('forked-decks-container')
    const ownedDeckContainer = document.getElementById('owned-decks-container')

    try {
        const response = await fetchWithAuth("/api/decks/getDecks");

        const cardTemplate = await fetch("/components/cards/deck_template.jsp", { headers: { "X-Requested-With": "XMLHttpRequest" }}); 
        const searchDecks = await fetch("/components/cards/search_decks.jsp", { headers: { "X-Requested-With": "XMLHttpRequest" }});
        
        if (response.ok && cardTemplate.ok) {

            const data = await response.json();
            const cardTp = await cardTemplate.text();

            const search = await searchDecks.text();

            insertDeckContainer(data["forkedDecks"], forkedDeckContainer, cardTp)
            insertDeckContainer(data["ownedDecks"], ownedDeckContainer, cardTp)

            const wrapDeck = document.createElement("div");
            wrapDeck.innerHTML = search;
            wrapDeck.className = 'deck-container';
            wrapDeck.style.position = "relative";

            const hoverOverlay = document.createElement("div")
            hoverOverlay.className = "hoverOverlay"
            hoverOverlay.style.width = "100%";
            hoverOverlay.style.height = "100%";

            wrapDeck.appendChild(hoverOverlay);
            forkedDeckContainer.appendChild(wrapDeck);

        } else {
            window.location.replace("/login");
        }
    } catch (error) {
        window.location.replace("/login");
    }
}