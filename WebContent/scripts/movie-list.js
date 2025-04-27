let currentPage = 1;
let currentLimit = 25;
let currentSort = 1;
let totalPages = 1;

function updatePageControls() {
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');
    
    prevButton.disabled = currentPage === 1;
    nextButton.disabled = currentPage === totalPages;
    pageInfo.textContent = `Page ${currentPage} of ${totalPages}`;
}

function updateURL() {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('page', currentPage);
    urlParams.set('limit', currentLimit);
    urlParams.set('sort', currentSort);
    const newURL = window.location.pathname + '?' + urlParams.toString();
    // update url with new queries
    window.history.pushState({}, '', newURL);
}

function loadMovies() {
    const urlParams = new URLSearchParams(window.location.search);
    const filterType = urlParams.get('filter');
    const filterValue = urlParams.get('value');
    const title = urlParams.get('title');
    const year = urlParams.get('year');
    const director = urlParams.get('director');
    const star = urlParams.get('star');

    jQuery.ajax({
        method: "GET",
        url: "api/movies",
        data: {
            filter: filterType,
            value: filterValue,
            page: currentPage,
            limit: currentLimit,
            title: title,
            year: year,
            director: director,
            star: star
        },
        dataType: "json",
        success: handleMovieResults,
    });
}


function handleMovieResults(data) {
    const tbody = jQuery("#movie-table-body");
    tbody.empty();
    
    const urlParams = new URLSearchParams(window.location.search);
    const filterType = urlParams.get('filter');
    const filterValue = urlParams.get('value');

    let title = 'Results';
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

    if (data.movies.length === 0) {
        currentPage = Math.max(1, currentPage - 1);
        loadMovies();
        return;
    }

    totalPages = data.totalPages;
    updatePageControls();
    updateURL();

    data.movies.forEach(function(movie) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td><a href="single-movie.html?id=${movie.id}" class="details">${movie.title}</a></td>
            <td>${movie.year}</td>
            <td>${movie.director}</td>
            <td>${movie.genres.map(g => `<a href="movie-list.html?filter=genre&value=${g}&page=1&limit=25" class="details">${g}</a>`).join(", ")}</td>
            <td>${movie.stars.map(s => `<a href="single-star.html?id=${s.id}" class="details">${s.name}</a>`).join(", ")}</td>
            <td>${movie.rating}</td>
        `;
        tbody[0].appendChild(row);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const pageSizeSelect = document.getElementById('page-size');
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const urlParams = new URLSearchParams(window.location.search);
    const pageParam = urlParams.get('page');
    const limitParam = urlParams.get('limit');
    
    if (pageParam) {
        currentPage = parseInt(pageParam);
    }
    if (limitParam) {
        currentLimit = parseInt(limitParam);
    }

    pageSizeSelect.value = currentLimit.toString();
    pageSizeSelect.addEventListener('change', function() {
        const newLimit = parseInt(this.value);
        if (newLimit !== currentLimit) {
            currentLimit = newLimit;
            currentPage = 1;
            loadMovies();
        }
    });
    prevButton.addEventListener('click', function() {
        if (currentPage > 1) {
            currentPage--;
            loadMovies();
            window.scrollTo(0,0);
        }
    });
    nextButton.addEventListener('click', function() {
        if (currentPage < totalPages) {
            currentPage++;
            loadMovies();
            window.scrollTo(0,0);
        }
    });
    loadMovies();
});