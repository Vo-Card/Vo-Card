<script src="/js/workspace/page-fixer.js"></script>

<root-menu>
    <h2 style="margin-bottom: 10px;">ROOT Menu</h2>

    <root-div style="border-bottom: 2px solid white;">
        <a href="/workspace/root">
            <role-button>Role</role-button>
        </a>
        <a href="/workspace/root/assign">
            <assign-role-button>Assign Role</assign-role-button>
        </a>
    </root-div>
    <root-div>
        <form id="createNewRole">
            <input type="submit" style="width: fit-content ; padding: 5px;" value="Create">
        </form>
    </root-div>
    <input style="width: 50%;" type="text" name="roleName" id="search-roles" placeholder="Search or Create roles..">

    <root-div style="border-bottom: 2px solid white;">
        <p>Role :</p>
        <p class="all-roles">1</p>
    </root-div>

    <list-item>
        <!-- loading compo jssssss -->
        <role-item>
            <role-front>
                <p id="role-name">Role-name</p>
            </role-front>
            <!-- add attribute on p to show count user on that role  -->
            <role-mid>
                <i class="nf nf-fa-user"></i>
                <p class="user-with-role"> 2 </p>
                <p>User</p>
            </role-mid>
            <!-- Enter to role id -->
            <role-behind>
                <button id="role-edit"></button>
                <button id="role-delete"></button>
            </role-behind>
        </role-item>
    </list-item>
</root-menu>