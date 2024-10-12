// https://github.com/bigskysoftware/htmx/issues/2856

document.addEventListener("htmx:beforeHistorySave", () => {
    for (const el of document.body.querySelectorAll("video[src]")) {
        el.setAttribute("data-saved-src", el.getAttribute("src"));
        el.removeAttribute("src");
    }
});

document.addEventListener("htmx:historyRestore", () => {
    for (const el of document.body.querySelectorAll("video[data-saved-src]")) {
        el.setAttribute("src", el.getAttribute("data-saved-src"));
        el.removeAttribute("data-saved-src");
    }
});
