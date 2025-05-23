let autocompleteCache = {};

document.addEventListener("DOMContentLoaded", function () {
    $('#title-input').autocomplete({
        minChars: 3,
        deferRequestBy: 300,
        triggerSelectOnValidInput: false,
        lookup: function (query, done) {
            if (autocompleteCache[query]) {
                done({ suggestions: autocompleteCache[query] });
            } else {
                $.ajax({
                    method: "GET",
                    url: "api/search",
                    data: {
                        mode: "autocomplete",
                        "title-input": query
                    },
                    success: function (data) {
                        autocompleteCache[query] = data;
                        done({ suggestions: data });
                    }
                });
            }
        },
        onSelect: function (suggestion) {
            const movieId = suggestion.data.movieId;
            window.location.href = `single-movie.html?id=${movieId}`;
        }
    });

    $('#search-btn').click(function () {
        const query = $('#title-input').val().trim();
        if (query.length > 0) {
            window.location.href = `movie-list.html?title=${query}&year=&director=&star=`;
        }
    });

    $('#title-input').keypress(function (event) {
        if (event.key === 'Enter') {
            const query = $('#title-input').val().trim();
            if (query.length > 0) {
                window.location.href = `movie-list.html?title=${query}&year=&director=&star=`;
            }
        }
    });
});
