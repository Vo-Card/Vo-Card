import { fetchWithAuth } from '/js/auth/auth.js';

export async function deckLoader() {
    const deckContainer = document.getElementById('deck-container')

    try {
        const response = await fetchWithAuth("/api/decks/getDecks");

        if (response.ok) {

            const data = await response.json();


            for (let i = 0; i < data["decks"].length; i++) {
                const deckColumn = document.createElement("div");
                const newDeck = document.createElement("div");
                const deckEditor = document.createElement("div");

                newDeck.className = 'deck';
                deckEditor.className = 'deck-editor';

                newDeck.textContent = data["decks"][i]["deck_name"];
                deckEditor.textContent = 'Edit';

                deckContainer.appendChild(deckColumn)
                deckColumn.appendChild(newDeck);
                deckColumn.appendChild(deckEditor);
            }

            console.log(data["decks"][0]["deck_name"]);
            console.log(data["decks"][1]["deck_name"]);

        } else {
            window.location.replace("/login");
        }
    } catch (error) {
        window.location.replace("/login");
    }
}