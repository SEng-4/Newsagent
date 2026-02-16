const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => document.querySelectorAll(selector);

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

        const idCell = document.createElement("td");
        idCell.textContent = product.id;
        row.appendChild(idCell);

        const nameCell = document.createElement("td");
        nameCell.textContent = product.name;
        row.appendChild(nameCell);

        const priceCell = document.createElement("td");
        priceCell.textContent = product.price;
        row.appendChild(priceCell);

        const quantityCell = document.createElement("td");
        quantityCell.textContent = product.quantity;
        row.appendChild(quantityCell);

        const typeCell = document.createElement("td");
        typeCell.textContent = product.type;
        row.appendChild(typeCell);

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

document.addEventListener("DOMContentLoaded", () => {
  populateTable();
});

document
  .getElementById("create-btn")
  .addEventListener("click", () => {
    showProductModal();
  });