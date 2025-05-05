function goBackToSavedMovieList() {
    const lastUrl = sessionStorage.getItem("lastMovieListUrl");
    if (lastUrl) {
        window.location.href = lastUrl;
    } else {
        // fallback in case it's missing (first time or cleared storage)
        window.location.href = "movie-list.html";
    }
}