 const urlParams = new URLSearchParams(window.location.search);
    const starId = urlParams.get("id");

    //sends id to servlet
    fetch(`api/single-star?id=${starId}`)
    .then(response => response.json())
    .then(data => {
    const container = document.getElementById("star-info");
    const moviesHTML = data.movies.map(movie =>
    `<li><a href="single-movie.html?id=${movie.id}">${movie.title}</a></li>`
    ).join("");

    container.innerHTML = `
        <p><strong>Name:</strong> ${data.name}</p>
        <p><strong>Birth Year:</strong> ${data.birthYear || "N/A"}</p>
        <p><strong>Movies:</strong></p>
        <ul>${moviesHTML}</ul>
      `;
})
    .catch(error => {
    document.body.innerHTML += `<p>Error loading star: ${error}</p>`;
});
