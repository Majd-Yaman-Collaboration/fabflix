let cartItems = [];

function loadCartItems() {
    $.ajax({
        method: "POST",
        url: "api/shopping-cart",
        dataType: "json",
        success: function(data) {
            cartItems = Object.entries(data).map(([movieId, quantity]) => ({
                movieId: movieId,
                quantity: quantity
            }));
            fetchMovieDetails();
        }
    });
}

function fetchMovieDetails() {
    cartItems.forEach((item, index) => {
        fetch(`api/single-movie?id=${item.movieId}`)
            .then(response => response.json())
            .then(movieData => {
                cartItems[index] = {
                    ...item,
                    title: movieData.title,
                    price: (parseFloat(movieData.rating) + 5.39)
                };
                updateCart();
            })
            .catch(error => {
                console.error('Error fetching movie details:', error);
            });
    });
}

function updateCart() {
    const tbody = document.getElementById('cart-table-body');
    tbody.innerHTML = '';
    let total = 0;

    cartItems.forEach((item, index) => {
        if (!item.title) return; 
        
        const row = document.createElement('tr');
        const subtotal = item.quantity * parseFloat(item.price);
        total += subtotal;
        
        row.innerHTML = `
            <td>${item.title}</td>
            <td>${item.quantity}</td>
            <td>$${item.price.toFixed(2)}</td>
            <td>$${subtotal.toFixed(2)}</td>
            <td class="action-buttons">
                <button class="increment-btn" onclick="changeQuantity(${index}, 1)">+</button>
                <button class="decrement-btn" onclick="changeQuantity(${index}, -1)">-</button>
                <button class="remove-btn" onclick="removeItem(${index})">Remove</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    document.getElementById('total-amount').textContent = total.toFixed(2);
}

function changeQuantity(index, amt) {
    if (cartItems[index].quantity + amt > 0) {
        cartItems[index].quantity += amt;
        updateCart();
    }
}

function removeItem(index) {
    cartItems.splice(index, 1);
    updateCart();
}

document.getElementById('proceed-to-payment').addEventListener('click', function() {
    const total = document.getElementById('total-amount').textContent;
    window.location.href = `checkout.html?total=${total}`;
});

loadCartItems();
