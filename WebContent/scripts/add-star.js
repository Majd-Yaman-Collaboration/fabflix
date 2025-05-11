document.getElementById("add-star-form").addEventListener("submit", function (event) {
    event.preventDefault(); // remove page reloading func

    const name = document.getElementById("star-name").value;
    const birthYear = document.getElementById("birth-year").value;
    const confirmation = document.getElementById("success");

    fetch("api/add-star", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `name=${encodeURIComponent(name)}&birthYear=${encodeURIComponent(birthYear)}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                confirmation.textContent = `Success! Star ID : ${data.starId}`;
            } else {
                confirmation.textContent = data.error || "Failed to add star.";
                confirmation.style.color = "red";
            }
        })
});
