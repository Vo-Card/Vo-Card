<div style="width: 100%;
            height: 100%;">
    <form id="user-setting" method="post" autocomplete="off">
        <div class="user-setting-inputs">
            <p style="width: 25%; margin: 0px;">Displayname :</p>
            <input style="padding-left: 10px;" name="displayName" id="setting-display-name" type="text"
                placeholder="New Displayname">
        </div>

        <div class="user-setting-inputs">
            <p style="width: 25%; margin: 0px;">Username :</p>
            <input style="padding-left: 10px;" name="username" id="setting-username" type="text"
                placeholder="New Username">
        </div>

        <div class="user-setting-inputs">
            <p style="width: 25%; margin: 0px;">New Password :</p>
            <input style="padding-left: 10px;" name="newPassword" id="setting-new-password" type="password"
                placeholder="New Password">
        </div>

        <div class="user-setting-inputs">
            <p style="width: 25%; margin: 0px;">Current Password :</p>
            <input style="padding-left: 10px;" name="confirmPassword" id="setting-old-password" type="password" required
                placeholder="Current Password">
        </div>
        <p id="setting-message"></p>
        <div style="width: 100%; 
                padding-top: 10px; 
                margin-top: 10px; 
                padding-bottom: 10px; 
                display: flex; 
                justify-content: space-between;
                border-top: 1px solid white;">
            <delete-user style="background-color: #972626;
                ">Delete Account</delete-user>

            <div style="display: flex; gap: 6px;">
                <a id="setting-logout" href="/logout" style="background-color: #C26262;">Logout</a>

                <input type="submit" id="setting-submit" value="Save"
                    style="background-color: #628AC2; width: fit-content;">
            </div>
        </div>
    </form>

</div>