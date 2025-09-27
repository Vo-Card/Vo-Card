import { appendTemplate, populateContainer } from "../content/deck-loader.js";
import { loadPage } from "../module/page-manager.js";
import { fetchWithAuth, TokenManager } from "/js/auth/auth.js";

export async function loadUserInformation() {
    const res = await fetchWithAuth("/api/review/mostRecentLearning");
    const data = await res.json();

    const welcomeMessages = [
        "Greetings!",
        "Welcome back!",
        "Ready to learn some new words?",
        "Have a great learning session!",
        "Let’s expand your vocabulary today!",
        "Knowledge awaits—are you ready?",
        "Another step closer to mastery!",
        "Your learning journey continues!",
        "Let’s unlock some new words together!",
        "Consistency is key—great to see you again!",
        "Your brain is hungry, let’s feed it!",
        "Small steps today, big progress tomorrow!",
        "Let’s make learning fun and easy!",
        "Flashcards loaded—let’s go!",
        "Keep going, you’re doing amazing!",
        "A new session, a new opportunity to grow!",
        "Words are powerful—let’s collect more!",
        "Your future self will thank you for this!",
        "Learning never stops—glad you’re here!",
        "Another day, another word mastered!",
    ];

    const wM =
        welcomeMessages[Math.floor(Math.random() * welcomeMessages.length)];

    document.getElementById(
        "welcome-message"
    ).innerHTML = `<span style="color: #C38A39;">${wM}</span> ${TokenManager.getDisplayName()}.`;

    // const display
    console.log(data);
    const reviewedCard = data.recent_review;

    const dailyCardContainer = /**@type {HTMLElement} */ (
        document.querySelector("daily-card-container")
    );

    if (reviewedCard === null) {
        await appendTemplate(
            dailyCardContainer,
            "/components/template/learn_new_card.svg",
            "daily-review-card",
            "card-item-container"
        );

        const element = dailyCardContainer.querySelector("#daily-review-card");
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

                ripple.addEventListener("animationend", () => {
                    loadPage("/workspace/review");
                    element.remove();
                });
            }
        );
    } else {
        populateContainer(
            [reviewedCard],
            dailyCardContainer,
            "card",
            "no-display"
        );
    }
}
