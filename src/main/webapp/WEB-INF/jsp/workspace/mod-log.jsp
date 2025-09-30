<script src="/js/workspace/page-fixer.js"></script>

<mod-menu>
    <h2>Moderation Log</h2>

    <!-- Reuse -->
    <root-div class="mod-header">
        <h4>Moderation auto log / User</h4>
        <input style="width: 50%;" type="text" id="search-roles" onkeyup="" placeholder="Search for roles..">
    </root-div>

    <moderation-log>
        <div class="log-header">
            <div class="title-name">Username</div>
            <div class="title-action-type">Action type</div>
            <div class="title-message">Message</div>
            <div class="title-timestamp">Time Stamp</div>
        </div>

        <!-- Start Log item -->
        <div class="log-item">
            <div id="log-name">{Username}</div>
            <div id="log-action">{Action Type}</div>
            <div id="log-message">{Message}</div>
            <div id="log-timestamp">{Time Stamp}</div>
        </div>
    </moderation-log>
</mod-menu>