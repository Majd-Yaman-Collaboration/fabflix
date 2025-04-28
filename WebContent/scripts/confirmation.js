document.addEventListener('DOMContentLoaded', function() {
    fetchCartAndConfirm();
});

//step 1: good
function fetchCartAndConfirm() {
    jQuery.ajax({
        method: "POST",
        url: "api/shopping-cart",
        dataType: "json",
        success: function(cart) {
            console.log("Cart from session:", cart);
            processCart(cart);
        }
    });
}

//step 2:
function processCart(cart) {
    const movieIds = Object.keys(cart);
    let totalAmount = 0.00;

    movieIds.forEach(movieId => {
        const quantity = cart[movieId];

        jQuery.ajax({
            method: "POST",
            url: "api/confirmation-servlet",
            dataType: "json",
            data: {
                movieId: movieId,
                saleDate: new Date().toISOString().split('T')[0],
                quantity:quantity
            },
            success: function(data) {
                if (data.saleId) {
                    addRowToConfirmationTable(data, movieId, quantity);
                    const pricePerItem = parseFloat(data.rating) + 5.39;
                    totalAmount += pricePerItem * quantity;
                    document.getElementById("total-amount").textContent = totalAmount.toFixed(2);
                }
            }
        });
    });
    $.ajax({
        method: "POST",
        url: "api/shopping-cart",
        dataType: "json",
        data: {
            reset:true
        }
    });

}

function addRowToConfirmationTable(data, movieId, quantity) {
    const tableBody = document.getElementById("conf-table-body");

    const pricePerItem = parseFloat(data.rating) + 5.39;
    const totalPriceForRow = (pricePerItem * quantity).toFixed(2);

    const row = document.createElement("tr");
    row.innerHTML = `
        <td>${data.saleId}</td>
        <td>${data.title}</td>
        <td>${quantity}</td>
        <td>$${totalPriceForRow}</td>
    `;
    tableBody.appendChild(row);
}
