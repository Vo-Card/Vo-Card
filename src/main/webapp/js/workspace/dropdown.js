import { fetchWithAuth } from "/js/auth/auth.js";
import { loadPage } from "./module/page-manager.js";

export async function rtDropdowns() {
    const dropdowns = document.querySelectorAll("role-dropdown");
    dropdowns.forEach((dropdowns) => {
        const select = dropdowns.querySelector("selected-role");
        const menu = dropdowns.querySelector("role-dropdown ul");
        const options = dropdowns.querySelectorAll("role-dropdown ul li");
        const selected = dropdowns.querySelector(".selected");

        select.addEventListener("click", () => {
            select.classList.toggle("select-clicked");
            menu.classList.toggle("menu-open");
        });

        options.forEach((option) => {
            option.addEventListener("click", () => {
                // @ts-ignore ignore error on innerText
                selected.innerText = option.innerText;
                select.classList.remove("select-clicked");
                menu.classList.remove("menu-open");

                options.forEach((option) => {
                    option.classList.remove("active");
                });
                option.classList.add("active");
            });
        });
    });
}

export async function assignRole() {
    let pageCache = 1;
    let person = 0;

    const nextButton = document.querySelector('.next');
    const previousButton = document.querySelector('.previous');

    nextButton.addEventListener('click', () => {
        pageCache++;
        
    });
    
    previousButton.addEventListener('click', () => {
        pageCache--;
        if (pageCache <= 0){
            pageCache = 1;
        }
    });

    const res = await fetchWithAuth("/api/user/getUser?page=" + pageCache);
    const resRole = await fetchWithAuth("/api/user/viewRoles")

    if (res.ok && resRole.ok) {
        const data = await res.json();
        const dataRole = await resRole.json();
        const container = document.querySelector("list-item");
        const roleItem = document.querySelector("role-item");
        
        const roleItemCopy = roleItem.cloneNode(true);
        const counter = document.querySelector('.all-user');
        roleItem.remove();
        

        data.User.forEach((user) => {
            const roleItemAppend = roleItemCopy.cloneNode(true);
            // @ts-ignore
            const username = roleItemAppend.querySelector(".username-display");
            //@ts-ignore
            const select = roleItemAppend.querySelector("selected-role");
            //@ts-ignore
            const selected = roleItemAppend.querySelector(".selected");
            selected.textContent = user.permission_name;
            //@ts-ignore
            const roleContainer = roleItemAppend.querySelector(".role-selector");
            dataRole.Roles.forEach(role => {
                const li = document.createElement('li');
                li.textContent = role.permission_name;
                li.setAttribute("permissionName", role.permission_name);
                li.setAttribute("permissionId", role.permission_id_PK);
                roleContainer.append(li);
            });

            //@ts-ignore
            const roleItem = roleItemAppend.querySelectorAll('.role-selector li');
            select.addEventListener('click', () => {
                select.classList.toggle("select-clicked");
                roleContainer.classList.toggle("menu-open");
            });

            roleItem.forEach((role) => {
                role.addEventListener('click', async () => {
                    const uid = user.user_id_PK;
                    const rid = role.getAttribute("permissionId");
                    const preparedData = {
                        targetId : uid,
                        roleId : rid
                    };
                    console.log(preparedData);
                    
                    const response = await fetchWithAuth("/api/user/assignRole", {
                        method: "PUT",
                        headers: { "Content-Type": "application/json"},
                        body: JSON.stringify(preparedData),
                    });

                    if (response.ok){
                        selected.textContent = role.getAttribute("permissionName");
                        const chk = await response.json();
                        console.log(chk);
                    } else {
                    }

                })  
            });
            username.textContent = user.username;
            container.append(roleItemAppend);
            person++;
        });
            //@ts-ignore
        counter.textContent = person;
    } else {
        console.log("ello");
    }
}
