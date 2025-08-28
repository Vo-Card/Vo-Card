// TODO:<send response to api>
import { fetchWithAuth } from '/js/auth/auth.js';

document.addEventListener('DOMContentLoaded', async () => {
    const deckContainer = document.getElementById('deck-container')


    try {
        const response = await fetchWithAuth("/api/decks/getDecks");

        if (response.ok) {
            mainContent.style.display = 'block';
            const data = await response.json();
            console.log(data.get("decks"));

            deckContainer.setContent()
        } else {
            window.location.replace("/login");
        }
    } catch (error) {
        window.location.replace("/login");
    }
})