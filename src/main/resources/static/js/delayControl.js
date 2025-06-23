document.addEventListener("DOMContentLoaded", () => {
    const overlay = document.getElementById("loading-overlay");

    // すべてのリンクにローディングを追加
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", e => {
            const href = link.getAttribute("href");

            // 外部リンクや # アンカーリンクは除外
            if (!href || href.startsWith("#") || href.startsWith("javascript:") || link.target === "_blank") return;

            overlay.style.display = "flex";
        });
    });

    // すべてのフォーム送信にもローディングを追加
    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", () => {
            overlay.style.display = "flex";
        });
    });
});
