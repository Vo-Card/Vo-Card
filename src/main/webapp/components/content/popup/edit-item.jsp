<!-- This template will only use for Deck and Level edit -->

<div style="
    display: flex;
    width: 100%;
    height: 100%;
    justify-content: space-between;
    align-items: center;">
    <div style="width: 60%; display: flex; justify-content: center;">
        <card-container class="card-item-container">
        </card-container>
    </div>
    <div style="width: 2px; height: 100%; background-color: white;"></div>
    <edit-input-area
        style="width: 100%; display: flex; justify-content: left; height: 100%; padding: 10px; flex-direction: column; overflow-y: auto;">
        <form id="levelEditForm" style="display: none;">
            <div><label for="levelValue">Level Name :</label><br><input type="text" name="levelValue" id="levelValue">
            </div>
            <div><label for="levelWeight">Level Weight :</label><br><input type="number" name="levelWeight"
                    id="levelWeight"></div>
            <div><label for="lvlPrimaryColor">Primary Color :</label><br><input type="text" name="primaryColor"
                    id="lvlPrimaryColor" data-coloris=""></div>
            <div><label for="lvlSecondaryColor">Secondary Color :</label><br><input type="text" name="secondaryColor"
                    id="lvlSecondaryColor" data-coloris=""></div>
            <div><br><input type="submit" value="Save" name="undefined" id="undefined"></div>
        </form>
        <form id="deckEditForm">
            <div><label for="deckName">Deck Name :</label><br><input type="text" name="deckName" id="deckName"></div>
            <div><label for="deckDescription">Description :</label><br><textarea name="deckDescription"
                    id="deckDescription"></textarea></div>
            <div><label for="deckPrimaryColor">Primary Color :</label><br><input type="text" name="primaryColor"
                    id="deckPrimaryColor" data-coloris=""></div>
            <div><label for="deckSecondaryColor">Secondary Color :</label><br><input type="text" name="secondaryColor"
                    id="deckSecondaryColor" data-coloris=""></div>
            <div class="info-wraper" style="height: 40px; align-items: center; margin: 10px 0px 0px;">
                <p style="margin: 0px;">Set Public</p>
                <div class="checkbox-wrapper-59"><label class="switch"><input type="checkbox" id="deckIsPublic"
                            name="isPublic"><span class="slider"></span></label></div>
            </div>
            <div class="info-wraper" style="height: 40px; align-items: center; margin: 10px 0px 0px;">
                <p style="margin: 0px;">Set Clone</p>
                <div class="checkbox-wrapper-59"><label class="switch"><input type="checkbox" id="deckAllowCloning"
                            name="allowCloning"><span class="slider"></span></label></div>
            </div>
            <div><br><input type="submit" value="Save" name="undefined" id="undefined"></div>
        </form>
    </edit-input-area>
</div>