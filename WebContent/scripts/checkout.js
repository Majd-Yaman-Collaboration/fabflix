document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('payment-form').addEventListener('submit', function(e) {
        e.preventDefault();
    });

    const urlParams = new URLSearchParams(window.location.search);
    const total = urlParams.get('total') || '0.00';
    document.getElementById('cart-total').textContent = total;
});
