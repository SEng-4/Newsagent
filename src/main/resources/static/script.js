const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => document.querySelectorAll(selector);

let snapshot = {};
let cart = {}; // { productId: { product, quantity } }
let productsCache = [];

// ─── TABLE ────────────────────────────────────────────────────────────────────

function populateTable() {
  const tableBody = $("#products tbody");
  tableBody.innerHTML = "";

  fetch("/products")
    .then((r) => {
      if (!r.ok) throw new Error(r.statusText);
      return r.json();
    })
    .then((data) => {
      productsCache = data;
      data.forEach((product) => {
        const inCart = cart[product.id]?.quantity ?? 0;
        const row = document.createElement("tr");
        row.dataset.id = product.id;

        const fields = ["id", "name", "price", "quantity", "type"];
        fields.forEach((field) => {
          const td = document.createElement("td");
          td.dataset.field = field;
          td.textContent = product[field];
          row.appendChild(td);
        });

        // Cart controls cell
        const actionsTd = document.createElement("td");
        actionsTd.className = "cart-actions";
        actionsTd.innerHTML = `
          <div class="qty-control">
            <button class="qty-btn minus-btn" data-id="${product.id}" title="Remove one">−</button>
            <span class="cart-qty" id="cart-qty-${product.id}">${inCart > 0 ? inCart : ""}</span>
            <button class="qty-btn plus-btn" data-id="${product.id}" title="Add one">+</button>
          </div>
        `;
        row.appendChild(actionsTd);
        tableBody.appendChild(row);
      });

      attachCartListeners();
      renderCart(); // update cart UI
    })
    .catch((err) => console.error("Fetch error:", err));
}

function attachCartListeners() {
  $$(".plus-btn").forEach((btn) => {
    btn.addEventListener("click", () => addToCart(Number(btn.dataset.id)));
  });
  $$(".minus-btn").forEach((btn) => {
    btn.addEventListener("click", () => removeFromCart(Number(btn.dataset.id)));
  });
}

// ─── CART LOGIC ───────────────────────────────────────────────────────────────

function addToCart(productId) {
  const product = productsCache.find((p) => p.id === productId);
  if (!product) return;

  const currentInCart = cart[productId]?.quantity ?? 0;
  if (currentInCart >= product.quantity) {
    alert("No more stock available!");
    return;
  }

  cart[productId] = { product, quantity: currentInCart + 1 };
  updateCartBadge(productId);
  renderCart();
}

function removeFromCart(productId) {
  if (!cart[productId]) return;
  cart[productId].quantity -= 1;
  if (cart[productId].quantity <= 0) delete cart[productId];
  updateCartBadge(productId);
  renderCart();
}

function updateCartBadge(productId) {
  const el = $(`#cart-qty-${productId}`);
  if (!el) return;

  const qty = cart[productId]?.quantity ?? 0;

  if (qty > 0) {
    el.textContent = qty;
  } else {
    el.textContent = "";
  }
}

function renderCart() {
  const cartItemsDiv = $("#cart-items");
  const totalSpan = $("#cart-total");
  const checkoutBtn = $("#checkout-btn");
  const items = Object.values(cart);

  if (items.length === 0) {
    cartItemsDiv.innerHTML = "<p>Your cart is empty.</p>";
    totalSpan.textContent = "Total: €0.00";
    checkoutBtn.disabled = true;
    return;
  }

  let total = 0;
  let html = "";
  items.forEach(({ product, quantity }) => {
    const lineTotal = product.price * quantity;
    total += lineTotal;
    html += `
      <div class="cart-item">
        <span>${product.name} (x${quantity})</span>
        <span>€${lineTotal.toFixed(2)}</span>
        <div>
          <button class="cart-minus-btn" data-id="${product.id}">−</button>
          <button class="cart-plus-btn" data-id="${product.id}">+</button>
        </div>
      </div>
    `;
  });

  cartItemsDiv.innerHTML = html;
  totalSpan.textContent = `Total: €${total.toFixed(2)}`;
  checkoutBtn.disabled = false;

  // attach listeners for cart panel buttons
  $$(".cart-plus-btn").forEach((btn) => {
    btn.addEventListener("click", () => addToCart(Number(btn.dataset.id)));
  });
  $$(".cart-minus-btn").forEach((btn) => {
    btn.addEventListener("click", () => removeFromCart(Number(btn.dataset.id)));
  });
}

// ─── CHECKOUT ─────────────────────────────────────────────────────────────────

async function checkout() {
  const items = Object.values(cart).map(({ product, quantity }) => ({
    productId: product.id,
    quantity,
  }));
 
  if (items.length === 0) return;
 
  const checkoutBtn = $("#checkout-btn");
  checkoutBtn.disabled = true;
  checkoutBtn.textContent = "Processing…";
 
  try {
    const response = await fetch("/sales", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ items }),
    });
 
    if (!response.ok) {
      const err = await response.json();
      throw new Error(err.error || "Checkout failed");
    }
 
    const sale = await response.json();
    cart = {};
    renderCart();
    populateTable();
    showReceiptPrompt(sale.id);
    showToast("Sale completed", "success");
  } catch (err) {
    showToast(`Error: ${err.message}`, "error");
    checkoutBtn.disabled = false;
  } finally {
    checkoutBtn.textContent = "Checkout";
  }
}
 
function showReceiptPrompt(saleId) {
  const existing = $("#receipt-toast");
  if (existing) existing.remove();
 
  const div = document.createElement("div");
  div.id = "receipt-toast";
  div.className = "receipt-toast";
  div.innerHTML = `
    <span>Sale #${saleId} recorded.</span>
    <a href="/sales/${saleId}/receipt" download="receipt_${saleId}.pdf" class="receipt-link">
      ⬇ Download Receipt
    </a>
    <button class="receipt-close" onclick="this.parentElement.remove()">✕</button>
  `;
  document.body.appendChild(div);
 
  // auto-remove after 15s
  setTimeout(() => div.remove(), 15000);
}

// ─── PRODUCT MODAL ───────────────────────────────────────────────

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
        document.body.removeChild(modal);
        populateTable();
      })
      .catch((error) => {
        alert(`Error ${isEdit ? "updating" : "adding"} product.`);
        console.error("Error:", error);
      });
  });
}

// ─── EDIT MODE ───────────────────────────────────────────────────

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

async function saveEdits() {
  const changes = [];

  document.querySelectorAll("#products tbody tr").forEach((row) => {
    const id = row.dataset.id;
    const name = row.querySelector("[data-field='name'] input").value;
    const price = row.querySelector("[data-field='price'] input").value;
    const quantity = row.querySelector("[data-field='quantity'] input").value;
    const type = row.querySelector("[data-field='type'] input").value;

    const product = { id, name, price, quantity, type };

    const promise = fetch(`/products/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(product),
    }).then((response) => {
      if (!response.ok) {
        alert("Error saving product.");
      }
      return response;
    });

    changes.push(promise);
  });

  try {
    await Promise.all(changes);
    console.log("Product saved successfully!");
    exitEditMode();
    populateTable();
  } catch (error) {
    console.error(error);
  }
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

// ─── INIT ─────────────────────────────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
  populateTable();
  document.getElementById("create-btn").addEventListener("click", () => {
    showProductModal();
  });
  document.getElementById("checkout-btn").addEventListener("click", checkout);
});