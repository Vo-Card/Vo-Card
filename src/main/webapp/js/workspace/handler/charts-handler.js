import { fetchWithAuth } from "/js/auth/auth.js";

export async function statsLoader() {
    // mock up data change later
    const reviewData = {
        Easy: 20,
        Good: 35,
        Hard: 25,
        Again: 20,
    };

    const voteColors = {
        Easy: "#6FCF97",
        Good: "#2D9CDB",
        Hard: "#F2994A",
        Again: "#EB5757",
    };

    const total = Object.values(reviewData).reduce((a, b) => a + b, 0);
    const barContainer = document.getElementById("monthly-stats-normal");

    for (const [vote, count] of Object.entries(reviewData)) {
        const percent = (count / total) * 100;

        const progress = document.createElement("div");
        progress.style.width = `${percent}%`;
        progress.style.height = "100%";
        progress.style.backgroundColor = voteColors[vote];
        progress.title = `${vote}: ${count} cards (${percent.toFixed(1)}%)`;

        barContainer.appendChild(progress);
    }
    const response = await fetchWithAuth("/api/review/thisWeekStats");
    const { stats: data } = await response.json();

    await loadWeeklyChart(data);
    loadWeekTotalStats(data);
}

function loadWeekTotalStats(data) {
    const totalReviews = data.reduce(
        (sum, entry) => sum + entry.total_reviews,
        0
    );
    const totalCorrect = data.reduce(
        (sum, entry) => sum + entry.total_correct,
        0
    );
    const totalFailed = data.reduce(
        (sum, entry) => sum + entry.total_failed,
        0
    );
    const totalOutOfTime = totalReviews - totalCorrect - totalFailed;

    const totalStatsBar = document.getElementById("total-stats");

    // Create custom bar chart
    const stats = [
        { label: "Correct", count: totalCorrect, color: "#2D9CDB" },
        { label: "Failed", count: totalFailed, color: "#F2994A" },
        { label: "Out of Time", count: totalOutOfTime, color: "#EB5757" },
    ];

    stats.forEach((stat) => {
        const percent = totalReviews ? (stat.count / totalReviews) * 100 : 0;
        const segment = document.createElement("div");
        segment.style.width = `${percent}%`;
        segment.style.height = "30px";
        segment.style.backgroundColor = stat.color;
        segment.title = `${stat.label}: ${stat.count} (${percent.toFixed(1)}%)`;
        totalStatsBar.appendChild(segment);
    });

    // Update text fields
    document.querySelector("total-cards").textContent = `${totalReviews} cards`;
    document.querySelector("total-failed").textContent = `${totalFailed} cards`;

    document.querySelector(
        "total-correct"
    ).textContent = `${totalCorrect} cards`;
    document.querySelector(
        "total-outoftime"
    ).textContent = `${totalOutOfTime} cards`;
    const averagePassing = totalReviews
        ? ((totalCorrect / totalReviews) * 100).toFixed(1)
        : "0.0";
    document.querySelector(
        "average-passing"
    ).textContent = `${averagePassing} %`;
}

export async function loadWeeklyChart(data) {
    if (!data || data.length === 0) {
        const response = await fetchWithAuth("/api/review/thisWeekStats");
        data = (await response.json()).stats;
    }
    const ctx = /** @type {HTMLCanvasElement} */ (
        document.getElementById("weekly-bar")
    ).getContext("2d");

    const sortedData = [...data].sort((a, b) => a.day_of_week - b.day_of_week);

    const weekdays = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

    function normalizeDayIndex(mysqlDay) {
        return (mysqlDay + 5) % 7;
    }

    const labels = [];
    const correctPerDay = Array(7).fill(0);
    const failedPerDay = Array(7).fill(0);
    const outOfTimePerDay = Array(7).fill(0);

    // (MM-DD + weekday)
    const today = new Date();
    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() - ((today.getDay() + 6) % 7));

    for (let i = 0; i < 7; i++) {
        const day = new Date(startOfWeek);
        day.setDate(startOfWeek.getDate() + i);
        const monthDay = day.toISOString().slice(5, 10);
        labels.push(`${monthDay} (${weekdays[i]})`);
    }

    for (const entry of sortedData) {
        const i = normalizeDayIndex(entry.day_of_week);
        correctPerDay[i] = entry.total_correct;
        failedPerDay[i] = entry.total_failed;
        outOfTimePerDay[i] =
            entry.total_reviews - entry.total_correct - entry.total_failed;
    }

    const chartConfig = {
        type: "bar",
        data: {
            labels,
            datasets: [
                {
                    label: "Out of time",
                    data: outOfTimePerDay,
                    backgroundColor: "rgba(255, 99, 132, 0.5)",
                    borderColor: "rgba(255, 99, 132, 1)",
                    borderWidth: 1,
                },
                {
                    label: "Correct",
                    data: correctPerDay,
                    backgroundColor: "rgba(51, 110, 162, 0.5)",
                    borderColor: "rgba(51, 112, 162, 1)",
                    borderWidth: 1,
                },
                {
                    label: "Failed",
                    data: failedPerDay,
                    backgroundColor: "rgba(255, 206, 86, 0.5)",
                    borderColor: "rgba(255, 206, 86, 1)",
                    borderWidth: 1,
                },
            ],
        },
        options: {
            scales: {
                x: {
                    grid: {
                        offset: true,
                    },
                    ticks: {
                        color: "white",
                    },
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 10,
                        color: "white",
                    },
                    grid: {
                        color: "rgba(231, 231, 231, 0.2)",
                        borderColor: "red",
                    },
                },
            },
        },
    };

    // @ts-ignore
    new Chart(ctx, chartConfig);
}
