<script src="/js/workspace/page-fixer.js"></script>

<div class="stats-header">Your Stat</div>
<div class="stats-grid">

    <!-- First Grid Section -->
    <div class="most-learn content-container">
        <div class="most-learn-deck-head">Most learn decks</div>
        <div class="most-learn-deck">Vocab Vault</div>
        <div class="most-learn-deck">English Vocabulary – Beginner</div>
        <div class="most-learn-deck">Common English Phrases</div>
        <div class="most-learn-deck">English Edge</div>
        <div class="most-learn-deck">PhraseFlash</div>
        <div class="most-learn-deck">English Idioms & Expressions</div>
    </div>

    <div class="monthly-normal content-container">
        <div class="monthly-stats-head">Monthly stats : Normal</div>
        <div class="monthly-stats-item">
            <div>Total Card learned</div>
            <div>100 cards</div>
        </div>

        <div class="monthly-stats-item">
            <div>Easy</div>
            <div class="vote">
                <div id="EASY-VOTE">20</div>
                <div>cards</div>
            </div>
        </div>

        <div class="monthly-stats-item">
            <div>Good</div>
            <div class="vote">
                <div id="GOOD-VOTE">35</div>
                <div>cards</div>
            </div>
        </div>

        <div class="monthly-stats-item">
            <div>Hard</div>
            <div class="vote">
                <div id="HARD-VOTE">25</div>
                <div>cards</div>
            </div>
        </div>

        <div class="monthly-stats-item">
            <div>Again</div>
            <div class="vote">
                <div id="AGAIN-VOTE">20</div>
                <div>cards</div>
            </div>
        </div>

        <div id="monthly-stats-normal"></div>
    </div>

    <!-- Second Grid Second -->
    <div class="monthly-gamemode content-container">
        <div class="monthly-header">Monthly Stats: Definetion to word</div>
        <div class="monthly-gamemode-items">
            <p>Total card played</p>
            <total-cards>0 cards</total-cards>
        </div>

        <div class="monthly-gamemode-items">
            <p>Total correct answer</p>
            <total-correct>0 cards</total-correct>
        </div>

        <div class="monthly-gamemode-items">
            <p>Total failed answer</p>
            <total-failed>0 cards</total-failed>
        </div>

        <div class="monthly-gamemode-items">
            <p>Total out of time</p>
            <total-outoftime>0 cards</total-outoftime>
        </div>

        <div class="monthly-gamemode-items">
            <p>Average</p>
            <average-passing>55 %</average-passing>
        </div>

        <div id="total-stats" style="
    display: flex;
    height: 17px;
    overflow: hidden;
    border-radius: 10px;
"></div>

    </div>

    <div class="total-played content-container">
        <div>Total played</div>
        <div>Every session you play</div>
        <p>10</p>
        <div>Times</div>
    </div>

    <!-- Third Grid Section -->
    <div class="stats_7d content-container">
        <div class="stats-header">Stat Review</div>
        <p>Reviewed Stats: Monday–Sunday</p>
        <canvas class="stats-canvas" id="weekly-bar"></canvas>
    </div>
</div>