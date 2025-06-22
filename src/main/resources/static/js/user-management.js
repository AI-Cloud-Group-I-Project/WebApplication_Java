
function enableEdit(btn) {
  const form = btn.closest("form");
  form.querySelectorAll("input, select").forEach(el => el.disabled = false);
  form.querySelector(".submit").disabled = false;
}


function toggleUserFilter() {
  const checkbox = document.getElementById("showActiveOnly");
  const rows = document.querySelectorAll("tr[data-status]");


  rows.forEach(row => {
    if (row.id === "addRow") return;


    const status = row.getAttribute("data-status");
    row.style.display = checkbox.checked && status !== "active" ? "none" : "";
  });
}



function updateStatusAttr(sel) {
  const tr = sel.closest("tr");
  if (tr) tr.setAttribute("data-status", sel.value);
}


function toggleAddRow() {
  const row = document.getElementById("addRow");
  row.style.display = (row.style.display === "none" || !row.style.display) ? "" : "none";
}

window.roles = [
  { id: 1, name: "admin" },
  { id: 2, name: "user" },
  { id: 3, name: "viewer" }
];
