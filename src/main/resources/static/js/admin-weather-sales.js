// Chart.jsのグラフ生成関数
let chart;
function updateChart(labels, salesData, tempData, rainData) {
    const ctx = document.getElementById('salesChart').getContext('2d');
    if (chart) chart.destroy();
    chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '売上（円）',
                    data: salesData,
                    borderColor: 'blue',
                    fill: false
                },
                {
                    label: '平均気温（℃）',
                    data: tempData,
                    borderColor: 'orange',
                    fill: false
                },
                {
                    label: '降水量（mm）',
                    data: rainData,
                    borderColor: 'green',
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}
