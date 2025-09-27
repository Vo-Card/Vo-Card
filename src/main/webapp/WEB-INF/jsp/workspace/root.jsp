<script src="/js/workspace/page-fixer.js"></script>

<root-menu>
    <h2 style="margin-bottom: 10px;">ROOT Menu</h2>

    <root-div style="border-bottom: 2px solid white;">
        <role-button>Role</role-button>
        <assign-role-button>Assign Role</assign-role-button>
    </root-div>

    <root-div style=" padding: 15px 10px 15px 0;">
        <role-create>Create</role-create>
        <input style="width: 50%;" type="text" id="search-roles" onkeyup="" placeholder="Search for roles..">
    </root-div>

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
            <role-behind>Edit</role-behind>
        </role-item>
        <!------------------->
    </list-item>
</root-menu>