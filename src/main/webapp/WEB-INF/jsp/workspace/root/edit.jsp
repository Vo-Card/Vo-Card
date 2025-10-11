<script src="/js/workspace/page-fixer.js"></script>

<root-menu>
    <h2>Root Create / Edit</h2>
    <root-div>
        <h4>Role -</h4>
        <h4>Name</h4>
    </root-div>

    <permission-setting>
        <!-- Right side -->
        <form autocomplete="off" style="width: 100%;">
            <!-- I hate naming the things -->
            <permission-container>
                <div class="permission-header">
                    <h5>Role Name *</h5>
                    <input type="text" id="role-name" placeholder="New-role">
                </div>

                <div class="permission-details">
                    <h5>Permission</h5>

                    <div class="role-container">
                        <p>Delete User</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="delete-user" data-bit="0"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>Update User</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="update-user" data-bit="1"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>Force Delete Item</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="force-delete-item" data-bit="2"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>Force Update Item</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="force-update-item" data-bit="3"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>Force Create Item</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="force-create-item" data-bit="4"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>Moderation Explore</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="moderation-explore" data-bit="5"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>View Audit Log</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="view-audit-log" data-bit="6"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                  
                      <div class="role-container">
                        <p>Change Username</p>
                        <div class="checkbox-custom">
                          <label class="switch">
                            <input type="checkbox" id="change-username" data-bit="7"><span class="slider"></span>
                          </label>
                        </div>
                      </div>
                    </div>

                    <div class="permission-bottom" style="width: 100%;">
                      <label for="role-description" style="margin-bottom: 10px;">Role Description</label>
                      <textarea id="role-description" placeholder="Role Description" style="resize: none;"></textarea>
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