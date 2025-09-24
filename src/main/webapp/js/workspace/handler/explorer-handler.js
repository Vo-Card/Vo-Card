// Module for handling the explorer page functionality
import { populateContainer } from "../content/deck-loader.js";
import { fetchWithAuth } from "/js/auth/auth.js";

/**
 * Fetch public decks from the server
 * @returns {Promise<Array>} Array of public decks
 */
async function fetchPublicDecks() {
    try {
        const response = await fetchWithAuth("/api/decks/public");
        if (response.ok) {
            return (await response.json()).publicDecks;
        } else {
            console.error("Failed to fetch public decks:", response.statusText);
            return [];
        }
    } catch (error) {
        console.error("Error fetching public decks:", error);
        return [];
    }
}

function getDeckTemplate() {
    const template = document.querySelector("explore-template");
    return template ? template.cloneNode(true) : null;
}

export async function displayPublicDecks() {
    const decks = await fetchPublicDecks();
    const container = document.querySelector("public-decks-container");
    const template = /**@type {HTMLElement} */ (getDeckTemplate());
    template.style.display = "flex";
    container.innerHTML = "";

    console.log(decks);

    decks.forEach((deck) => {
        const deckElement = /**@type {HTMLElement} */ (
            template.cloneNode(true)
        );
        const cardContainer = /**@type {HTMLElement} */ (
            deckElement.querySelector("card-container")
        );
        cardContainer.innerHTML = "";
        populateContainer([deck], cardContainer, "deck", "public");

        // -----------------------
        // Deck Info Logic
        // -----------------------

        deckElement.querySelector("deck-title").textContent = deck.deck_name;
        deckElement.querySelector(
            "deck-author"
        ).textContent = `by ${deck.username}`;
        deckElement.querySelector("deck-description").textContent =
            deck.deck_description || "No description provided.";

        deckElement.querySelector(
            "total-cards"
        ).textContent = `${deck.deck_contain_card} Cards`;

        const likedValue = deckElement.querySelector("liked-value");
        const dislikesValue = deckElement.querySelector("dislikes-value");

        if (deck.user_review_action === 1) {
            likedValue.classList.add("voted");
        } else if (deck.user_review_action === 2) {
            dislikesValue.classList.add("voted");
        }

        const removeVote = async () => {
            likedValue.classList.remove("voted");
            dislikesValue.classList.remove("voted");
            const res = await fetchWithAuth(`/api/decks/removeVote`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ deckId: deck.deck_id_PK }),
            });
            if (res.ok) {
                console.log("Review removed successfully!");
            } else {
                console.error("Failed to remove review.");
            }
        };

        likedValue.textContent = `${deck.upvotes} 󰔓`;
        dislikesValue.textContent = `${deck.downvotes} 󰔑`;

        likedValue.addEventListener("click", async () => {
            // Check if already liked
            if (likedValue.classList.contains("voted")) {
                await removeVote();
                let currentLikes = parseInt(likedValue.textContent) || 0;
                likedValue.textContent = `${Math.max(currentLikes - 1, 0)} 󰔓`;
                return;
            }

            try {
                const response = await fetchWithAuth(`/api/decks/upvote`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ deckId: deck.deck_id_PK }),
                });
                if (response.ok) {
                    // Switch to liked state
                    console.log("Deck upvoted successfully!");
                    let currentLikes = parseInt(likedValue.textContent) || 0;
                    likedValue.textContent = `${currentLikes + 1} 󰔓`;
                    likedValue.classList.add("voted");
                    dislikesValue.classList.remove("voted");
                    // If previously disliked, decrement dislike count
                    dislikesValue.textContent = `${Math.max(
                        (parseInt(dislikesValue.textContent) || 1) - 1,
                        0
                    )} 󰔑`;
                } else {
                    console.error("Failed to upvote deck.");
                }
            } catch (error) {
                console.error("Error upvoting deck:", error);
            }
        });

        dislikesValue.addEventListener("click", async () => {
            // Check if already disliked
            if (dislikesValue.classList.contains("voted")) {
                await removeVote();
                let currentDislikes = parseInt(dislikesValue.textContent) || 0;
                dislikesValue.textContent = `${Math.max(
                    currentDislikes - 1,
                    0
                )} 󰔑`;
                return;
            }

            try {
                const response = await fetchWithAuth(`/api/decks/downvote`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ deckId: deck.deck_id_PK }),
                });
                if (response.ok) {
                    // Switch to disliked state
                    console.log("Deck downvoted successfully!");
                    let currentDislikes =
                        parseInt(dislikesValue.textContent) || 0;
                    dislikesValue.textContent = `${currentDislikes + 1} 󰔑`;
                    dislikesValue.classList.add("voted");
                    likedValue.classList.remove("voted");
                    // If previously liked, decrement like count
                    likedValue.textContent = `${Math.max(
                        (parseInt(likedValue.textContent) || 1) - 1,
                        0
                    )} 󰔓`;
                } else {
                    console.error("Failed to downvote deck.");
                }
            } catch (error) {
                console.error("Error downvoting deck:", error);
            }
        });

        // -----------------------
        // Like/Dislike Bar Logic
        // -----------------------

        const likePercent =
            deck.upvotes + deck.downvotes === 0
                ? 0
                : (deck.upvotes / (deck.upvotes + deck.downvotes)) * 100;
        const dislikePercent = 100 - likePercent;
        if (deck.upvotes == 0 && deck.downvotes == 0) {
            // @ts-ignore
            deckElement.querySelector("#like-bar-fill").style.width = `50%`;
            // @ts-ignore
            deckElement.querySelector("#dislike-bar-fill").style.width = `50%`;
        } else {
            deckElement.querySelector(
                "#like-bar-fill"
                // @ts-ignore
            ).style.width = `${likePercent}%`;
            deckElement.querySelector(
                "#dislike-bar-fill"
                // @ts-ignore
            ).style.width = `${dislikePercent}%`;
        }

        // -----------------------
        // Fork Button Logic
        // -----------------------

        const forkButton = deckElement.querySelector("#fork-deck-button");
        forkButton.addEventListener("click", async () => {
            try {
                const response = await fetchWithAuth(`/api/decks/fork`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ deckId: deck.deck_id_PK }),
                });
                if (response.ok) {
                    console.log("Deck forked successfully!");
                } else {
                    console.error("Failed to fork deck.");
                }
            } catch (error) {
                console.error("Error forking deck:", error);
                alert("Error occurred while forking the deck.");
            }
        });

        container.appendChild(deckElement);
    });

    console.log(decks);
}
