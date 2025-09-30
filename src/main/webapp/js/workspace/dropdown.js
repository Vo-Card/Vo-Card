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

