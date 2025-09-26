import { loadTemplate } from "../content/deck-loader.js";
import { loadPage } from "../module/page-manager.js";
import { closePopup } from "./popup-handler.js";
import { fetchWithAuth } from "/js/auth/auth.js";

// Card CachedData Only the hardest of them all

function createForm(
    inputOverride = "input",
    formElement,
    labelValue,
    inputType = "text",
    inputValue = "",
    inputname,
    attributes = {},
    updateFunction = () => {}
) {
    const div = document.createElement("div");
    const label = document.createElement("label");

    if (labelValue != null) {
        label.textContent = labelValue;
        label.setAttribute("for", inputname);
        div.appendChild(label);
    }

    const input = document.createElement(inputOverride);

    if (inputOverride !== "textarea") {
        // @ts-ignore
        input.type = inputType;
    }

    // @ts-ignore
    input.value = inputValue;
    input.oninput = updateFunction;
    // @ts-ignore
    input.name = inputname;
    input.id = inputname;

    Object.entries(attributes).forEach(([key, value]) => {
        input.setAttribute(key, value);
    });

    div.appendChild(document.createElement("br"));
    div.appendChild(input);
    formElement.appendChild(div);
    return input;
}

function callCreate(type, apiEndpoint, form) {
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData(form);

        const checkboxes = form.querySelectorAll("input[type=checkbox]");
        // @ts-ignore
        const data = Object.fromEntries(Array.from(formData.entries()));

        checkboxes.forEach((cb) => {
            data[cb.name] = cb.checked;
        });

        console.log(data);

        try {
            const response = await fetchWithAuth(apiEndpoint, {
                method: type,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });
            if (response.ok) {
                const message = await response.json();
                closePopup();
                console.log(message);
            } else {
                alert("Failed to update deck.");
            }
        } catch (err) {
            console.error(err);
            alert("Error updating deck.");
        }

        // Refresh the page to show the new data
        loadPage(window.location.pathname);
    });
}

