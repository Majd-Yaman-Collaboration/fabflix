document.getElementById("add-movie-form").addEventListener("submit", function (event) {
    event.preventDefault();

    const movieTitle = document.getElementById("movie-title").value;
    const movieYear = document.getElementById("movie-year").value;
    const movieDirector = document.getElementById("movie-director").value;
    const starName = document.getElementById("star-name").value;
    const birthYear = document.getElementById("birth-year").value;
    const genreName = document.getElementById("genre-name").value;
    const errorDisplay = document.getElementById("error");

    const params = new URLSearchParams();
    params.append("movieTitle", movieTitle);
    params.append("movieYear", movieYear);
    params.append("movieDirector", movieDirector);
    params.append("starName", starName);
    params.append("birthYear", birthYear);
    params.append("genreName", genreName);

    fetch("_dashboard/api/add-movie", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params.toString()
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                errorDisplay.textContent = data.message;
            }
            else {
                errorDisplay.style.color = "red";
                errorDisplay.textContent = data.error || "Failed to add movie.";
            }
        });
});