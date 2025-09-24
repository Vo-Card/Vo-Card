import { loadPage } from "/js/workspace/module/page-manager.js";
import { fetchWithAuth } from "/js/auth/auth.js";
import { populateContainer } from "/js/workspace/content/deck-loader.js";

// TODO<>: create random func then put in loop function for playing
// 1. random array(if has more than 1 deck) then random card
// 2. loading card appearance
// 3. let user guessing the definition (not showing vote cards and definition yet) let user click on div or space-bar to continue
// 4. showing definition
// 5. let user voting for their memory (No FSRS yet)
// 6. back to step 1. until complete the session

const Cache = new Map(); // per-deck cache if you still need it
let CardPool = []; // global working pool of cards (flattened)

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

/**
 *
 * @param {Array<string>} ids
 */
export async function sendPackDeckId(ids) {
    const missing = ids.filter((id) => !Cache.has(id));

    if (missing.length === 0) {
        if (CardPool.length > 0) return;
    }

    try {
        const text = missing
            .map((id) => "arrayDeckId=" + encodeURIComponent(id))
            .join("&");
        const res = await fetchWithAuth("/api/review/getCards?" + text);
        if (!res.ok) {
            console.error("Failed to fetch cards:", res.status, res.statusText);
            return;
        }

        const data = await res.json();

        let cards = [];
        if (Array.isArray(data.Cards)) {
            if (data.Cards.length > 0 && data.Cards[0].card_id_PK) {
                cards = data.Cards;
            } else {
                for (const group of data.Cards) {
                    if (Array.isArray(group.cards)) {
                        cards.push(...group.cards);
                    } else if (Array.isArray(group)) {
                        cards.push(...group);
                    } else if (group.card_id_PK) {
                        cards.push(group);
                    }
                }
            }
        } else {
            console.warn("Unexpected Cards format from API", data);
            return;
        }

        for (const card of cards) {
            if (!Cache.has(card.deck_id_FK)) Cache.set(card.deck_id_FK, []);
            Cache.get(card.deck_id_FK).push(card);
        }

        CardPool = CardPool.concat(cards);
    } catch (err) {
        console.error("Failed to send Deck ID:", err);
    }
}

/**
 * Get deckId for a given cardId
 * @param {string} cardId
 * @returns {string|null} deckId or null if not found
 */
function getDeckIdByCardId(cardId) {
    for (const [deckId, cards] of Cache.entries()) {
        if (cards.some((card) => card.card_id_PK === cardId)) {
            return deckId;
        }
    }
    return null; // not found
}

/**
 * Return and remove 1 random card from the global pool.
 * @param {boolean} isPop
 * @returns {Object|null} one card or null if pool empty
 */
function randomizer(isPop) {
    if (CardPool.length === 0) {
        console.warn("No cards available in pool!");
        return null;
    }

    const idx = Math.floor(Math.random() * CardPool.length);
    if (isPop) {
        return CardPool.splice(idx, 1)[0];
    }
    return CardPool[idx];
}

const sessionDataTemplate = {
    remaining: 0,
    total_card: 0,
    deckIds: [],
    total_pass: 0,
    total_failed: 0,
    counting: 0,
    is_counting: true,
};

let sessionData = null;

let timerInterval = null;

/**
 *
 * @param {number} sessionPlay
 */
export async function cardSessionPlay(deckIds, sessionPlay, playTime) {
    if (timerInterval != null) clearInterval(timerInterval);
    // Load card datas
    await sendPackDeckId(deckIds);
    await loadPage("/workspace/playground");
    // Clone template :D
    if (sessionData == null) sessionData = { ...sessionDataTemplate };
    sessionData.counting = playTime;
    sessionData.remaining = sessionPlay;
    sessionData.total_card = sessionPlay;
    sessionData.deckIds = deckIds;

    const reviewTimer = document.querySelector("review-timer");
    document.querySelector("total-cards").textContent = sessionPlay.toString();

    timerInterval = setInterval(() => {
        if (sessionData.is_counting) sessionData.counting--;

        const minutes = Math.floor(sessionData.counting / 60)
            .toString()
            .padStart(2, "0");
        const seconds = (sessionData.counting % 60).toString().padStart(2, "0");

        reviewTimer.textContent = `${minutes}:${seconds}`;

        if (sessionData.counting < 0) {
            clearInterval(timerInterval);
            //force end
            sessionData.remaining = 0;
            nextCard();
        }
    }, 1000);

    await nextCard();
}

