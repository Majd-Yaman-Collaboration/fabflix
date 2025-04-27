const form = document.getElementById("form");
const elements = form.elements;

const errorDisplay = document.getElementById("error");


form.addEventListener("submit", function(event) {
    //start
    event.preventDefault(); // Prevent the form from submitting normally

    jQuery("#error").text(""); //reset the button if they press it again
    //to show that it's reprocessing and not just staying on
    //"wrong email", but rather the website changes a teeny bit to say the same thing
    // (if they're still wrong)

    let email = elements["email"].value;
    let password = elements["password"].value;
    // console.log(email+password);

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/login",
        data: {
            email: email,
            password: password
        },
        success: handle_results //takes the result of this as args
    });
});


function handle_results(data)
{

    if (handle_errors(data)) {return;} //return true if there is an error

    //here-on we have a clean email and password
    window.location.href = "main-page.html";

}

function handle_errors(data)
{

    if (!data.error) {return false;} //no error
    jQuery("#error").text("Login error: " + data.error);
    return true;
}