export async function createLoader(path, itemType) {
    const temp = document.createElement("div");
    const popupTemplate = await (await fetch(path)).text();

    temp.style.height = "100%";
    temp.innerHTML = popupTemplate;

    const inpArea = temp.querySelector("create-input-area");
    const cardDisplay = temp.querySelector(".card-item-container");
    const form = /**@type {HTMLFormElement} */ document.createElement("form");

    const winPath = document.location.pathname;

    switch (itemType) {
        case "Deck":
            const theme = await loadTemplate(
                "/components/template/deck_template.svg"
            );

            // Insert SVG into display
            cardDisplay.innerHTML = theme.replace(/{ii}/g, "");

            // Get all linearGradient elements inside this SVG
            const gradients = cardDisplay.querySelectorAll("linearGradient");

            const svgTitle = cardDisplay.querySelector("#fit-text");
            svgTitle.textContent = "New Deck";
            createForm(
                "input",
                form,
                "Deck Name :",
                "text",
                "New Deck",
                "deckName",
                {},
                (e) => {
                    if (svgTitle) svgTitle.textContent = e.target.value;
                }
            );

            createForm(
                "textarea",
                form,
                "Description :",
                "textarea",
                "Description",
                "deckDescription"
            );

            const priColor = createForm(
                "input",
                form,
                "Primary Color :",
                "text",
                "#000000",
                "primaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                // @ts-ignore
                (e) => {
                    gradients.forEach((gradient) => {
                        const stops = gradient.querySelectorAll("stop");
                        stops.forEach((stop, index) => {
                            if (index === 0)
                                // @ts-ignore
                                stop.setAttribute("stop-color", priColor.value);
                        });
                    });
                }
            );

            const secColor = createForm(
                "input",
                form,
                "Secondary Color :",
                "text",
                "#000000",
                "secondaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                // @ts-ignore
                (e) => {
                    gradients.forEach((gradient) => {
                        const stops = gradient.querySelectorAll("stop");
                        stops.forEach((stop, index) => {
                            if (index === 1)
                                // @ts-ignore
                                stop.setAttribute("stop-color", secColor.value);
                        });
                    });
                }
            );

            createForm("input", form, null, "submit", "Create Deck");

            callCreate("PUT", "/api/decks/create", form);

            break;

        case "Level":
            const cardId = "level_" + Date.now(); // or UUID
            const levelTheme = (
                await loadTemplate("/components/template/card_template.svg")
            )
                .replace(/{card_id}/g, cardId)
                .replace(/{top_indicator}/g, "Deck's Name")
                .replace(/{ii}/g, "");

            const match = winPath.match(/^\/workspace\/decks\/([^/]+)/);
            if (!match) return temp;

            const deckId = match[1];

            // Insert SVG into display
            cardDisplay.innerHTML = levelTheme;

            // Get all linearGradient elements inside this SVG
            const lev_radgradient = cardDisplay.querySelector("radialGradient");
            const lev_svgPriColor = cardDisplay.querySelector(
                `#primary_indicator_${cardId}`
            );

            const levSvgTitle = cardDisplay.querySelector("#fit-text");
            levSvgTitle.textContent = "New Level";
            createForm(
                "input",
                form,
                "Level Name :",
                "text",
                "New Level",
                "levelValue",
                {},
                (e) => {
                    if (levSvgTitle) levSvgTitle.textContent = e.target.value;
                }
            );

            createForm(
                "input",
                form,
                "Level Weight :",
                "number",
                "0",
                "levelWeight"
            );

            // Primary color
            const levPriColor = createForm(
                "input",
                form,
                "Primary Color :",
                "text",
                "#000000",
                "primaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                (e) => {
                    // @ts-ignore
                    lev_svgPriColor.setAttribute("fill", levPriColor.value);
                }
            );
            // @ts-ignore
            lev_svgPriColor.setAttribute("fill", levPriColor.value);

            // Secondary color
            const levSecColor = createForm(
                "input",
                form,
                "Secondary Color :",
                "text",
                "#000000",
                "secondaryColor",
                { "data-coloris": "" },
                // @ts-ignore
                (e) => {
                    const stops = lev_radgradient.querySelectorAll("stop");
                    // @ts-ignore
                    stops[1].setAttribute("stop-color", levSecColor.value);
                }
            );

            const stops = lev_radgradient.querySelectorAll("stop");
            // @ts-ignore
            stops[1].setAttribute("stop-color", levSecColor.value);

            createForm("input", form, null, "submit", "Create Level");
            callCreate("PUT", `/api/decks/${deckId}/create`, form);

            break;
        case "Card":
            const cardTheme2 = await loadTemplate(
                "/components/template/card_template.svg"
            );

            const cardId2 = "card_" + Date.now(); // or UUID

            const match2 = winPath.match(
                /^\/workspace\/decks\/([^/]+)\/([^/]+)/
            );
            if (!match2) return temp;

            const deckId2 = match2[1];
            const levelId2 = match2[2];

            const container = document.getElementById("cards-container");
            // Insert SVG into display
            cardDisplay.innerHTML = cardTheme2
                .replace(/{card_id}/g, cardId2)
                .replace(/{top_indicator}/g, "Level's Name")
                .replace(/{ii}/g, "")
                .replace(
                    /{primary_color}/g,
                    container.getAttribute("primary-color")
                )
                .replace(
                    /{secondary_color}/g,
                    container.getAttribute("secondary-color")
                );

            const svgTitle2 = cardDisplay.querySelector("#fit-text");
            svgTitle2.textContent = "New Card";
            createForm(
                "input",
                form,
                "Card Word :",
                "text",
                "New Card",
                "cardWord",
                {},
                (e) => {
                    if (svgTitle2) svgTitle2.textContent = e.target.value;
                }
            );

            createForm("input", form, null, "submit", "Create Card");
            callCreate("PUT", `/api/decks/${deckId2}/${levelId2}/create`, form);

            break;
        default:
            console.error("Incorrect type");
            break;
    }
    inpArea.appendChild(form);
    // @ts-ignore
    Coloris.setInstance("[data-coloris]", {
        theme: "polaroid",
        themeMode: "dark",
        alpha: false,
        formatToggle: true,
        swatches: ["#264653", "#2a9d8f", "#e9c46a"],
    });

    return temp;
}

/**
 *
 * @param {String} path  - path to template file
 * @param {String} type - is Deck, Level, or Card
 * @param {Object} data - object like your Java result (card_word, pos, definitions, etc.)
 * @returns {Promise<HTMLElement>}
 */
