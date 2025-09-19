<div id="workspace-right-bar">
    <h4 style="margin-right: 50px;">Configuration</h4>
    <div class="right-decks-style">
        <div>Decks</div>
        <div style="display: flex; gap: 5px;">
            <!-- display none while user doesn't select deck yet -->
            <div id="choose-deck">1</div>
            <div id="choose-deck">/</div>

            <div id="all-decks">1</div>
        </div>
    </div>

    <div class="right-card-session">
        <div>Card Per Session</div>
        <input type="number" style="width: 50px; height: 25px;">
    </div>

    <!-- <div class="right-font-size">
        <div>Font size</div>
        <input type="number" style="width: 50px; height: 25px;">
    </div> -->

    <div class="right-auto-timer">
        <div class="auto-timer">
            <div>Auto-flip timer</div>
            <div id="auto-time-changer">
                <div>Off</div>
                <div class="timer-unicode">&#129170;</div>
            </div>
        </div>

        <div class="timer-options">
            <div data-value="off">Off</div>
            <div data-value="custom">Custom
                <input type="number" min="1" class="custom-input" placeholder="sec"
                    style="width: 50px; height: 25px; float: right;">
            </div>
        </div>
    </div>

    <div class="review-mode">
        <div class="review-selection">
            <div class="">Review Mode</div>
            <div id="review-changer">
                <div>Randomize</div>
                <div class="review-unicode">&#129170;</div>
            </div>
        </div>

        <div class="review-options">
            <div data-value="FSRS">FSRS</div>
            <div data-value="Randomize">Randomize</div>
        </div>
    </div>

    <a style="cursor: pointer; user-select: none;" id="start-review">
        Review Deck(s)</a>
</div>