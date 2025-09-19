<script src="/js/workspace/page-fixer.js"></script>
<style>
    #content,
    #content * {
        position: relative;
        padding: 0;
        overflow: hidden;
    }

    #background-play {
        width: 100vw;
        height: 100vh;
        background-color: #151620;
        position: absolute;
        z-index: 0;
        overflow: hidden;
    }

    .card-item-container svg {
        position: absolute !important;
    }
</style>
<canvas id="background-play"></canvas>
<div style="padding: 24px; display: flex; height: 100%; flex-direction: column; justify-content: space-between;">

    <!-- Timer and Card -->
    <div class="playground-header">
        <div>{Timer}</div>
        <div style="display: flex; gap: 5px;">
            <div>{Current Card}</div>
            <div>/</div>
            <div>{Card Per Session}</div>
        </div>
    </div>

    <!-- Showing Card Zone -->
    <div class="play-card-zone" id="card-placeholder"></div>


    <div class="play-definition">
        <div>Definition placeholder</div>
    </div>
    <!-- Vote zone -->
    <div class="vote-container">
        <div id="vote-cover"></div>
        <div id="vote-cards"></div>
    </div>
</div>