export async function editLoader(path, type, data) {
    // Prepare Template
    const temp = document.createElement("div");
    temp.style.width = "100%";
    temp.style.height = "100%";

    const template = await (await fetch(path)).text();
    temp.innerHTML = template;

    console.log(data);

    switch (type) {
        case "deck":
            const form = document.createElement("form");
            temp.querySelector("edit-input-area").appendChild(form);

            const deckId = data.deck_id_PK; // For API
            const deckThemeSVG = (await loadTemplate(data.theme_url))
                .replace(/{deck_id}/g, data.deck_id_PK)
                .replace(/{deck_name}/g, data.deck_name)
                .replace(/{primary_color}/g, data.primary_color)
                .replace(/{secondary_color}/g, data.secondary_color)
                .replace(/{ii}/g, "");

            const DeckContainer = temp.querySelector("card-container");
            DeckContainer.innerHTML = deckThemeSVG;

            const hoverOverlay = document.createElement("div");
            hoverOverlay.className = "hoverOverlay";
            DeckContainer.appendChild(hoverOverlay);

            const deckSVGTitle = DeckContainer.querySelector("#fit-text");
            const deckSVGGradients =
                /**@type {NodeListOf<SVGLinearGradientElement>} */ (
                    DeckContainer.querySelectorAll("linearGradient")
                );

            createForm(
                "input",
                form,
                "Deck Name :",
                "text",
                data.deck_name,
                "deckName",
                {},
                (e) => {
                    if (deckSVGTitle) deckSVGTitle.textContent = e.target.value;
                }
            );

            createForm(
                "textarea",
                form,
                "Description :",
                "textarea",
                data.deck_description,
                "deckDescription"
            );

            const deckPriColor = createForm(
                "input",
                form,
                "Primary Color :",
                "text",
                data.primary_color,
                "primaryColor",
                { "data-coloris": "" },
                (e) => {
                    deckSVGGradients.forEach((gradient) => {
                        const stops = gradient.querySelectorAll("stop");
                        stops.forEach((stop, index) => {
                            if (index === 0)
                                stop.setAttribute(
                                    "stop-color",
                                    /**@type {HTMLInputElement} */ (
                                        deckPriColor
                                    ).value
                                );
                        });
                    });
                }
            );

            const deckSecColor = createForm(
                "input",
                form,
                "Secondary Color :",
                "text",
                data.secondary_color,
                "secondaryColor",
                { "data-coloris": "" },
                (e) => {
                    deckSVGGradients.forEach((gradient) => {
                        const stops = gradient.querySelectorAll("stop");
                        stops.forEach((stop, index) => {
                            if (index === 1)
                                stop.setAttribute(
                                    "stop-color",
                                    /**@type {HTMLInputElement} */ (
                                        deckSecColor
                                    ).value
                                );
                        });
                    });
                }
            );

            // custom checkbox [Import]

            const deckPublicWraper = document.createElement("div");
            deckPublicWraper.className = "info-wraper";
            deckPublicWraper.style.height = "40px";
            deckPublicWraper.style.alignItems = "center";
            deckPublicWraper.style.margin = "10px 0 0 0";

            const deckPublicText = document.createElement("p");
            deckPublicText.textContent = "Set Public";
            deckPublicText.style.margin = "0";
            deckPublicWraper.appendChild(deckPublicText);

            const deckPublicDiv = document.createElement("div");
            deckPublicDiv.className = "checkbox-wrapper-59";

            const deckPublicLabel = document.createElement("label");
            deckPublicLabel.className = "switch";

            const deckPublicInput = document.createElement("input");
            deckPublicInput.type = "checkbox";
            deckPublicInput.id = "isPublic";
            deckPublicInput.name = "isPublic";
            deckPublicInput.checked = data.deck_is_public;

            console.log(data);

            const checkboxSpan = document.createElement("span");
            checkboxSpan.className = "slider";

            deckPublicLabel.appendChild(deckPublicInput);
            deckPublicLabel.appendChild(checkboxSpan);

            deckPublicDiv.appendChild(deckPublicLabel);

            deckPublicWraper.appendChild(deckPublicDiv);

            form.appendChild(deckPublicWraper);

            // custom checkbox [Import]

            const deckCloneWraper = document.createElement("div");
            deckCloneWraper.className = "info-wraper";
            deckCloneWraper.style.height = "40px";
            deckCloneWraper.style.alignItems = "center";
            deckCloneWraper.style.margin = "10px 0 0 0";

            const deckCloneText = document.createElement("p");
            deckCloneText.textContent = "Set Clone";
            deckCloneText.style.margin = "0";
            deckCloneWraper.appendChild(deckCloneText);

            const deckCloneDiv = document.createElement("div");
            deckCloneDiv.className = "checkbox-wrapper-59";

            const deckCloneLabel = document.createElement("label");
            deckCloneLabel.className = "switch";

            const deckCloneInput = document.createElement("input");
            deckCloneInput.type = "checkbox";
            deckCloneInput.id = "allowCloning";
            deckCloneInput.name = "allowCloning";
            deckCloneInput.checked = data.deck_clone_perm;

            const checkboxSpan2 = document.createElement("span");
            checkboxSpan2.className = "slider";

            deckCloneLabel.appendChild(deckCloneInput);
            deckCloneLabel.appendChild(checkboxSpan2);

            deckCloneDiv.appendChild(deckCloneLabel);

            deckCloneWraper.appendChild(deckCloneDiv);

            form.appendChild(deckCloneWraper);
            createForm("input", form, null, "submit", "Save");
            callCreate("POST", `/api/decks/${deckId}/update`, form);
            break;
        case "level":
            const levelForm = document.createElement("form");
            temp.querySelector("edit-input-area").appendChild(levelForm);

            const levelId = "edit_" + data.deck_id_PK;
            const levelThemeSVG = (await loadTemplate(data.theme_url))
                .replace(/{card_id}/g, levelId)
                .replace(/{top_indicator}/g, data.deck_name)
                .replace(/{word_content}/g, data.level_name)
                .replace(/{primary_color}/g, data.primary_color)
                .replace(/{secondary_color}/g, data.secondary_color)
                .replace(/{ii}/g, "");

            const LevelContainer = temp.querySelector("card-container");
            LevelContainer.innerHTML = levelThemeSVG;

            const match = window.location.pathname.match(
                /^\/workspace\/decks\/([^/]+)/
            );
            if (!match) return temp;

            const lDeckId = match[1]; // For API

            // Insert SVG into display
            LevelContainer.innerHTML = levelThemeSVG;

            // Get all linearGradient elements inside this SVG
            const lev_radgradient =
                LevelContainer.querySelector("radialGradient");
            const lev_svgPriColor = LevelContainer.querySelector(
                `#primary_indicator_${levelId}`
            );
            const levSvgTitle = LevelContainer.querySelector("#fit-text");

            createForm(
                "input",
                levelForm,
                "Level Name :",
                "text",
                data.level_name,
                "levelValue",
                {},
                (e) => {
                    if (levSvgTitle) levSvgTitle.textContent = e.target.value;
                }
            );

            createForm(
                "input",
                levelForm,
                "Level Weight :",
                "number",
                data.level_weight,
                "levelWeight"
            );

            const lvlPriColor = createForm(
                "input",
                levelForm,
                "Primary Color :",
                "text",
                data.primary_color,
                "primaryColor",
                { "data-coloris": "" },
                (e) => {
                    lev_svgPriColor.setAttribute(
                        "fill",
                        /**@type {HTMLInputElement} */ (lvlPriColor).value
                    );
                }
            );

            const lvlSecColor = createForm(
                "input",
                levelForm,
                "Secondary Color :",
                "text",
                data.secondary_color,
                "secondaryColor",
                { "data-coloris": "" },
                (e) => {
                    const stops = lev_radgradient.querySelectorAll("stop");
                    stops[1].setAttribute(
                        "stop-color",
                        /**@type {HTMLInputElement} */ (lvlSecColor).value
                    );
                }
            );

            createForm("input", levelForm, null, "submit", "Save");
            callCreate(
                "POST",
                `/api/decks/${lDeckId}/${data.level_id_PK}/update`,
                levelForm
            );
            break;
        case "card":
            const cardForm = /**@type {HTMLFormElement} */ (temp.firstChild);
            const cardNameInput = /**@type {HTMLInputElement} */ (
                cardForm.querySelector("#edit-card-name-input")
            );

            const cardAddDefBTN = /**@type {HTMLButtonElement} */ (
                cardForm.querySelector("def-container button")
            );

            const cardAddPosBTN = /**@type {HTMLButtonElement} */ (
                cardForm.querySelector("pos-container button")
            );

            const defContainer = /**@type {HTMLElement} */ (
                cardForm.querySelector("def-values")
            );

            const posContainer = /**@type {HTMLElement} */ (
                cardForm.querySelector("pos-values")
            );

            const cancelBTN = /**@type {HTMLButtonElement} */ (
                cardForm.querySelector("[cancel-btn]")
            );

            cancelBTN.addEventListener("click", (e) => {
                e.preventDefault();
                closePopup();
            });

            cardForm.onsubmit = (e) => {
                e.preventDefault();
                prepareCardData();
            };

            const cardData = data.card_data;
            clearCardCache();
            createCache(cardData);
            cardNameInput.value = cardData.card_word;
            cardNameInput.oninput = () => {
                updatedCardName(cardNameInput.value);
            };

            const POS = /**@type {Array} */ (cardData.pos);

            POS.forEach((pos_data, idx) => {
                const posAppend =
                    /**@type {HTMLButtonElement}*/ document.createElement(
                        "button"
                    );
                if (idx === 0) {
                    posContainer
                        .querySelectorAll("[pos_append]")
                        .forEach((el) => {
                            el.removeAttribute("active");
                        });
                    posAppend.setAttribute("active", "");
                    displayDefinition(defContainer, pos_data, posAppend);
                }
                posAppend.addEventListener("click", (event) => {
                    event.preventDefault();
                    posContainer
                        .querySelectorAll("[pos_append]")
                        .forEach((el) => {
                            el.removeAttribute("active");
                        });
                    posAppend.setAttribute("active", "");
                    displayDefinition(defContainer, pos_data, posAppend);
                });
                const wraper = document.createElement("div");
                wraper.className = "card-edit-value-wraper";

                posAppend.className = "popup-card-create-buttons";
                posAppend.setAttribute("pos_append", "");
                posAppend.innerHTML = pos_data.part_of_speech;

                const delBtn = document.createElement("button");
                delBtn.className = "edit-del-btn";
                delBtn.textContent = "󰆴";

                delBtn.addEventListener("click", (e) => {
                    e.preventDefault();

                    if (delBtn.hasAttribute("deleted")) {
                        delBtn.removeAttribute("deleted");
                        posAppend.removeAttribute("deleted");
                        removeDeleteUpdate(pos_data.pos_id_PK, true);
                    } else {
                        delBtn.setAttribute("deleted", "");
                        posAppend.setAttribute("deleted", "");
                        addDeleteUpdate(pos_data.pos_id_PK, true);
                    }
                });

                wraper.appendChild(posAppend);
                wraper.appendChild(delBtn);

                posContainer.appendChild(wraper);
            });

            cardAddPosBTN.addEventListener("click", (e) => {
                e.preventDefault();

                // create fake ~~root~~ data environment
                const generatedData = {
                    part_of_speech: "noname",
                    pos_id_PK: "fake_" + Date.now(),
                    definitions: [],
                };

                addCreateUpdate(
                    "pos",
                    null,
                    generatedData.part_of_speech.trim(),
                    generatedData.pos_id_PK
                );

                const newPosAppend =
                    /**@type {HTMLButtonElement}*/ document.createElement(
                        "button"
                    );

                newPosAppend.addEventListener("click", (event) => {
                    event.preventDefault();
                    posContainer
                        .querySelectorAll("[pos_append]")
                        .forEach((el) => {
                            el.removeAttribute("active");
                        });
                    newPosAppend.setAttribute("active", "");
                    displayDefinition(
                        defContainer,
                        generatedData,
                        newPosAppend
                    );
                });
                const wraper = document.createElement("div");
                wraper.className = "card-edit-value-wraper";

                newPosAppend.className = "popup-card-create-buttons";
                newPosAppend.setAttribute("pos_append", "");
                newPosAppend.setAttribute("created", "");
                newPosAppend.innerHTML = generatedData.part_of_speech;

                const delBtn = document.createElement("button");
                delBtn.className = "edit-del-btn";
                delBtn.textContent = "󰆴";

                delBtn.addEventListener("click", (e) => {
                    e.preventDefault();

                    if (currentPos === generatedData.pos_id_PK) {
                        // set current page to
                        /**@type {HTMLButtonElement} */ (
                            posContainer.querySelector("[pos_append]")
                        ).click();
                    }

                    removeCreateUpdate(
                        "pos",
                        null,
                        generatedData.part_of_speech.trim(),
                        generatedData.pos_id_PK
                    );
                    wraper.remove();
                });
                wraper.appendChild(newPosAppend);
                wraper.appendChild(delBtn);
                posContainer.appendChild(wraper);
            });

            cardAddDefBTN.addEventListener("click", (e) => {
                e.preventDefault();

                // create fake ~~root~~ data environment
                const generatedData = {
                    uuid: "fake_" + Date.now(),
                    content: "New definition",
                };

                addCreateUpdate(
                    "definition",
                    currentPos,
                    generatedData.content.trim(),
                    generatedData.uuid
                );

                const newDefAppend = /**@type {HTMLTextAreaElement}*/ (
                    document.createElement("textarea")
                );
                newDefAppend.value = generatedData.content;
                newDefAppend.oninput = (event) => {
                    event.preventDefault();
                    addCreateUpdate(
                        "definition",
                        currentPos,
                        newDefAppend.value.trim(),
                        generatedData.uuid
                    );
                };

                const wraper = document.createElement("div");
                wraper.className = "card-edit-value-wraper";

                newDefAppend.className = "definition-edit-input";
                newDefAppend.style = "field-sizing: content;";
                newDefAppend.setAttribute("created", "");

                const delBtn = document.createElement("button");
                delBtn.className = "edit-del-btn";
                delBtn.textContent = "󰆴";

                delBtn.addEventListener("click", (e) => {
                    e.preventDefault();
                    removeCreateUpdate(
                        "definition",
                        currentPos,
                        newDefAppend.value.trim(),
                        generatedData.uuid
                    );
                    wraper.remove();
                });
                wraper.appendChild(newDefAppend);
                wraper.appendChild(delBtn);
                defContainer.appendChild(wraper);
            });

            break;
        default:
            break;
    }

    return temp;
}

