import { fetchWithAuth } from "/js/auth/auth.js";
import { loadPage } from "./module/page-manager.js";


let pageCache = 1;
export async function lookOverview() {
    const res = await fetchWithAuth("/api/user/getUser?page=" + pageCache);
    if (res.ok) {
        const data = await res.json();
        const container = document.querySelector('list-item');
        const roleItem = document.querySelector('role-item');
        const roleItemCopy = roleItem.cloneNode(true);
        roleItem.remove();
        data.User.forEach(user => {
            const roleItemAppend = roleItemCopy.cloneNode(true);
            // @ts-ignore
            const username = roleItemAppend.querySelector('.username-display');
            // @ts-ignore
            const deleteButton = roleItemAppend.querySelector('.choosen-one');
            deleteButton.addEventListener('click', async () => await deleteUser(user.user_id_PK));

            username.textContent = user.username;
            container.append(roleItemAppend);
        });
    } else {
        console.log("User doesn't has Permission");
        loadPage("/workspace/home");
    }
}
async function deleteUser(targetId) {
    const response = await fetchWithAuth("/api/user/forceDeleteUser?target=" + targetId, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        }
    });
    const data = await response.json();
    console.log(data);
}