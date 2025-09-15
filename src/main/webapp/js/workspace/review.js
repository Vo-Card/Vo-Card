import { loadPage } from './pageManager.js';
import { fetchWithAuth } from '/js/auth/auth.js';
import { populateContainer } from '/js/deckManagement/deckLoader.js';

// TODO<>: create random func then put in loop function for playing
// 1. random array(if has more than 1 deck) then random card
// 2. loading card appearance
// 3. let user guessing the definition (not showing vote cards and definition yet) let user click on div or space-bar to continue
// 4. showing definition
// 5. let user voting for their memory (No FSRS yet)
// 6. back to step 1. until complete the session 
let tempData = null;

/**
 * Send array of deck_id for get all cards in decks
 * then continue to randomize 
 * @param {Array} ids 
 */
export async function sendPackDeckId(ids) {
    const text = ids.map(id => "arrayDeckId=" + id).join("&");

    try {
        const res = await fetchWithAuth("/api/review/getCards?" + text)
        if (res.ok) {
            const data = await res.json();
            return data.Cards;

        }
    } catch (err) {
        console.log("Failed to send Deck ID: ", err);
    }
}

async function randomizer(usrDecks) {

    // starting randomization
    const whichDeck = Math.floor(Math.random() * usrDecks.length)
    console.log(usrDecks[whichDeck]);
    const whichCard = Math.floor(Math.random() * usrDecks[whichDeck].length)
    console.log(usrDecks[whichDeck][whichCard]);

    return usrDecks[whichDeck][whichCard];
    // cardStage.textContent(packArray.Cards[whichDeck][whichCard]);
}


// Toilet ~~

// TODO:<> Loop function for play
/**
 *
 * @param {number} sessionPlay
 */
export async function cardSessionPlay(sessionPlay) {
    // start
    const usrCards = await sendPackDeckId(["752111449966383104"]);

    for (let i = 0; i < sessionPlay; i++) {
        console.log("IT'S STARTED");

        let whatCard = await randomizer(usrCards);
        console.log(whatCard);

        try {
            const cardDefinition = await fetchWithAuth(`/api/review/getDefinition?levelId=${whatCard.level_id_PK}&deckId=${whatCard.deck_id_FK}&cardId=${whatCard.card_id_PK}`);
            const data = await cardDefinition.json();

            console.log(data);
            const cardStage = document.getElementById('card-placeholder');
            populateContainer([whatCard], cardStage, "card", null);

        } catch (err) {
            console.error(err);
        }
    }
}

