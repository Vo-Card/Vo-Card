const bgBox = document.body;



const bgLayers = {
    dots: {
        value: 'radial-gradient(rgb(255,255,255) 1px, transparent 0)',
        position: '-25px -25px',
        size: '50px 50px',
        repeat: 'repeat',
    },
    glow: {
        value: 'radial-gradient(circle, rgba(85, 104, 119, 0.2) 0%, rgba(33, 34, 46, 0) 70%)',
        position: '-750px -750px',
        size: '1500px 1500px',
        repeat: 'no-repeat',
    },
    glow2: {
        value: 'radial-gradient(ellipse 150% 50%, rgba(27, 29, 36, 1) 65%, #d38c2aff 85%)',
        position: 'center -50vh',
        size: '100vw 500vh',
        repeat: 'no-repeat',
    },
    glow2_mask: {
        value: 'radial-gradient(ellipse 150% 50%, rgba(27, 29, 36, 1) 60%, rgba(255, 149, 10, 0) 75%)',
        repeat: 'no-repeat',
    },
    gradient: 'linear-gradient(transparent 750px, rgb(27, 29, 36) 1000px)',
    color: 'rgb(27, 29, 36)'
};

function updateBackground() {
    const dot = `${bgLayers.dots.value} ${bgLayers.dots.position} / ${bgLayers.dots.size} ${bgLayers.dots.repeat}`;
    const glow = `${bgLayers.glow.value} ${bgLayers.glow.position} / ${bgLayers.glow.size} ${bgLayers.glow.repeat}`;
    const glow2 = `${bgLayers.glow2.value} ${bgLayers.glow2.position} / ${bgLayers.glow2.size} ${bgLayers.glow2.repeat}`;
    const glow2_mask = `${bgLayers.glow2_mask.value} ${bgLayers.glow2.position} / ${bgLayers.glow2.size} ${bgLayers.glow2.repeat}`;
    document.body.style.background = `${glow}, ${bgLayers.gradient}, ${glow2_mask}, ${dot}, ${glow2}, ${bgLayers.color}`;
}

// Initial background setup
updateBackground();

//get position based on window size and scroll
function getCursorPosition(event) {
    var x = 0, y = 0;
    if (event.pageX || event.pageY) {
        x = event.pageX;
        y = event.pageY;
    } else if (event.clientX || event.clientY) {
        x = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
        y = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
    }
    return { x: x, y: y };
}

//Check cfor cursor
document.addEventListener('mousemove', function(event) {
    var cursorPosition = getCursorPosition(event);
    var windowWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
    var windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
    bgLayers.dots.position = (cursorPosition.x / windowWidth * 50 - 25) + 'px ' + (cursorPosition.y / windowHeight * 50 - 25) + 'px';
    bgLayers.glow.position = (cursorPosition.x - 750) + 'px ' + (cursorPosition.y - 750) + 'px';
    updateBackground();
});

