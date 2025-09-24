<script src="/js/workspace/page-fixer.js"></script>
<h1>Explore Public Decks</h1>
<p>Browse and fork public decks to add them to your collection.</p>
<hr style="height: 4px; background-color: var(--primary-font-color);">
<div class="explore-header" style="display: flex; justify-content: space-between; margin-bottom: 30px;">
    <div style="width: 50%;">
        <input id='explore-search-bar' type="text" placeholder="Search bar">
    </div>

    <div class="explore-right">
        <button onclick="openSortOption()" class="explore-sort-selected" id="selected-value">
            <div>Select sort</div>
            <i class="nf nf-cod-arrow_down"></i>
        </button>
        <ul id="sort-options">
            <li class="sort-option" data-value="name-asc">A-Z</li>
            <li class="sort-option" data-value="name-dsc">Z-A</li>
            <li class="sort-option" data-value="most-viewed">Most Liked</li>
            <li class="sort-option" data-value="newest">Newest</li>
        </ul>
    </div>
</div>

<deck-wraper style="border-radius: 10px; display: flex; width: 100%; justify-content: center;">
    <public-decks-container style="display: flex;">
        <explore-template
            style="display: none; width: max-content; padding: 20px; gap: 20px; flex-direction: row; background: var(--menu-primary-color); border: var(--content-border); border-radius: 10px;">
            <div id="explore-decks-container" style="justify-content: start;">
                <card-container class="card-item-container">
                    <svg width="174" height="237" viewBox="0 0 174 237" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <rect x="0.5" y="0.5" width="172.17" height="235.93" rx="7.5"
                            fill="url(#paint0_linear_1143_804)" />
                        <rect x="0.5" y="0.5" width="172.17" height="235.93" rx="7.5" stroke="white" />
                        <path
                            d="M86.6211 81C93.9544 81 99.8711 82.375 104.371 85.125C108.871 87.875 111.121 92.3333 111.121 98.5C111.121 101.75 110.454 104.792 109.121 107.625C107.871 110.375 106.329 112.833 104.496 115C102.746 117.083 100.371 119.583 97.3711 122.5C93.6211 126.167 90.8711 129.208 89.1211 131.625C87.3711 133.958 86.4961 136.583 86.4961 139.5C86.1628 139.833 85.4961 140.125 84.4961 140.375C83.5794 140.542 82.7044 140.625 81.8711 140.625C79.8711 140.625 78.3711 140.125 77.3711 139.125C76.4544 138.125 75.9961 136.625 75.9961 134.625C75.9961 132.542 76.6628 130.5 77.9961 128.5C79.3294 126.5 81.4961 123.75 84.4961 120.25C88.2461 116 91.1211 112.25 93.1211 109C95.1211 105.75 96.1211 102.25 96.1211 98.5C96.1211 95.5 95.2878 93.0833 93.6211 91.25C92.0378 89.4167 89.5794 88.5 86.2461 88.5C83.3294 88.5 80.9961 89.2083 79.2461 90.625C77.4961 92.0417 76.6211 94.2083 76.6211 97.125C76.6211 98.625 76.9961 100.667 77.7461 103.25C76.4961 104.167 74.6628 104.625 72.2461 104.625C69.0794 104.625 66.5378 103.833 64.6211 102.25C62.7878 100.667 61.8711 98.3333 61.8711 95.25C61.8711 90 64.3711 86.3333 69.3711 84.25C74.3711 82.0833 80.1211 81 86.6211 81ZM81.8711 152.75C84.6211 152.75 86.8294 153.417 88.4961 154.75C90.1628 156.083 90.9961 158 90.9961 160.5C90.9961 163.083 90.0794 165.042 88.2461 166.375C86.4961 167.792 84.2461 168.5 81.4961 168.5C78.7461 168.5 76.5378 167.833 74.8711 166.5C73.2044 165.167 72.3711 163.25 72.3711 160.75C72.3711 158.167 73.2461 156.208 74.9961 154.875C76.8294 153.458 79.1211 152.75 81.8711 152.75Z"
                            fill="white" />
                        <defs>
                            <linearGradient id="paint0_linear_1143_804" x1="175.5" y1="-6.99999" x2="-2.5" y2="243"
                                gradientUnits="userSpaceOnUse">
                                <stop stop-color="#1D1D1D" />
                                <stop offset="0.519231" stop-color="#373A4C" />
                                <stop offset="1" stop-color="#1D1D1D" />
                            </linearGradient>
                        </defs>
                    </svg>
                    <div class="hoverOverlay"></div>
                </card-container>
            </div>
            <deck-content
                style="display: flex; flex-direction: column; align-items: center; width: 300px; padding: 0 10px; gap: 5px; border-left: var(--content-border); justify-content: space-between">
                <div id="deck-detail-container" style="width: 100%; display: flex; flex-direction: column; gap: 10px;">
                    <div style="display: flex; flex-direction: column; width: 100%;">
                        <deck-title style="font-size: 24px; font-weight: bold;">No Selection</deck-title>
                        <deck-author>by Unknown</deck-author>
                    </div>
                    <deck-description style="font-size: 14px; margin-top: 10px;">
                        This is a public deck. Fork it to add to your collection!
                    </deck-description>
                    <deck-interactions style="display: flex; flex-direction: column; width: 100%; font-size: 14px;">
                        <deck-total-card>
                            Containing <total-cards>0</total-cards>
                        </deck-total-card>
                        <deck-total-forked>
                            Forked <total-forked>0</total-forked> times
                        </deck-total-forked>
                    </deck-interactions>
                </div>
                <div style="display: flex; justify-content: space-between; width: 100%;">
                    <liked-value style="cursor: pointer;">0 <i class="nf nf-md-thumb_up"></i></liked-value>
                    <dislikes-value style="cursor: pointer;">0 <i class="nf nf-md-thumb_down"></i></dislikes-value>
                </div>
                <like-value-bar
                    style="width: 100%; height: 5px; background-color: var(--content-border); display: flex; gap: 0; overflow: hidden; border-radius: 10px;">
                    <div id="like-bar-fill" style="height: 5px; background-color: var(--like-color); width: 50%;"></div>
                    <div id="dislike-bar-fill" style="height: 5px; background-color: var(--dislike-color); width: 50%;">
                    </div>
                </like-value-bar>
                <button id="fork-deck-button"
                    style="width: 100%; padding: 5px; border: none; border-radius: 10px; background-color: #5050a1; color: var(--background-color); font-size: 18px; cursor: pointer;">
                    Fork
                </button>

            </deck-content>
        </explore-template>
    </public-decks-container>
</deck-wraper>