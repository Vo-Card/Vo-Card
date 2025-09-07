export async function snowfallEffect() {
    const canvas = document.getElementById('background-play');
    const ctx = canvas.getContext('2d');

    function resizeCanvas() {
        const dpr = window.devicePixelRatio || 1;
        canvas.width = window.innerWidth * dpr;
        canvas.height = window.innerHeight * dpr;
        ctx.setTransform(1, 0, 0, 1, 0, 0);
        ctx.scale(dpr, dpr);
    }
    resizeCanvas();
    window.addEventListener("resize", resizeCanvas);

    let numOfFlakes = 100;
    const snowfallEffect = [];

    function createSnowFlake() {
        const snowflake = {
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            size: Math.random() * 3 + 1,
            speedX: (Math.random() - 0.5) * 0.5,
            speedY: Math.random() * 1 + 0.5,
            opacity: 0.3 + Math.random() * 0.7 // assign once
        };
        snowfallEffect.push(snowflake);
    }

    function drawSnowFlake(snowflake) {
        ctx.beginPath();
        ctx.arc(snowflake.x, snowflake.y, snowflake.size, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(255,255,255,${snowflake.opacity})`;
        ctx.fill();
    }


    for (let i = 0; i < numOfFlakes; i++) {
        createSnowFlake();
    }

    function updateSnowFlake(snowflake) {
        snowflake.x += snowflake.speedX;
        snowflake.y += snowflake.speedY;

        if (snowflake.y > canvas.height) {
            snowflake.x = Math.random() * canvas.width;
            snowflake.y = -10;
        }
    }

    function snowfall() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        for (let i = 0; i < snowfallEffect.length; i++) {
            drawSnowFlake(snowfallEffect[i]);
            updateSnowFlake(snowfallEffect[i]);
        }

        requestAnimationFrame(snowfall);
    }
    snowfall();
}