/**
 *
 * @param {HTMLElement} target
 * @param {Object} posData
 */
function displayDefinition(target, posData, posbtn) {
    target.innerHTML = "";
    const definitions = /**@type {Array} */ (posData.definitions);

    currentPos = posData.pos_id_PK;

    console.log(posData, posData.pos_id_PK.split("_"));
    let isFake = false;

    if (posData.pos_id_PK.split("_").length > 1) {
        isFake = true;
    }

    const updatePOS = document.createElement("input");
    updatePOS.name = "updatePOS";
    updatePOS.id = "editPOSName";

    if (getEditUpdate(posData.pos_id_PK) !== null) {
        updatePOS.value = getEditUpdate(posData.pos_id_PK);
        posbtn.setAttribute("edited", "");
    } else updatePOS.value = posData.part_of_speech;

    updatePOS.oninput = () => {
        posbtn.textContent = updatePOS.value.trim();
        if (isFake) {
            addCreateUpdate(
                "pos",
                null,
                updatePOS.value.trim(),
                posData.pos_id_PK
            );
        } else {
            addEditUpdate(posData.pos_id_PK, updatePOS.value.trim());
            if (isEditUpdate(posData.pos_id_PK))
                posbtn.setAttribute("edited", "");
            else posbtn.removeAttribute("edited");
        }
    };

    target.appendChild(updatePOS);

    const hr = document.createElement("hr");
    hr.className = "sep-info";
    target.appendChild(hr);

    definitions.forEach((def) => {
        const wraper = document.createElement("div");
        wraper.className = "card-edit-value-wraper";

        const textarea = document.createElement("textarea");

        if (getEditUpdate(posData.pos_id_PK, def.definition_id_PK) !== null) {
            textarea.value = getEditUpdate(
                posData.pos_id_PK,
                def.definition_id_PK
            );
            console.log(getEditUpdate(posData.pos_id_PK, def.definition_id_PK));

            textarea.setAttribute("edited", "");
        } else textarea.value = def.definition;

        textarea.className = "definition-edit-input";
        textarea.style = "field-sizing: content; resize: none;";
        textarea.oninput = () => {
            addEditUpdate(
                posData.pos_id_PK,
                null,
                def.definition_id_PK,
                textarea.value.trim()
            );
            if (isEditUpdate(posData.pos_id_PK, def.definition_id_PK))
                textarea.setAttribute("edited", "");
            else textarea.removeAttribute("edited");
        };

        const delBtn = document.createElement("button");
        delBtn.className = "edit-del-btn";
        delBtn.textContent = "󰆴";

        if (isDeleteUpdate(posData.pos_id_PK, def.definition_id_PK)) {
            textarea.setAttribute("deleted", "");
            delBtn.setAttribute("deleted", "");
        } else if (isEditUpdate(posData.pos_id_PK, def.definition_id_PK)) {
            textarea.setAttribute("edited", "");
            delBtn.setAttribute("edited", "");
        }
        delBtn.addEventListener("click", (e) => {
            e.preventDefault();

            if (delBtn.hasAttribute("deleted")) {
                delBtn.removeAttribute("deleted");
                textarea.removeAttribute("deleted");
                removeDeleteUpdate(
                    posData.pos_id_PK,
                    null,
                    def.definition_id_PK
                );
            } else {
                delBtn.setAttribute("deleted", "");
                textarea.setAttribute("deleted", "");
                addDeleteUpdate(posData.pos_id_PK, null, def.definition_id_PK);
            }
        });

        wraper.appendChild(textarea);
        wraper.appendChild(delBtn);

        target.appendChild(wraper);
    });

    // Display create Data
    const createdDefinitions = getCreateDefUpdate(posData.pos_id_PK);
    console.log(createdDefinitions);

    createdDefinitions.forEach((def) => {
        const wraper = document.createElement("div");
        wraper.className = "card-edit-value-wraper";

        const textarea = document.createElement("textarea");
        textarea.value = def.updatedContent;
        textarea.className = "definition-edit-input";
        textarea.setAttribute("created", "");
        textarea.style = "field-sizing: content; resize: none;";
        textarea.oninput = () => {
            addCreateUpdate(
                "definition",
                posData.pos_id_PK,
                textarea.value.trim(),
                def.uuid
            );
        };

        const delBtn = document.createElement("button");
        delBtn.className = "edit-del-btn";
        delBtn.textContent = "󰆴";

        delBtn.addEventListener("click", (e) => {
            e.preventDefault();
            removeCreateUpdate(
                "definition",
                posData.pos_id_PK,
                textarea.value.trim(),
                def.uuid
            );
            wraper.remove();
        });

        wraper.appendChild(textarea);
        wraper.appendChild(delBtn);

        target.appendChild(wraper);
    });
}

