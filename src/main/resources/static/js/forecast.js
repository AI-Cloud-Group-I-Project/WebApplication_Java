document.addEventListener("DOMContentLoaded", () => {
    const select = document.getElementById("order-date");
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const current = select.dataset.current;

    // 今週の月曜・木曜と来週の同曜日を返す
    function getTargetDates() {
        const dow = today.getDay();
        const thisMon = new Date(today);
        thisMon.setDate(today.getDate() - ((dow + 6) % 7));
        const thisThu = new Date(thisMon);
        thisThursday = thisThu; // for clarity
        thisThu.setDate(thisMon.getDate() + 3);
        const nextMon = new Date(thisMon);
        nextMon.setDate(thisMon.getDate() + 7);
        const nextThu = new Date(thisThu);
        nextThu.setDate(thisThu.getDate() + 7);
        return [thisMon, thisThu, nextMon, nextThu];
    }

    function jpWeekday(d) {
        return ['日曜日', '月曜日', '火曜日', '水曜日', '木曜日', '金曜日', '土曜日'][d.getDay()];
    }

    // ★ 年も含めてラベル作成
    function formatLabel(d) {
        return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日（${jpWeekday(d)}）`;
    }

    function formatValue(d) {
        const yyyy = d.getFullYear();
        const mm = String(d.getMonth() + 1).padStart(2, "0");
        const dd = String(d.getDate()).padStart(2, "0");
        return `${yyyy}-${mm}-${dd}`;   // ← ローカル基準
    }

    select.innerHTML = "";
    let selected = false;
    getTargetDates().forEach(d => {
        const opt = document.createElement("option");
        const value = formatValue(d);
        opt.value = value;
        opt.textContent = formatLabel(d);
        if (value === current) {
            opt.selected = true;
            selected = true;
        } else if (!selected && d >= today) {
            opt.selected = true;
            selected = true;
        }
        select.appendChild(opt);
    });
});

function handleOrderDateChange() {
    const selected = document.getElementById("order-date").value;
    if (selected) {
        window.location.href = `/forecast?orderDate=${encodeURIComponent(selected)}`;
    }
}