<script src="/js/workspace/page-fixer.js"></script>
<div class="grid-container">
    <div class="greeting content-container"
        style="display: flex; flex-direction: column; justify-content: space-between;">
        <div>
            <p class="font-fixed" id="welcome-message"></p>
            <p>Build decks for any subject and review them with proven spaced repetition. Each session helps you
                remember more, while progress tracking keeps you motivated. Learning becomes easier when you can see how
                far you’ve already come.</p>
        </div>
        <div class="px-3 pb-2">
            <div class="d-flex gap-2">
                <a href="/workspace/review">
                    <p>[ Get Started ]</p>
                </a>
                <a href="/workspace/decks">
                    <p>[ Create your own deck ]</p>
                </a>
            </div>
        </div>
    </div>

    <div class="daily content-container">
        <div class="pt-4  pb-2">
            <p class="font-fixed text-center">Your today recent card!</p>
        </div>

        <daily-card-container style="width: 100%; display: flex; justify-content: center;">

        </daily-card-container>
    </div>

    <!-- chart recently 7 days -->
    <section class="stats content-container">
        <div class="light"></div>
        <div class="p-2 pt-3 ps-2">
            <p class="font-fixed">Stat</p>
        </div>
        <div class="p-2 ps-3">
            <p>Showing your stat recently 7 days</p>
        </div>
        <!-- TODO: <Create charts> -->
        <canvas class="p-3 px-2 w-100" id="weekly-bar" style="height: 300px;"></canvas>
    </section>
</div>