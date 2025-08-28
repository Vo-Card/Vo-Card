// grab elements
const splitter = document.getElementById('splitter');
const sidebar = document.getElementById('workflow-sidebar');

let isDragging = false;

// start dragging
splitter.addEventListener('mousedown', () => {
    isDragging = true;
    document.body.style.cursor = 'col-resize';
    splitter.classList.add('dragging');
});

// stop dragging
document.addEventListener('mouseup', () => {
    isDragging = false;
    document.body.style.cursor = 'default';
    splitter.classList.remove('dragging');
});

// handle dragging
document.addEventListener('mousemove', (e) => {
    if (!isDragging) return;

    // calculate new width
    const workspaceLeft = sidebar.parentElement.getBoundingClientRect().left;
    let newWidth = e.clientX - workspaceLeft;

    // optional min/max
    const minWidth = 150;
    const maxWidth = 450;
    if (newWidth < minWidth) newWidth = minWidth;
    if (newWidth > maxWidth) newWidth = maxWidth;

    // apply new width
    sidebar.style.width = `${newWidth}px`;
});
