jQuery.ajax({
    method: "GET",
    url: "api/movies",
    data: {
        filter: new URLSearchParams(window.location.search).get('filter'),
        value: new URLSearchParams(window.location.search).get('value')
    },
    dataType: "json",
    success: handleMovieResults,
    error: handleMovieErrors
});

function handleMovieErrors(error) {
    window.location.href = "login.html";
}

function handleMovieResults(data) {
    const tbody = jQuery("#movie-table-body");
    const urlParams = new URLSearchParams(window.location.search);
    const filterType = urlParams.get('filter');
    const filterValue = urlParams.get('value');

    let title = 'Movies';
    if (filterType === 'genre') {
        title = `${filterValue} Movies`;
    } else if (filterType === 'title') {
        if (filterValue === '*') {
            title = 'Movies starting with non-alphanumeric characters';
        } else {
            title = `Movies starting with ${filterValue}`;
        }
    }

    document.querySelector('h1').textContent = title;

    data.forEach(function(movie) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td><a href="single-movie.html?id=${movie.id}" class="details">${movie.title}</a></td>
            <td>${movie.year}</td>
            <td>${movie.director}</td>
            <td>${movie.genres.join(", ")}</td>
            <td>${movie.stars.map(s => `<a href="single-star.html?id=${s.id}" class="details">${s.name}</a>`).join(", ")}</td>
            <td>${movie.rating}</td>
        `;
        tbody[0].appendChild(row);
    });
}