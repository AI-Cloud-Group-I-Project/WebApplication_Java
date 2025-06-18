

// 年と月の初期化
const yearSelect = document.getElementById('year-select');
const monthSelect = document.getElementById('month-select');
const currentYear = new Date().getFullYear();
for (let y = currentYear - 2; y <= currentYear + 3; y++) {
    yearSelect.innerHTML += `<option value="${y}">${y}年</option>`;
}
for (let m = 1; m <= 12; m++) {
    monthSelect.innerHTML += `<option value="${m}">${m}月</option>`;
}

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

// データ取得（API連携を想定）
function fetchFilteredData() {
    fetch('/api/sales')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('#sales-table tbody');
            tableBody.innerHTML = '';
            const labels = [], sales = [], temp = [], rain = [];

            data.forEach(row => {
                labels.push(row.date);
                sales.push(row.sales);
                temp.push(row.temperature);
                rain.push(row.rainfall);

                tableBody.innerHTML += `
              <tr>
                <td>${row.date}</td>
                <td>${row.weather}</td>
                <td>${row.brand}</td>
                <td>${row.volume}</td>
                <td>${row.sales}</td>
              </tr>`;
            });

            updateChart(labels, sales, temp, rain);
        });
}