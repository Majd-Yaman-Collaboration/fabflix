const form = document.getElementById("payment-form");
const element = form.elements;
const errorDisplay = document.getElementById("error");
const urlParams = new URLSearchParams(window.location.search);
const total = urlParams.get('total') || '0.00';
document.getElementById('cart-total').textContent = total;

form.addEventListener("submit", function(event) {
    event.preventDefault();
    let firstName = element["firstName"].value;
    let lastName = element["lastName"].value;
    let cardNumber = element["cardNumber"].value;
    let expirationDate = element["expirationDate"].value;

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/checkout-servlet",
        data: {
            firstName:firstName,
            lastName:lastName,
            cardNumber:cardNumber,
            expirationDate:expirationDate
        },
        success: handle_results
    });
});

function handle_results(data) {
    if (data.status === "success") {
        window.location.href = "confirmation.html"
    }
    else {
        document.getElementById("error").textContent = "Incorrect Payment Info";
        window.scrollTo(0, document.body.scrollHeight);
    }
}

