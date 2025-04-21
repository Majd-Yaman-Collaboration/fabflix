fetch('/components/navbar.html')
    .then(response => response.text())
    .then(data => {
        document.getElementById('navbar').innerHTML = data;
        document.getElementById("search-toggle").addEventListener("click", () => {
            document.getElementById("search-inputs").classList.toggle("visible");
        });
    });
