const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => document.querySelectorAll(selector);

let snapshot = {};

function populateTable() {
  const tableBody = document.querySelector("#products tbody");

  // Clear the table body to prevent duplicates
  tableBody.innerHTML = "";

  fetch("/products")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Network error: " + response.statusText);
      }
      return response.json();
    })
    .then((data) => {
      data.forEach((product) => {
        const row = document.createElement("tr");
        row.dataset.id = product.id;

        ["id", "name", "price", "quantity", "type"].forEach((field) => {
          const td = document.createElement("td");
          td.dataset.field = field;
          td.textContent = product[field];
          row.appendChild(td);
        });

        tableBody.appendChild(row);
      });
    })
    .catch((error) => {
      console.error("Fetch error:", error);
    });
}

function showProductModal(product = null) {
  const modal = document.createElement("div");
  modal.className = "modal";

  const isEdit = product !== null;
  const title = isEdit ? "Edit Product" : "Add Product";
  const submitText = isEdit ? "Update" : "Create";

  modal.innerHTML = `
    <div class="modal-content">
      <h2>${title}</h2>
      <form id="product-form">
        <label for="product-name">Product Name:</label>
        <input type="text" id="product-name" name="product-name" value="${isEdit ? product.name : ""}" required />

        <label for="product-price">Price:</label>
        <input type="number" id="product-price" name="product-price" step="0.01" value="${isEdit ? product.price : ""}" required />

        <label for="product-quantity">Quantity:</label>
        <input type="number" id="product-quantity" name="product-quantity" value="${isEdit ? product.quantity : ""}" required />

        <label for="product-type">Type:</label>
        <input type="text" id="product-type" name="product-type" value="${isEdit ? product.type : ""}" required />

        <button type="submit">${submitText}</button>
        <button type="button" id="cancel-btn">Cancel</button>
      </form>
    </div>
  `;

  document.body.appendChild(modal);

  $("#cancel-btn").addEventListener("click", () => {
    document.body.removeChild(modal);
  });

  $("#product-form").addEventListener("submit", (event) => {
    event.preventDefault();

    const productData = {
      name: $("#product-name").value,
      price: $("#product-price").value,
      quantity: $("#product-quantity").value,
      type: $("#product-type").value,
    };

    const url = isEdit ? `/products/${product.id}` : "/products";
    const method = isEdit ? "PUT" : "POST";

    fetch(url, {
      method: method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(productData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network error: " + response.statusText);
        }
        return response.json();
      })
      .then(() => {
        console.log(`Product ${isEdit ? "updated" : "added"} successfully!`);
        document.body.removeChild(modal);
        populateTable();
      })
      .catch((error) => {
        alert(`Error ${isEdit ? "updating" : "adding"} product.`);
        console.error("Error:", error);
      });
  });
}

function enterEditMode() {
  snapshot = {};
  document.querySelectorAll("#products tbody tr").forEach((row) => {
    const id = row.dataset.id;
    snapshot[id] = {};
    row.querySelectorAll("td[data-field]").forEach((td) => {
      snapshot[id][td.dataset.field] = td.textContent;
      if (td.dataset.field === "id") return;
      td.innerHTML = `<input class="edit-input" value="${td.textContent}">`;
    });
  });
  $("#edit-btn").style.display = "none";
  $("#save-btn").style.display = "inline-block";
  $("#discard-btn").style.display = "inline-block";
}

function saveEdits() {
  document.querySelectorAll("#products tbody tr").forEach((row) => {
    const id = row.dataset.id;
    const name = row.querySelector("[data-field='name'] input").value;
    const price = row.querySelector("[data-field='price'] input").value;
    const quantity = row.querySelector("[data-field='quantity'] input").value;
    const type = row.querySelector("[data-field='type'] input").value;

    const product = { id, name, price, quantity, type };

    fetch(`/products/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(product)
    }).then((response) => {
      if (response.ok) {
        console.log("Product saved successfully!");
      } else {
        alert("Error saving product.");
      }
    });
  });

  exitEditMode();
  populateTable();
}

function discardEdits() {
  document.querySelectorAll("#products tbody tr").forEach((row) => {
    const id = row.dataset.id;
    row.querySelectorAll("td[data-field]").forEach((td) => {
      td.textContent = snapshot[id][td.dataset.field];
    });
  });
  exitEditMode();
}

function exitEditMode() {
  snapshot = {};
  $("#edit-btn").style.display = "inline-block";
  $("#save-btn").style.display = "none";
  $("#discard-btn").style.display = "none";
}

document.addEventListener("DOMContentLoaded", () => {
  populateTable();
  document.getElementById("create-btn").addEventListener("click", () => {
    showProductModal();
  });
});