let currentPos = null;
const cardCache = new Map();
const postCache = new Map();

function clearCardCache() {
    cardCache.clear();
    postCache.clear();
}

function createCache(cardData) {
    cardCache.set("cardId", cardData.card_id_PK);
    cardCache.set("cardName", cardData.card_word);
    cardCache.set("pos", cardData.pos);

    postCache.set("newCardName", cardData.card_word);
    postCache.set("deleteData", []);
    postCache.set("editData", []);
    postCache.set("addData", []);
}

function updatedCardName(cardName) {
    postCache.set("newCardName", cardName);
}

async function prepareCardData() {
    const payload = JSON.stringify(Object.fromEntries(postCache));

    const match = window.location.pathname.match(
        /^\/workspace\/decks\/([^/]+)\/([^/]+)/
    );
    if (!match) return;

    const deckId = match[1];
    const levelId = match[2];

    const response = await fetchWithAuth(
        `/api/decks/${deckId}/${levelId}/${cardCache.get("cardId")}/update`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: payload,
        }
    );

    closePopup();

    if (response.ok) {
        loadPage(window.location.pathname);
    }
}

function addEditUpdate(posId, newPos, defId, newDef) {
    const editData = postCache.get("editData");
    const posList = cardCache.get("pos"); // old cache

    // find old pos entry
    const oldPos = posList.find((p) => p.pos_id_PK === String(posId));
    if (!oldPos) {
        console.warn("No matching pos in cardCache for posId:", posId);
        return;
    }

    let posEntry = editData.find((e) => e.posId === posId);
    if (!posEntry) {
        posEntry = { posId: posId, newPos: null, definitions: [] };
        editData.push(posEntry);
    }

    if (newPos !== undefined) {
        if (newPos !== oldPos.part_of_speech) {
            posEntry.newPos = newPos;
        } else {
            // revert back if matches old one
            posEntry.newPos = null;
        }
    }

    if (defId !== undefined) {
        const oldDef = oldPos.definitions.find(
            (d) => d.definition_id_PK === String(defId)
        );
        if (!oldDef) {
            console.warn(
                "No matching definition in cardCache for defId:",
                defId
            );
            return;
        }

        let defEntry = posEntry.definitions.find((d) => d.defId === defId);

        if (newDef !== undefined) {
            if (newDef !== oldDef.definition) {
                if (!defEntry) {
                    defEntry = { defId: defId, newDef: newDef };
                    posEntry.definitions.push(defEntry);
                } else {
                    defEntry.newDef = newDef;
                }
            } else {
                // matches old, remove if exists
                if (defEntry) {
                    posEntry.definitions = posEntry.definitions.filter(
                        (d) => d.defId !== defId
                    );
                }
            }
        }
    }

    // ----- cleanup -----
    if (!posEntry.newPos && posEntry.definitions.length === 0) {
        const idx = editData.findIndex((e) => e.posId === posId);
        if (idx !== -1) editData.splice(idx, 1);
    }

    postCache.set("editData", editData);
    console.log("editData:", editData);
}

