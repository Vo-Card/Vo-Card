// This file is to prevent javascript or server didn't redirect the user to the workspace before actually load the content

if (document.getElementById("content") === null) {
    window.location.reload();
}
