jQuery.ajax({
        method: "GET",
        url: "api/movies",
        dataType: "json",
        success: handleMovieResults,
        error: handleMovieErrors
    }
)


function handleMovieErrors(error)
{
    window.location.href = "login.html";
}

function handleMovieResults(data) {
        const tbody = jQuery("#movie-table-body");

        data.forEach(function(movie)
                {
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
                }
            )

}