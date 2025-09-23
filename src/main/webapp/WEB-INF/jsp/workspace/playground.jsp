<script src="/js/workspace/page-fixer.js"></script>
<style>
    #content {
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
<canvas id="background-play" style="padding: 0;"></canvas>
<div
    style="z-index: 1;padding: 24px; display: flex; height: 100%; flex-direction: column; justify-content: space-between; position: relative;">
    <div class="playground-header">
        <review-timer style="font-size: 22px;">00:00</review-timer>
        <card-countdown style="display: none; font-size: 22px;">Next Card in : 5 sec</card-countdown>
        <div style="display: flex; gap: 5px;">
            <remainding-cards style="font-size: 22px;">0</remainding-cards>
            <p style="font-size: 22px;">/</p>
            <total-cards style="font-size: 22px;">0</total-cards>
            <p style="font-size: 22px;">Cards</p>
        </div>
    </div>

    <!-- Showing Definition Zone -->
    <card-prompt-area style="width: 100%; height: 50vh; display: flex; justify-content: center;">
        <card-data
            style="width: 70%; background: rgb(41, 41, 41); padding: 10px; border-radius: 10px; overflow-y: auto;">

        </card-data>
    </card-prompt-area>

    <!-- Card Selection zone -->
    <card-answer-selector style="display: flex; justify-content: center; gap: 10px;">
    </card-answer-selector>
</div>