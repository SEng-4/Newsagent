document.addEventListener('DOMContentLoaded', () => {
    const tableBody = document.querySelector('#productTable tbody');

    fetch('/products')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network error: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            data.forEach(product => {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = product.id;
                row.appendChild(idCell);

                const nameCell = document.createElement('td');
                nameCell.textContent = product.name;
                row.appendChild(nameCell);

                const priceCell = document.createElement('td');
                priceCell.textContent = product.price;
                row.appendChild(priceCell);

                const quantityCell = document.createElement('td');
                quantityCell.textContent = product.quantity;
                row.appendChild(quantityCell);

                const typeCell = document.createElement('td');
                typeCell.textContent = product.type;
                row.appendChild(typeCell);

                tableBody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('Fetch error:', error);
        });
});