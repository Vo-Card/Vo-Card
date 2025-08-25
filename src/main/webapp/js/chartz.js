// TODO: <creating recently 7 days stat>
// This files currently use in welcome.jsp
// getting data by java
// temporary mock up data for now

// get id
var myCanvas = document.getElementById('chartz').getContext('2d');
// get month/day
let datetime = [];

for (let i = 0; i < 7; i++) {
    let today = new Date();
    today.setDate(today.getDate() - i);
    datetime.push(today.toISOString().split("T")[0].slice(5));
}

// chart 
var chart = {
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
                    stepSize: 10,
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
var statChart = new Chart(myCanvas, chart);