async function nextCard() {
    document.querySelector("remainding-cards").textContent = (
        sessionData.total_card -
        sessionData.remaining +
        1
    ).toString();

    if (sessionData.remaining <= 0 || CardPool.length < 4) {
        await fetchWithAuth("/api/review/addCompletedSession", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                totalCards: sessionData.total_card,
                totalCorrect: sessionData.total_pass,
                totalFailed: sessionData.total_failed,
            }),
        });

        Cache.clear();
        sessionData = {};
        CardPool = [];
        loadPage("/workspace/review");
        return;
    }

    const selectedCard = randomizer(true);
    if (!selectedCard) {
        console.warn("No card picked!");
        return;
    }

    renderCard(selectedCard);
}

async function renderCard(selectedCard) {
    const CAS = document.querySelector("card-answer-selector");
    const defContainer = document.querySelector("card-data");

    CAS.innerHTML = "";
    defContainer.innerHTML = "";

    const cardDataFetch = await fetchWithAuth(
        `/api/decks/${selectedCard.deck_id_FK}/${selectedCard.level_id_PK}/${selectedCard.card_id_PK}`
    );

    const cardData = (await cardDataFetch.json()).card_data;

    console.log(cardData);

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

    const falseCards = Array.from(
        { length: CardPool.length >= 3 ? 3 : CardPool.length },
        () => randomizer(false)
    );

    const choices = shuffleArray([selectedCard, ...falseCards]);

    // @ts-ignore
    await populateContainer(choices, CAS, "card", "no-display");

    const allCardAns = CAS.querySelectorAll(".card-item-container");
    const nextDeckCountDown = document.querySelector("card-countdown");
    allCardAns.forEach((element) => {
        element.addEventListener("click", () => {
            const cardId = element.getAttribute("item-id");
            if (cardId == selectedCard.card_id_PK) {
                sessionData.total_pass += 1;
            } else {
                sessionData.total_failed += 1;
            }

            allCardAns.forEach(async (element) => {
                const cardId = element.getAttribute("item-id");
                if (cardId == selectedCard.card_id_PK) {
                    element.classList.add("correct");
                    await fetchWithAuth("/api/review/addLearnedCard", {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({
                            cardId: cardId,
                            deckId: getDeckIdByCardId(cardId),
                        }),
                    });
                } else {
                    // @ts-ignore
                    element.style.opacity = 0.5;
                }
                // @ts-ignore
                element.style.pointerEvents = "none";
            });

            let count = 5;

            const countdownInterval = setInterval(() => {
                sessionData.is_counting = false;
                nextDeckCountDown.textContent = `Next Card in : ${count} secs`;
                // @ts-ignore
                nextDeckCountDown.style.display = "block";
                count--;

                if (count < 0) {
                    clearInterval(countdownInterval);
                    // @ts-ignore
                    nextDeckCountDown.style.display = "none";
                    sessionData.is_counting = true;
                    sessionData.remaining--;
                    nextCard();
                }
            }, 1000);
        });
    });

    // Add a juicy ripple effect
    allCardAns.forEach((element) => {
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
                ripple.style.left = `${event.clientX - rect.left - size / 2}px`;
                ripple.style.top = `${event.clientY - rect.top - size / 2}px`;

                element.appendChild(ripple);

                ripple.addEventListener("animationend", () => ripple.remove());
            }
        );
    });
}
