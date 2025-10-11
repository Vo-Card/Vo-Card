<script src="/js/workspace/page-fixer.js"></script>

<root-menu>
    <h2 style="margin-bottom: 10px;">ROOT Menu</h2>

    <root-div style="border-bottom: 2px solid white;">
        <role-button>Role</role-button>
        <assign-role-button>Assign Role</assign-role-button>
    </root-div>

    <root-div style="padding: 15px 10px 15px 0; justify-content: space-between;">
        <div>
            <p>User :</p>
            <p class="all-user">1</p>
        </div>
        
        <div>
            <button class="previous"> < </button>
            <button class="next"> > </button>
        </div>
    </root-div>

    <list-item>
        <!-- js -->
        <role-item>
            <!-- loading username -->
            <role-front>
                <i class="nf nf-fa-user"></i>
                <p class="username-display">Username</p>
            </role-front>
                    
            <role-dropdown>
                <!-- selected id -->
                <selected-role class="">
                    <span class="selected">None</span>
                    <i class="nf nf-md-chevron_down_circle"></i>
                </selected-role>
                <!-- Loadding role on this attribute as li -->
                <ul class="role-selector">
                    <li permissionId="null" permissionName = "None" class="active">None</li>
                </ul>
            </role-dropdown>
        </role-item>
        <!-- end -->
    </list-item>
</root-menu>