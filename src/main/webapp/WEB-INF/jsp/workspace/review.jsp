<script src="/js/workspace/page-fixer.js"></script>
<div style=" padding : 0 10px;">
    <h1>Review</h1>
    <p>Choose your Deck(s) to start playing! </p>
</div>
<hr style="height: 4px; background-color: var(--primary-font-color);">
<h3>Selected Decks</h3>
<selected-card
    style="border: var(--content-border); border-radius: 10px; display: flex; width: 100%; height: calc(var(--card-height) + 44px); background-color: var(--menu-primary-color); overflow-x: auto; overflow-y: hidden;">
    <selected-card-container style="padding: 20px; display: flex; justify-content: center; gap: 10px;">

    </selected-card-container>
</selected-card>
<br>
<h3>All Decks</h3>
<div class="review-header" style="display: flex;">

    <div style="width: 50%;">
        <input id='review-search-bar' type="text" placeholder="Search bar">
    </div>

    <div class="review-left">
        <button onclick="openSortOption()" class="review-sort-selected" id="selected-value">
            <div>Select sort</div>
            <i class="nf nf-cod-arrow_down"></i>
        </button>
        <ul id="sort-options">
            <li class="sort-option" data-value="name-asc">A-Z</li>
            <li class="sort-option" data-value="name-dsc">Z-A</li>
            <li class="sort-option" data-value="fork-deck">Forked Decks</li>
            <li class="sort-option" data-value="own-deck">Owned Deck</li>
        </ul>
    </div>
</div>
<deck-wraper
    style="border: var(--content-border); border-radius: 10px; display: flex; width: 100%; min-height: calc(var(--card-height) + 44px); background-color: var(--menu-primary-color);">
    <div id="decks-container" style="justify-content: start;"></div>
</deck-wraper>