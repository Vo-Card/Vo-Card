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
            <p all-user>1</p>
        </div>
        <input style="width: 50%;" type="text" id="search-roles" onkeyup="" placeholder="Search for roles..">
    </root-div>

    <list-item>
        <!-- js -->
        <role-item>
            <!-- loading username -->
            <role-front>
                <i class="nf nf-fa-user"></i>
                <p>Username</p>
            </role-front>

            <role-dropdown>
                <!-- selected id -->
                <selected-role class="">
                    <span class="selected">None</span>
                    <i class="nf nf-md-chevron_down_circle"></i>
                </selected-role>
                <!-- Loadding role on this attribute as li -->
                <ul class="">
                    <li class="active">None</li>
                    <li>Place-holder</li>
                </ul>
            </role-dropdown>
        </role-item>
        <!-- end -->
    </list-item>
</root-menu>