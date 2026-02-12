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

function editProduct() {
    const id = prompt("Enter product ID to edit:");
    
    if (!id) return;
    
    fetch(`/products/${id}`)
        .then((getResponse) => {
            if (!getResponse.ok) {
                alert("Product not found!");
                return;
            }
            return getResponse.json();
        })
        .then((existingProduct) => {
            if (!existingProduct) return;
            
            const name = prompt("Edit product name:", existingProduct.name) || existingProduct.name;
            const price = prompt("Edit product price:", existingProduct.price) || existingProduct.price;
            const quantity = prompt("Edit quantity:", existingProduct.quantity) || existingProduct.quantity;
            const type = prompt("Edit type:", existingProduct.type) || existingProduct.type;
            
            const updatedProduct = {
                id: id,
                name: name,
                price: parseFloat(price),
                quantity: parseInt(quantity),
                type: type
            };
            
            return fetch(`/products/${id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(updatedProduct)
            });
        })
        .then((putResponse) => {
            if (putResponse && putResponse.ok) {
                console.log("Product edited successfully!");
                populateTable();
            } else if (putResponse) {
                alert("Error editing product.");
            }
        })
        .catch((error) => {
            alert("Error editing product.");
            console.error("Error:", error);
        });
}

document.addEventListener("DOMContentLoaded", () => {
  populateTable();
});