fetch('/components/navbar.html')
    .then(response => response.text())
    .then(data => {
        document.getElementById('navbar').innerHTML = data;
        document.getElementById("search-toggle").addEventListener("click", () => {
            document.getElementById("search-inputs").classList.toggle("visible");
        });
    });


function handleSearchResults(data)
{
    console.log("gets to handleSearchResults");
}


document.getElementById("search-form").addEventListener("submit", function(e) {
    e.preventDefault();

    const data = {
        search_title: document.getElementById("title").value,
        search_year: document.getElementById("year").value,
        search_director: document.getElementById("director").value,
        search_star: document.getElementById("star").value
    };

    jQuery.ajax({
        method: "GET",
        url: "api/movies",
        data: data,
        dataType: "json",
        success: handleSearchResults
    });


});