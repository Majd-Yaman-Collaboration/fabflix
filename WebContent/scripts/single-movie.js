const urlParams = new URLSearchParams(window.location.search);
const movieId = urlParams.get("id");

fetch(`api/single-movie?id=${movieId}`)

    .then(response => response.json())
    .then(data => {
        const movie = data;
        const container = document.getElementById("movie-detail");

        const html = `
                <h2>${movie.title} (${movie.year})</h2>
                <p><strong>Director:</strong> ${movie.director}</p>
                <p><strong>Genres:</strong> ${movie.genres.map(g => `<a href="movie-list.html?filter=genre&value=${g}">${g}</a>`).join(", ")}</p>
                <p><strong>Stars:</strong> ${movie.stars.map(s => `<a href="single-star.html?id=${s.id}">${s.name}</a>`).join(", ")}</p>
                <p><strong>Rating:</strong> ${movie.rating}</p>
            `;
        container.innerHTML = html;

        document.getElementById('add-to-cart-btn').addEventListener('click', function() {
            console.log("Added To Cart Movie:", movieId);
        }
    )
    })
    .catch(error => {
        document.body.innerHTML += `<p>Loading Error: ${error}</p>`;
    });