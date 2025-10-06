import { fetchWithAuth } from "/js/auth/auth.js";
import { loadPage } from "./module/page-manager.js";

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

export async function roleSideBar() {
    // working after click edit role
    const res = await fetchWithAuth("/api/user/viewRoles?page=" + pageCache);
    if (res.ok) {
        const data = await res.json();
        const container = document.querySelector("list-item");
        const roleItem = document.querySelector("role-item");
        const roleItemCopy = roleItem.cloneNode(true);
        roleItem.remove();
        console.log(data);

        data.Roles.forEach((role) => {
            const roleItemAppend = roleItemCopy.cloneNode(true);
            const roleInfo = roleItemAppend.querySelector(".role-display");

            roleInfo.innerHTML = role.permission_name;
            container.append(roleItemAppend);
            console.log(container);
        });
    } else {
        console.log("Access Denied");
        loadPage("home");
    }
}

let pageCache = 1;
export async function showAllRoles() {
    let allRoleCounter = 0;
    console.log(`Current paht : ${window.location.pathname}`);

    const res = await fetchWithAuth("/api/user/viewRoles?page=" + pageCache);

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

            editRoleButton.addEventListener("click", (e) => {
                e.preventDefault();
                loadPage("/workspace/root/" + role.permission_id_PK);
            });
            // @ts-ignore
            const deleteRoleButton =
                roleItemAppend.querySelector("#role-delete");

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

function isRootDisplayPage(item) {
    const currentPath = window.location.pathname;
    return currentPath === "/workspace/root";
}

async function deleteRole(targetId) {}
