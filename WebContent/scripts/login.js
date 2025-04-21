const form = document.getElementById("form");
const elements = form.elements;

const errorDisplay = document.getElementById("error");


form.addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent the form from submitting normally

    let email = elements["email"];
    let password = elements["password"];

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/login",
        data: {
            email: email,
            password: password
        },
        success: handle_results
    });
});


function handle_results(data)
{
    //return true if there is an error
    if (handle_errors(data)) return;

    //here-on we have a clean email and password
    window.location.href = "api/main";

}

function handle_errors(data)
{
    if (!data.error) return false; //no error
    jQuery("#error").text("Login error: " + data.error);
    return true;
}


jQuery.ajax
(
    {
        dataType: "json",
        method: "POST",
        url: "api/login",
        success: (resultData) => handle_results(resultData)
    }
);