document.addEventListener("DOMContentLoaded", () => {
    const overlay = document.getElementById("loading-overlay");
    if (!overlay) return;

    /* ───────── リンククリック時 ───────── */
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", () => {
            const href = link.getAttribute("href");
            if (!href || href.startsWith("#") || href.startsWith("javascript:") || link.target === "_blank") return;
            overlay.style.display = "flex";
        });
    });

    /* ───────── フォーム送信時 ───────── */
    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", () => {
            overlay.style.display = "flex";
            setTimeout(() => timeoutGuard(overlay), 20000);
        });
    });

    /* ───────── 発注日 <select> 変更時 ───────── */
    const orderSelect = document.getElementById("order-date");
    if (orderSelect) {
        orderSelect.addEventListener("change", () => {
            const val = orderSelect.value;
            if (!val) return;                         // 未選択ならスキップ
            overlay.style.display = "flex";
            setTimeout(() => timeoutGuard(overlay), 25000);

            // 少し描画を挟んでから遷移
            requestAnimationFrame(() => {
                setTimeout(() => {
                    window.location.href = `/forecast?orderDate=${encodeURIComponent(val)}`;
                }, 50);
            });
        });
    }
});

/* 画面戻る/進む・ロード完了時にローディング解除 */
["pageshow", "load"].forEach(ev =>
    window.addEventListener(ev, () => {
        const overlay = document.getElementById("loading-overlay");
        if (overlay) overlay.style.display = "none";
    })
);

/* タイムアウト保険 */
function timeoutGuard(overlay) {
    if (overlay.style.display !== "none") {
        overlay.style.display = "none";
        alert("サーバとの通信がタイムアウトしました。再度試してください。");
    }
}
