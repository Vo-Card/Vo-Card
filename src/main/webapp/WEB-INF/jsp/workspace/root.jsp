<script src="/js/workspace/page-fixer.js"></script>

<root-menu>
    <h2 style="margin-bottom: 10px;">ROOT Menu</h2>

    <root-div style="border-bottom: 2px solid white;">
        <role-button>Role</role-button>
        <assign-role-button>Assign Role</assign-role-button>
    </root-div>

    <form id="createNewRole" style=" padding: 15px 10px 15px 0;">
        <input type="submit" style="width: fit-content;" value="Create">
        <input style="width: 50%;" type="text" name="roleName" id="search-roles" placeholder="Search or Create roles..">
    </form>

    <root-div style="border-bottom: 2px solid white;">
        <p>Role :</p>
        <p all-roles>1</p>
    </root-div>

    <list-item>
        <!-- loading compo jssssss -->
        <role-item>
            <role-front>Role-name</role-front>
            <!-- add attribute on p to show count user on that role  -->
            <role-mid>
                <i class="nf nf-fa-user"></i>
                <p> 2 </p>
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