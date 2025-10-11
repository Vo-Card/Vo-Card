import { fetchWithAuth } from "/js/auth/auth.js";
import { loadPage } from "./module/page-manager.js";

const DELETE_USER = 1n << 0n;
const UPDATE_USER = 1n << 1n;
const FORCE_DELETE_ITEM = 1n << 2n;
const FORCE_UPDATE_ITEM = 1n << 3n;
const FORCE_CREATE_ITEM = 1n << 4n;
const MODERATE_EXPLORER = 1n << 5n;
const VIEW_AUDIT_LOG = 1n << 6n;
const CHANGE_USERNAME = 1n << 7n;

export async function createEmptyRole() {
    const createRoleButton = document.querySelector("#createNewRole");

    createRoleButton?.addEventListener("click", async () => {
        const newRole = {
            name: "New Role",
            permissions: 0n,
        };

        console.log("Creating new role:", newRole);

        const response = await fetchWithAuth(
            `/api/user/createNewRole?roleName=New%20Role&bitmask=0`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    roleName: newRole.name,
                    bitmask: newRole.permissions.toString(),
                }),
            }
        );

        const result = await response.json();
        console.log("Server response:", result);
    });
}

export async function showAllRoles() {
    let allRoleCounter = 0;
    console.log(`Current paht : ${window.location.pathname}`);

    const res = await fetchWithAuth("/api/user/viewRoles");

    if (res.ok) {
        const data = await res.json();
        const container = document.querySelector("list-item");
        const roleItem = document.querySelector("role-item");
        const roleItemCopy = roleItem.cloneNode(true);
        const allRole = document.querySelector(".all-roles");
        roleItem.remove();
        console.log(data);

        data.Roles.forEach((role) => {
            const roleItemAppend = roleItemCopy.cloneNode(true);
            // @ts-ignore
            const roleName = roleItemAppend.querySelector("#role-name");
            // @ts-ignore
            const userInRole = roleItemAppend.querySelector(".user-with-role");
            // @ts-ignore
            const editRoleButton = roleItemAppend.querySelector("#role-edit");

            editRoleButton.addEventListener("click", async (e) => {
                e.preventDefault();
                loadPage("/workspace/root/" + role.permission_id_PK);
            });
            // @ts-ignore
            const deleteRoleButton = roleItemAppend.querySelector("#role-delete");
            deleteRoleButton.addEventListener(
                "click",
                async () => await deleteRole(role.permission_id_PK)
            );

            roleName.textContent = role.permission_name;
            userInRole.textContent = role.user_count;
            container.append(roleItemAppend);
            allRoleCounter++;
        });
        // @ts-ignore
        allRole.textContent = allRoleCounter;
    } else {
        console.log("Access Denied");
        loadPage("home");
    }
}

async function deleteRole(targetId) {
    const response = await fetchWithAuth(
        "/api/user/deleteRole?target=" + targetId,
        {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        }
    );
    const data = await response.json();
    console.log(data);
}

async function getCurrentRoleInfo(path) {
    const match = path.match(/^\/workspace\/root\/([^/]+)/);

    const roleId = match ? match[1] : null;
    console.log(roleId);

    const res = await fetchWithAuth("/api/user/currentRole?target=" + roleId);

    if (res.ok) {
        const data = await res.json();
        console.log(data);
        return data.role;
    } else {
        console.log("Not found role ID");
    }
}

// TODO: updaterole....
export async function updateRole(path) {
    const data = await getCurrentRoleInfo(path);
    whichBitmaskOn(data.permission_level)
    console.log(data);

    const createRoleButton = document.querySelector("#submit-role");

    const roleName = document.getElementById("role-name");
    //@ts-ignore
    roleName.value = data.permission_name;
    const roleDescription = document.getElementById("role-description");
    //@ts-ignore
    roleDescription.value = data.permission_description;

    createRoleButton?.addEventListener("click", async (e) => {
        const roleNameInput = document.querySelector("#role-name");
        //@ts-ignore
        const newRoleName = roleNameInput?.value?.trim() || null;

        const roleDescInput = document.querySelector("#role-description");
        //@ts-ignore
        const newRoleDesc = roleDescInput?.value?.trim() || null;
        e.preventDefault();

        let permissions = 0n;

        // collect all checkbox states and set bits accordingly
        //@ts-ignore
        if (document.querySelector("#delete-user")?.checked)
            permissions |= DELETE_USER;

        //@ts-ignore
        if (document.querySelector("#update-user")?.checked)
            permissions |= UPDATE_USER;

        //@ts-ignore
        if (document.querySelector("#force-delete-item")?.checked)
            permissions |= FORCE_DELETE_ITEM;

        //@ts-ignore
        if (document.querySelector("#force-update-item")?.checked)
            permissions |= FORCE_UPDATE_ITEM;

        //@ts-ignore
        if (document.querySelector("#force-create-item")?.checked)
            permissions |= FORCE_CREATE_ITEM;

        //@ts-ignore
        if (document.querySelector("#moderation-explore")?.checked)
            permissions |= MODERATE_EXPLORER;

        //@ts-ignore
        if (document.querySelector("#view-audit-log")?.checked)
            permissions |= VIEW_AUDIT_LOG;

        //@ts-ignore
        if (document.querySelector("#change-username")?.checked)
            permissions |= CHANGE_USERNAME;

        console.log("Final permission bitmask:", permissions.toString());
        console.log(newRoleName);
        console.log(newRoleDesc);

        const preparedData = {
            roleId: data.permission_id_PK,
            bitmask: permissions.toString(),
            roleName: newRoleName,
            roleDesc: newRoleDesc,
        };

        const response = await fetchWithAuth("/api/user/updateRole", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(preparedData),
        });

        const result = await response.json();
        console.log("Server response:", result);
    });
}

async function whichBitmaskOn(bitmask) {
    console.log("Get bit :" +bitmask);
    
    const checkboxes = document.querySelectorAll('input[type="checkbox"][data-bit]');
    console.log(checkboxes);
    
  
  checkboxes.forEach(checkbox => {
            //@ts-ignore
    const bit = parseInt(checkbox.dataset.bit, 10);
    const isOn = (bitmask & (1 << bit)) !== 0; 
            //@ts-ignore
    checkbox.checked = isOn;
  });
}