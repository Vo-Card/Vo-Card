// This module will be handling displaying page metadata

function toDate(arr) {
    // @ts-ignore
    return Array.isArray(arr) ? new Date(...arr) : null;
}

// Create element to display datas
function formatDate(date) {
    // @ts-ignore
    if (!(date instanceof Date) || isNaN(date)) return "";
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const yyyy = date.getFullYear();
    return `${mm}/${dd}/${yyyy}`;
}

/**
 * @param {HTMLElement} container
 * @param {Object} deckData
 */
export function showDeckInformation(container, deckData, target) {
    const dataContainer = container.querySelector("deck-data-container");

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    const dataToStore = {
        "Deck Name": deckData.deck_name,
        "Contained Cards": deckData.deck_contain_card,
        "Deck Description": deckData.deck_description,
        "Created Date": formatDate(toDate(deckData.deck_created_date)),
        "Last Update": formatDate(toDate(deckData.deck_lastest_updated)),
        "Is Public": deckData.deck_is_public,
        "Allow Cloning": deckData.deck_clone_perm,
    };

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });
}

/**
 * @param {HTMLElement} container
 * @param {Object} levelData
 */
export function showLevelInformation(container, levelData, target) {
    const dataContainer = container.querySelector("deck-data-container");

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    const dataToStore = {
        "Level Value": levelData.level_name,
        "Level Weight": levelData.level_weight,
        "Created Date": formatDate(toDate(levelData.level_created_date)),
        "Contained Cards": levelData.level_contain_card,
    };

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });
}

export function showCardInformation(container, cardData, target) {
    const dataContainer = container.querySelector("deck-data-container");

    const sel = container.querySelector("selection");

    if (sel != null) sel.remove();

    const cardContainer = container.querySelector(".card-item-container");
    cardContainer.innerHTML = "";
    cardContainer.appendChild(target.cloneNode(true));

    const rip = cardContainer.querySelector(".ripple");

    if (rip != null) rip.remove();

    const dataToStore = {
        "Card Word": cardData.card_word,
        "Created Date": formatDate(toDate(cardData.card_created_date)),
    };

    dataContainer.innerHTML = "";

    Object.entries(dataToStore).forEach(([key, value]) => {
        const p = document.createElement("p");
        const formattedString = key + " : " + value;
        p.textContent = formattedString;
        dataContainer.append(p);
    });
}
