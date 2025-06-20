document.addEventListener('DOMContentLoaded', () => {
    // ── hidden input から現在の年月日を取得 ──
    const yVal = document.getElementById('yearInput').value;
    const mVal = document.getElementById('monthInput').value;
    const dVal = document.getElementById('dayInput').value;
    const selectedDay = dVal ? parseInt(dVal) : null;

    // ── currentDate をパラメータ月にセット（なければ today） ──
    let currentDate;
    if (yVal && mVal) {
        currentDate = new Date(parseInt(yVal), parseInt(mVal) - 1, 1);
    } else {
        currentDate = new Date();
    }

    const monthYearEl = document.querySelector('.month-year');
    const prevBtn = document.querySelector('.month .prev');
    const nextBtn = document.querySelector('.month .next');
    const daysContainer = document.querySelector('.days');

    const monthNames = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ];

    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        monthYearEl.innerHTML = `
      ${monthNames[month]}<br>
      <span style="font-size:18px">${year}</span>
    `;

        daysContainer.innerHTML = '';
        const firstDay = new Date(year, month, 1).getDay();
        const blankDays = (firstDay === 0 ? 6 : firstDay - 1);

        // 空白
        for (let i = 0; i < blankDays; i++) {
            const li = document.createElement('li');
            li.classList.add('blank');
            daysContainer.appendChild(li);
        }

        // 日にち
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        for (let d = 1; d <= daysInMonth; d++) {
            const li = document.createElement('li');
            li.textContent = d;

            // 選択済み日をアクティブ化
            if (selectedDay === d &&
                year === parseInt(yVal) &&
                month + 1 === parseInt(mVal)) {
                li.classList.add('active');
            }

            // クリックで再送＆選択反映
            li.addEventListener('click', () => {
                // カレンダー上のハイライト切り替え
                daysContainer.querySelectorAll('li')
                    .forEach(x => x.classList.remove('active'));
                li.classList.add('active');

                // hidden input にセットして submit
                document.getElementById('yearInput').value = year;
                document.getElementById('monthInput').value = month + 1;
                document.getElementById('dayInput').value = d;
                document.getElementById('dateForm').submit();
            });

            daysContainer.appendChild(li);
        }
    }

    prevBtn.addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });
    nextBtn.addEventListener('click', () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

    // 初回描画
    renderCalendar();
});


document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.querySelector('.btn.add');
    const editBtn = document.querySelector('.btn.edit');
    const submitBtn = document.querySelector('.btn.submit');
    const tbody = document.querySelector('.product-table tbody');
    const tpl = document.getElementById('new-row-template');

    // 「編集モード ON」にする関数
    function enableEditMode() {
        editBtn.classList.add('active');
        submitBtn.disabled = false;
        tbody.querySelectorAll('input[name="quantity"]').forEach(input => {
            input.disabled = false;
        });
    }

    // Add ボタン押した時 ---
    addBtn.addEventListener('click', e => {
        e.preventDefault();
        const clone = tpl.content.cloneNode(true);
        tbody.appendChild(clone);
        enableEditMode();
    });

    // Edit ボタン押した時 ---
    editBtn.addEventListener('click', e => {
        e.preventDefault();
        enableEditMode();
    });

    // プルダウン変更時に価格/JANを流し込む
    tbody.addEventListener('change', e => {
        if (!e.target.matches('select[name="productId"]')) return;

        const opt = e.target.selectedOptions[0];
        const row = e.target.closest('tr');

        // dataset から読み出して input にセット
        row.querySelector('input[name="price"]').value = opt.dataset.price || '';
        row.querySelector('input[name="janCode"]').value = opt.dataset.janCode || '';
    });
});
