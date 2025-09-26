<form style="display: flex; width: 100%; height: 100%; flex-direction: column; gap: 10px;">
    <div style="display: flex; width: 100%; align-items: center;">
        <p style="margin: 0; white-space: nowrap;">Word : </p>
        <input type="text" name="cardName" id="edit-card-name-input">
    </div>

    <div style="display: flex; width: 100%; height: 80%; gap: 10px;">
        <pos-container class="edit-value-container" style="width: 25%;">
            <pos-values style="display: flex; width: 100%; flex-direction: column; gap: 5px;">

            </pos-values>
            <button class="popup-card-create-buttons" style="margin-top: 5px;">Add POS</button>
        </pos-container>
        <def-container class="edit-value-container" style="width: 75%;">
            <def-values style="display: flex; width: 100%; flex-direction: column; gap: 5px;">

            </def-values>
            <button class="popup-card-create-buttons" style="margin-top: 5px;">New Definition</button>
        </def-container>
    </div>

    <div style="display: flex; justify-content: space-between;">
        <button cancel-btn class="btn btn-secondary"
            style="min-width: fit-content; width: 20%; height: 34px; border: none; border-radius: 5px;">Cancel</button>
        <input type="submit" style="min-width: fit-content; width: 20%;">
    </div>
</form>