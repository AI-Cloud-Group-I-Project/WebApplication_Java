document.addEventListener("DOMContentLoaded", function () {
  // --- 既存のEditボタン処理 ---
  document.querySelectorAll(".btn.edit").forEach(function (editBtn) {
    editBtn.addEventListener("click", function () {
      const form = editBtn.closest(".user-form");
      const row = form.closest("tr");

      row.querySelectorAll("input, select").forEach(function (element) {
        element.disabled = false;
      });

      editBtn.classList.add("active");

      const submitBtn = form.querySelector(".btn.submit");
      if (submitBtn) {
        submitBtn.disabled = false;
      }
    });
  });

  // --- Addボタンの処理（上に追加） ---
  const addBtn = document.querySelector(".btn.add");
  const table = document.querySelector(".user-table");

  if (addBtn && table) {
    addBtn.addEventListener("click", function (event) {
      event.preventDefault();

      const newRow = document.createElement("tr");
      newRow.innerHTML = `
            <td colspan="4">
              <form action="/users/update" method="post" class="form">
                <table style="width: 100%;">
                  <tr>
                    <td style="width: 20%;">
                      <input type="text" name="name" value="" class="display-name" />
                    </td>
                    <td style="width: 30%;">
                      <input type="email" name="email" value="" class="display-email" />
                    </td>
                    <td style="width: 15%;">
                      <select name="role.id" class="display-role">
                        <option value="">選択してください</option>
                        ${window.roles.map(role =>
        `<option value="${role.id}">${role.name}</option>`).join('')}
                      </select>
                    </td>
                    <td style="width: 35%;">
                      <input type="hidden" name="id" value="" />
                      <button class="btn edit active" type="button" disabled>Edit</button>
                      <button class="btn submit" type="submit" name="action" value="submit">Submit</button>
                      <button class="btn reset" type="submit" name="action" value="reset" disabled>Password Reset</button>
                    </td>
                  </tr>
                </table>
              </form>
            </td>
        `;

      // tbody の最初に追加する
      const tbody = table.querySelector("tbody");
      if (tbody && tbody.firstChild) {
        tbody.insertBefore(newRow, tbody.firstChild);
      } else if (tbody) {
        tbody.appendChild(newRow); // 念のため空の場合は追加
      }
    });
  }


});