function getEditUpdate(posId, defId) {
    const editData = postCache.get("editData") || [];

    const posEntry = editData.find((e) => e.posId === posId);
    if (!posEntry) return null;

    if (defId === undefined) {
        // check pos-level update only
        return posEntry.newPos ? posEntry.newPos : null;
    }

    // check definition-level update
    const defEntry = posEntry.definitions.find((d) => d.defId === defId);
    return defEntry ? defEntry.newDef : null;
}

function addDeleteUpdate(posId, isPos, defId) {
    const deleteData = postCache.get("deleteData");

    let posEntry = deleteData.find((e) => e.posId === posId);

    if (!posEntry) {
        posEntry = {
            posId: posId,
            isPos: isPos === null ? false : isPos,
            definitions: [],
        };
        deleteData.push(posEntry);
    }

    if (isPos) {
        posEntry.isPos = isPos;
    }

    if (
        !(isPos === null ? false : isPos) &&
        defId !== undefined &&
        !posEntry.definitions.find((d) => d.defId === defId)
    ) {
        posEntry.definitions.push({
            defId: defId,
        });
    }

    postCache.set("deleteData", deleteData);
    console.log(deleteData);
}

function removeDeleteUpdate(posId, isPos, defId) {
    let deleteData = postCache.get("deleteData");

    let posIndex = deleteData.findIndex((e) => e.posId === posId);
    if (posIndex === -1) return;

    let posEntry = deleteData[posIndex];

    if (isPos) {
        deleteData[posIndex].isPos = false;
        if (posEntry.definitions.length === 0) {
            deleteData.splice(posIndex, 1);
        }
    } else if (defId !== undefined) {
        // remove a specific def
        posEntry.definitions = posEntry.definitions.filter(
            (d) => d.defId !== defId
        );

        if (posEntry.definitions.length === 0 && !posEntry.isPos) {
            deleteData.splice(posIndex, 1);
        }
    }

    postCache.set("deleteData", deleteData);
    console.log(deleteData);
}

