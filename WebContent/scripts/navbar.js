fetch('/components/navbar.html')
    .then(response => response.text())
    .then(data => {
        document.getElementById('navbar').innerHTML = data;
        document.getElementById("search-toggle").addEventListener("click", () => {
            document.getElementById("search-inputs").classList.toggle("visible");
        });

        console.log("peepee poopoo");
        setupNavbarListeners();
    });

function setupNavbarListeners() {
    document.getElementById("search-form").addEventListener("submit", handleSearchSubmit);
    console.log("peepee poopoosssss");

}


function handleSearchSubmit(e) {
    e.preventDefault();
    console.log("I got here");
    const data = {
        search_title: document.getElementById("title").value,
        search_year: document.getElementById("year").value,
        search_director: document.getElementById("director").value,
        search_star: document.getElementById("star").value
    };

    jQuery.ajax({ //I got here before going to work. error happened
        method: "GET",
        url: "api/movies",
        data: data,
        dataType: "json",
        success: handleSearchResults
    });
}


function handleSearchResults(data) {
    console.log("scoobeedoo");
    console.log("got search results:", data);
}