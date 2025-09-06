<script src="/js/workspace/page-fixer.js"></script>
<script>
    function startHideTimer() {
        hideTimeout = setTimeout(() => {
            document.getElementById('sort-options').style.display = "none";
        }, 3000);
    }

    function openSortOption() {
        document.getElementById('sort-options').style.display = document.getElementById('sort-options').style.display === "block" ? "none" : "block";
        startHideTimer();
    }
</script>

<div style=" padding-left: 10px; padding-right: 10px; padding-bottom: 8px;">
    <div style="font-size: 36px;">Review</div>
    <p>Choose your review style</p>
</div>
<div style="width: 50%; padding-left: 10px; padding-bottom: 8px; height: 48px;"><input id='review-search-bar'
        type="text" placeholder="Search bar">
</div>
<div class="review-header">

    <!-- TODO:<make div select dropdown due to option cannot remove outline> -->
    <!-- dropdown start -->
    <div class="review-left">
        <button onclick="openSortOption()" class="review-sort-selected" id="selected-value">
            <div>Select sort</div>
            <i class="nf nf-cod-arrow_down"></i>
        </button>
        <ul id="sort-options">
            <!-- no 's' -->
            <li class="sort-option" data-value="name-asc">A-Z</li>
            <li class="sort-option" data-value="name-dsc">Z-A</li>
            <li class="sort-option" data-value="fork-deck">Fork deck</li>
            <li class="sort-option" data-value="own-deck">Owned deck</li>
        </ul>
    </div>


    <div class="review-right">
        <!-- Create is first -->
        <a href="#" class="review-create">
            <div>Create Deck</div>
        </a>
        <a href="#">
            <div>Fork Decks</div>
        </a>
    </div>

    <!-- Showing Deck Session -->
</div>
<div id="forked-decks-container owned-decks-container"></div>