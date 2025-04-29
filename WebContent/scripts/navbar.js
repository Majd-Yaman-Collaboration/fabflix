fetch('components/navbar.html')
    .then(response => response.text())
    .then(data => {
        document.getElementById('navbar').innerHTML = data;
        document.getElementById("search-toggle").addEventListener("click", () => {
            document.getElementById("search-inputs").classList.toggle("visible");
        });

        setupNavbarListeners();
    });

function setupNavbarListeners() {
    document.getElementById("search-form").addEventListener("submit", handleSearchSubmit);


}


function handleSearchSubmit(e) {
    e.preventDefault();


    const search_title    = document.getElementById("title").value;
    const search_year     = document.getElementById("year").value;
    const search_director = document.getElementById("director").value;
    const search_star     = document.getElementById("star").value;
    window.location.replace(
        `movie-list.html?
title=${search_title}&
year=${search_year}&
director=${search_director}&
star=${search_star}
`
    );

}