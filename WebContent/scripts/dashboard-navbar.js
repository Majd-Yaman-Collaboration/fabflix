fetch('components/dashboard-navbar.html')
    .then(response => response.text())
    .then(data => {
        document.getElementById('dashboard-navbar').innerHTML = data;
    });