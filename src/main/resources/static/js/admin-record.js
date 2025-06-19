document.addEventListener('DOMContentLoaded', () => {
    const monthYearEl = document.querySelector('.month-year');
    const prevBtn = document.querySelector('.month .prev');
    const nextBtn = document.querySelector('.month .next');
    const daysContainer = document.querySelector('.days');

    // 月名配列
    const monthNames = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];

    // 現在表示中の年月
    let currentDate = new Date();

    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth(); // 0〜11

        // ヘッダー更新
        monthYearEl.innerHTML = `
      ${monthNames[month]}<br>
      <span style="font-size:18px">${year}</span>
    `;

        // 日付リストをクリア
        daysContainer.innerHTML = '';

        // 1日の曜日 (0=日,1=月…6=土) → 月曜日始まりに変換
        const firstDay = new Date(year, month, 1).getDay();
        const blankDays = (firstDay === 0 ? 6 : firstDay - 1);

        // 前月からの空白を生成
        for (let i = 0; i < blankDays; i++) {
            const li = document.createElement('li');
            li.classList.add('blank');
            daysContainer.appendChild(li);
        }

        // 当月の日数
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        for (let d = 1; d <= daysInMonth; d++) {
            const li = document.createElement('li');
            li.textContent = d;
            li.addEventListener('click', () => {
                // 選択状態の切り替え
                daysContainer.querySelectorAll('li').forEach(x => x.classList.remove('active'));
                li.classList.add('active');
            });
            daysContainer.appendChild(li);
        }
    }

    // ◀️▶️ ボタンのイベント
    prevBtn.addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });
    nextBtn.addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

    // 初期表示
    renderCalendar();
});
