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

function addProduct() {
    const name = prompt("Enter product name:");
    const price = prompt("Enter product price:");
    const quantity = prompt("Enter quantity:");
    const type = prompt("Enter type:");

    const product = {
        name: name,
        price: price,
        quantity: quantity,
        type: type
    }

    fetch("/products", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(product)
    }).then((response) => {
        if (response.ok) {
            console.log("Product added successfully!");
            populateTable();
        } else {
            alert("Error adding product.");
            console.error("Error:", error);
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
  populateTable();
});