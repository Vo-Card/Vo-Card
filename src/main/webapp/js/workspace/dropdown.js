import { fetchWithAuth } from "/js/auth/auth.js";
import { loadPage } from "./module/page-manager.js";

export async function rtDropdowns() {
    const dropdowns = document.querySelectorAll('role-dropdown')
    dropdowns.forEach(dropdowns => {
        const select = dropdowns.querySelector('selected-role')
        const menu = dropdowns.querySelector('role-dropdown ul')
        const options = dropdowns.querySelectorAll('role-dropdown ul li')
        const selected = dropdowns.querySelector('.selected')

        select.addEventListener('click', () => {
            select.classList.toggle('select-clicked')
            menu.classList.toggle('menu-open')
        });

        options.forEach(option => {
            option.addEventListener('click', () => {
                // @ts-ignore ignore error on innerText
                selected.innerText = option.innerText;
                select.classList.remove('select-clicked');
                menu.classList.remove('menu-open')

                options.forEach(option => {
                    option.classList.remove('active');
                });
                option.classList.add('active');
            });
        });
    });
}

let pageCache = 1;
export async function assignRole() {
    console.log("Ello");
    const res = await fetchWithAuth("/api/user/getUser?page=" + pageCache);

    if (res.ok) {
        const data = await res.json();
        const container = document.querySelector('list-item');
        const roleItem = document.querySelector('role-item');
        const roleItemCopy = roleItem.cloneNode(true);
        roleItem.remove();
        console.log(roleItemCopy);
        console.log(data.User);
        data.User.forEach(user => {
            const roleItemAppend = roleItemCopy.cloneNode(true);
            // @ts-ignore
            const username = roleItemAppend.querySelector('.username-display');

            username.textContent = user.username;
            container.append(roleItemAppend);
        });
    } else {
        console.log("ello");
    }
}
