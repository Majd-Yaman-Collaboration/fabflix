fetch("api/movies")
    .then(response => response.json())
    .then(data => {
        const tbody = document.getElementById("movie-table-body");
        data.forEach(movie => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td><a href="single-movie.html?id=${movie.id}">${movie.title}</a></td>
                <td>${movie.year}</td>
                <td>${movie.director}</td>
                <td>${movie.genres.join(", ")}</td>
                <td>${movie.stars.map(s => `<a href="single-star.html?id=${s.id}">${s.name}</a>`).join(", ")}</td>
                <td>${movie.rating}</td>
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        document.body.innerHTML += `<p>Loading Error: ${error}</p>`;
    });