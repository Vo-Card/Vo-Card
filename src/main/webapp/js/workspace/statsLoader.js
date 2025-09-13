export async function statsLoader() {
    // mock up data change later
    const reviewData = {
        Easy: 20,
        Good: 35,
        Hard: 25,
        Again: 20
    };

    const voteColors = {
        Easy: '#6FCF97',
        Good: '#2D9CDB',
        Hard: '#F2994A',
        Again: '#EB5757'
    };

    const total = Object.values(reviewData).reduce((a, b) => a + b, 0);
    const barContainer = document.getElementById('monthly-stats-normal');

    for (const [vote, count] of Object.entries(reviewData)) {
        const percent = (count / total) * 100;

        const progress = document.createElement('div');
        progress.style.width = `${percent}%`;
        progress.style.height = '100%';
        progress.style.backgroundColor = voteColors[vote];
        progress.title = `${vote}: ${count} cards (${percent.toFixed(1)}%)`;

        barContainer.appendChild(progress);
    }

    var myCanvas = /** @type {HTMLCanvasElement} */ (
        document.getElementById('stats-bar')
    ).getContext('2d');

    let datetime = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    var chartConfig = {
        type: "bar",
        data: {
            labels: datetime,
            datasets: [{
                label: "Words per day",
                data: [50, 20, 31, 15, 15, 12, 39],
                backgroundColor: [
                    'rgba(108, 162, 51, 0.5)',
                ],
                borderColor: [
                    'rgba(108, 162, 51, 1)',

                ],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                x: {
                    gird: {
                        offset: true
                    },
                    ticks: {
                        color: 'white'
                    }
                },
                y: {
                    ticks: {
                        stepSize: 5,
                        color: 'white'
                    },
                    grid: {
                        color: ' rgba(231, 231, 231, 0.2)',
                        borderColor: 'red'
                    }
                },
                beginAtZero: true
            }
        }

    };
    // Creating chart function 
    // dont delete this and dont care the error (if you see)
    // @ts-ignore
    new Chart(myCanvas, chartConfig);
}