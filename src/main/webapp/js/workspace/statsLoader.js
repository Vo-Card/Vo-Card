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
}