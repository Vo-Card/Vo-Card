async function loadPage(path) {
    try {
        const res = await fetch(path, { headers: { "X-Requested-With": "XMLHttpRequest" } });
        if (res.ok) {
            const html = await res.text();
            document.getElementById("content").innerHTML = html;

            if (path === "/workflow/home") {
                initChartz();
            }
        }
        
        window.history.pushState({}, "", path);

    } catch(err) {
        console.error(err);
    }
}

function runPageInit() {
    const page = document.location.pathname.split("/").pop();
    if (page === "home") initChartz();
}

document.addEventListener("click", e => {
    const link = e.target.closest("a[data-workflow]");
    if (link) {
        e.preventDefault();
        loadPage(link.getAttribute("href"));
    }
});

window.addEventListener("popstate", () => {
    loadPage(window.location.pathname);
});

document.addEventListener("DOMContentLoaded", runPageInit);
