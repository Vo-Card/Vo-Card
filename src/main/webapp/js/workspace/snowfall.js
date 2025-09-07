export async function snowfallEffect() {
    const canvas = document.getElementById('background-play');
    const ctx = canvas.getContext('2d');

    canvas.width = window.innerWidth
    canvas.height = window.innerHeight

    let numOfFlakes = 100;
    const snowfallEffect = []

    function createSnowFlake() {
        const snowflake = {
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            size: Math.random() * 3,
            speedX: Math.random() * 0,
            speedY: Math.random() * 1
        }
        snowfallEffect.push(snowflake)
    }

    for (let i = 0; i < numOfFlakes; i++) {
        createSnowFlake()
    }

    function drawSnowFlake(snowflake) {
        ctx.beginPath()
        ctx.arc(snowflake.x, snowflake.y, snowflake.size, 0, Math.PI * 2)
        ctx.fillStyle = 'white'
        ctx.fill()
    }

    function updateSnowFlake(snowflake) {
        snowflake.x += snowflake.speedX
        snowflake.y += snowflake.speedY

        if (snowflake.y > canvas.height) {
            snowflake.x = Math.random() * canvas.width
            snowflake.y = -50
        }
    }

    function snowfall() {
        ctx.clearRect(0, 0, canvas.width, canvas.height)

        for (let i = 0; i < snowfallEffect.length; i++) {
            drawSnowFlake(snowfallEffect[i])
            updateSnowFlake(snowfallEffect[i])
        }

        requestAnimationFrame(snowfall)
    }
    snowfall()
}