function isDeleteUpdate(posId, defId) {
    const deleteData = postCache.get("deleteData");
    if (!deleteData) return false;

    const posEntry = deleteData.find((e) => e.posId === posId);
    if (!posEntry) return false;

    if (defId !== undefined) {
        return posEntry.definitions.some((d) => d.defId === defId);
    }

    return false;
}

function isEditUpdate(posId, defId) {
    const editData = postCache.get("editData");
    if (!editData) return false;

    const posEntry = editData.find((e) => e.posId === posId);
    if (!posEntry) return false;

    if (defId === undefined) {
        // checking pos-level edit
        return posEntry.newPos !== null;
    } else {
        // checking definition-level edit
        return posEntry.definitions.some((d) => d.defId === defId);
    }
}

function addCreateUpdate(dataType, targetId, updatedContent, uuid) {
    const addData = postCache.get("addData") || [];

    // check if there is already an entry with the same dataType and targetId
    const existing = addData.find(
        (d) =>
            d.dataType === dataType &&
            d.targetId === targetId &&
            d.uuid === uuid
    );

    if (existing) {
        // update the content
        existing.updatedContent = updatedContent;
    } else {
        // otherwise, add a new entry
        addData.push({
            dataType: dataType,
            targetId: targetId,
            updatedContent: updatedContent,
            uuid: uuid,
        });
    }

    postCache.set("addData", addData);
}

// ----- REMOVE CREATE DATA -----
function removeCreateUpdate(dataType, targetId, updatedContent, uuid) {
    let addData = postCache.get("addData") || [];

    if (dataType === "pos") {
        // remove the POS itself
        addData = addData.filter(
            (d) =>
                !(
                    d.dataType === "pos" &&
                    d.targetId === targetId &&
                    d.updatedContent === updatedContent &&
                    d.uuid === uuid
                )
        );

        // cascade: remove all definitions that reference this POS
        addData = addData.filter(
            (d) => !(d.dataType === "definition" && d.targetId === uuid)
        );
        console.log(addData);
    } else {
        // remove a specific definition or other types
        addData = addData.filter(
            (d) =>
                !(
                    d.dataType === dataType &&
                    d.targetId === targetId &&
                    d.updatedContent === updatedContent &&
                    d.uuid === uuid
                )
        );
    }

    postCache.set("addData", addData);
}

// ----- GET CREATE DATA FOR DEFINITIONS -----
function getCreateDefUpdate(posId) {
    const addData = postCache.get("addData") || [];
    // filter only "definition" type entries for the given posId
    return addData.filter(
        (d) => d.dataType === "definition" && d.targetId === posId
    );
}
