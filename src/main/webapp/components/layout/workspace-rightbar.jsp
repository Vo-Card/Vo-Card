<form id="workspace-right-bar">
    <h4>Configuration</h4>
    <div class="tt-decks crd-ss-group" style="display: flex;">
        <p>Decks</p>
        <div style="display: flex; gap: 5px;">
            <selected-decks>0</selected-decks>
            <p> / </p>
            <maximum-decks>10</maximum-decks>
        </div>
    </div>

    <session-settings>
        <h5>Session Setting</h5>
        <label class="crd-ss-group">
            <div>
                <p style="margin: 0;">Card Per Session</p>
                <p>[Cards]</p>
            </div>
            <input name="total-session" min="5" type="number" style="width: 50px; height: 25px;" value="10">
        </label>
        <label class="crd-ss-group">
            <div>
                <p style="margin: 0;">Set Time Limit</p>
                <p>[Mins]</p>
            </div>
            <input name="timelimit" placeholder="mins" min="1" type="number" style="width: 50px; height: 25px;"
                value="15">
        </label>
    </session-settings>

    <session-mode-selection>
        <h5>Learning Mode</h5>
        <p style="color: rgb(103, 110, 120);">Unavailable</p>
        <p style="padding: 5px; border-radius: 5px; background-color: var(--menu-button-active);"><span
                style="margin-right: 10px;"> </span>Random Mode</p>
        <p style="padding: 5px; border-radius: 5px; background-color: var(--menu-button-hover);"><span
                style="margin-right: 10px;"> </span>Difficulty Mode</p>
    </session-mode-selection>

    <input id="start-review-button" type="submit" value="Start Learning!">
</form>