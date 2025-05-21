const genreContainer = document.getElementById("genres-container");

fetch("api/main-page")
    .then(response => response.json())
    .then(data => {
        const genres = data.genres;
        const numColumns = 4;
        const genresPerColumn = Math.ceil(genres.length / numColumns);

        for (let col = 0; col < numColumns; col++) {
            const colDiv = document.createElement("div");
            colDiv.className = "genre-column";

            const start = col * genresPerColumn;
            const end = start + genresPerColumn;

            genres.slice(start, end).forEach(genre => {
                const link = document.createElement("a");
                link.href = `movie-list.html?filter=genre&value=${encodeURIComponent(genre)}`;
                link.className = "genre";
                link.textContent = genre;
                colDiv.appendChild(link);
            });

            genreContainer.appendChild(colDiv);
        }
    })
    .catch(error => {
        console.error("Failed to load genres:", error);
    });

const letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
const numbers = "0123456789*".split("");

const letterRow = document.getElementById("letter-row");
const numberRow = document.getElementById("number-row");

letters.forEach(char => {
    const link = document.createElement("a");
    link.href = `movie-list.html?filter=title&value=${encodeURIComponent(char)}`;
    link.className = "title-char";
    link.textContent = char;
    letterRow.appendChild(link);
});

numbers.forEach(char => {
    const link = document.createElement("a");
    link.href = `movie-list.html?filter=title&value=${encodeURIComponent(char)}`;
    link.className = "title-char";
    link.textContent = char;
    numberRow.appendChild(link);
});
