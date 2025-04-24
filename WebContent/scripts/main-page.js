const genres = [
    "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy",
    "Crime", "Documentary", "Drama", "Family", "Fantasy", "History",
    "Horror", "Music", "Musical", "Mystery", "Reality-TV", "Romance",
    "Sci-Fi", "Sport", "Thriller", "War", "Western"
];

const genreContainer = document.getElementById("genres-container");
const columnSizes = [6, 6, 6, 5];
let currentIndex = 0;
columnSizes.forEach(size => {
    const colDiv = document.createElement("div");
    colDiv.className = "genre-column";
    for (let i = 0; i < size; i++) {
        const genre = genres[currentIndex++];
        const link = document.createElement("a");
        link.href = `/movie-list.html?filter=genre&value=${encodeURIComponent(genre)}`;
        link.className = "genre";
        link.textContent = genre;
        colDiv.appendChild(link);
    }
    genreContainer.appendChild(colDiv);
});

const letters = "ABCDEFGHIJKLMANOPQRSTUVWXYZ".split("");
const numbers = "0123456789*".split("");
const letterRow = document.getElementById("letter-row");
const numberRow = document.getElementById("number-row");

letters.forEach(char => {
    const link = document.createElement("a");
    link.href = `/movie-list.html?filter=title&value=${encodeURIComponent(char)}`;
    link.className = "title-char";
    link.textContent = char;
    letterRow.appendChild(link);
});

numbers.forEach(char => {
    const link = document.createElement("a");
    link.href = `/movie-list.html?filter=title&value=${encodeURIComponent(char)}`;
    link.className = "title-char";
    link.textContent = char;
    numberRow.appendChild(link);
});