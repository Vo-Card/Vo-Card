// TODO:<send response to api>

// FIXME:<deck loader not loading>
function deckLoader() {
    document.addEventListener('DOMContentLoaded', async () => {
        const deckContainer = document.getElementById('deck-container')

        try {
            const response = await fetchWithAuth("/api/decks/getDecks");

            if (response.ok) {

                const data = await response.json();
                const newDeck = document.createElement("div");
                newDeck.className = 'deck-cover'
                newDeck.textContent = data["decks"][0]["deck_name"];

                console.log(data["decks"][0]["user_id_FK"]);

                deckContainer.appendChild(newDeck);
            } else {
                window.location.replace("/login");
            }
        } catch (error) {
            window.location.replace("/login");
        }
    })
}