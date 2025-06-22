
function enableEdit(button) {
    const row = button.closest("tr");

    // 入力欄のdisabledを解除（hidden以外）
    row.querySelectorAll("input").forEach(input => {
        if (input.type !== "hidden") {
            input.disabled = false;
        }
    });

    // セレクトボックスも有効化
    row.querySelectorAll("select").forEach(select => {
        select.disabled = false;
    });

    // Submitボタンも有効化
    const submitBtn = row.querySelector(".btn.submit");
    if (submitBtn) {
        submitBtn.disabled = false;
    }
    // Editボタンをactiveに
    button.classList.add("active");
}



function toggleAddRow() {
    const addRow = document.getElementById("addRow");
    addRow.style.display = addRow.style.display === "none" ? "table-row-group" : "none";
}

function deleteProduct(productId) {
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/products/delete";

    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "id";
    input.value = productId;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}