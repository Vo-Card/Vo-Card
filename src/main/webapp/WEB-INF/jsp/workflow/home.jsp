<script src="/js/workflow/page-fixer.js"></script>
<div class="grid-container">
    <div class="greeting content-container">
        <p class="font-fixed">{daily_welcome_message}, {username}!</p>
        <p>Create your own flashcard decks, review with spaced repetition, and track
            your
            progess .</p>
        <div class="px-3 pb-2">
            <div class="d-flex gap-2">
                <a href="#">
                    <p>[ Get Started ]</p>
                </a>
                <a href="#">
                    <p>[ Create your own deck ]</p>
                </a>
            </div>
        </div>
    </div>

    <div class="daily content-container">
        <div class="pt-4  pb-2">
            <p class="font-fixed text-center">Your Today Card</p>
        </div>

        <div class="preview card-wel" id="card_preview">
            <div class="deck default a1" style="z-index: 1;">
                <div class="background">
                    <p class="category">A1</p>
                    <div class="decorations">
                        <div>
                            <span
                                style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;"></span>
                            <span
                                style="background-color: white; width: 70px; height: 2px; display: block; position: relative; top :10px;"></span>
                            <span
                                style="background-color: white; width: 2px; height: 100px; display: block; position: relative; bottom :12px; left: 50px;"></span>
                        </div>
                        <span
                            style="background-color: white; width: 10px; height: 10px; display: block; position: relative; left: 60px;">
                    </div>
                </div>
                <p class="word">Something</p>
            </div>
        </div>
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
        <canvas class="p-3 px-2 w-100" id="chartz" style="height: 300px;"></canvas>
    </section>
</div>
