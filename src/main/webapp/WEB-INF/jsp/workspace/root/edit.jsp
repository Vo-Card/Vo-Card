<script src="/js/workspace/page-fixer.js"></script>

<root-menu>
    <h2>Root Create / Edit</h2>
    <root-div>
        <h4>Role -</h4>
        <h4>Name</h4>
    </root-div>

    <permission-setting>
        <!-- left side -->
        <list-item style="width: 35%;">
            <!--loading Role name -->
            <!-- reminder: is role name is unique -->
            <role-item class="role-name-display">place-holder</role-item>
        </list-item>

        <!-- Right side -->
        <form style="width: 100%;">
            <!-- I hate naming the things -->
            <permission-container>
                <div class="permission-header">
                    <h5>Role Name *</h5>
                    <input type="text" id="role-name" value="New-role">
                </div>

                <div class="permission-details">
                    <h5>Permission</h5>

                    <div class="role-container">
                        <p>Update User</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="update-user"
                                    name="allowCloning"><span class="slider"></span></label></div>
                    </div>

                    <div class="role-container">
                        <p>Delete User</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="delete-user"
                                    name="allowCloning"><span class="slider"></span></label></div>
                    </div>

                    <div class="role-container">
                        <p>Force Delete Item</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="force-delete-item"
                                    name="allowCloning"><span class="slider"></span></label></div>
                    </div>

                    <div class="role-container">
                        <p>Force Update Item</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="force-update-item"
                                    name="allowCloning"><span class="slider"></span></label></div>
                    </div>

                    <div class="role-container">
                        <p>Force Create Item</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="force-create-item"
                                    name="allowCloning"><span class="slider"></span></label></div>
                    </div>

                    <div class="role-container">
                        <p>Moderation Explore</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox"
                                    id="moderation-explore" name="allowCloning"><span class="slider"></span></label>
                        </div>
                    </div>

                    <div class="role-container">
                        <p>View Audit Log</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="view-audit-log"
                                    name="allowCloning"><span class="slider"></span></label>
                        </div>
                    </div>

                    <div class="role-container">
                        <p>Change Username</p>
                        <div class="checkbox-custom"><label class="switch"><input type="checkbox" id="change-username"
                                    name="allowCloning"><span class="slider"></span></label>
                        </div>
                    </div>
                </div>

                <div class="permission-bottom" style="width: 100%; ">
                    <label for="role-description" style="margin-bottom: 10px;">Role Description</label>
                    <textarea type="text" name="role-desc" id="role-description" 
                    style="resize: none;"></textarea>
                </div>
            </permission-container>

            <role-confirm>
                <p style="margin: 0;">Don’t forget to save your change</p>
                <div class="role-button">
                    <input type="reset" id="reset-role">
                    <input type="submit" id="submit-role" value="Submit Changes">
                </div>
            </role-confirm>
        </form>

    </permission-setting>
</root-menu>