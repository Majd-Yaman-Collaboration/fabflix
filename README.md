## 📋 Overview

Fabflix is a full-stack movie web application developed throughout the CS122B course at UC Irvine. The entire class centered on incrementally building out its features, including movie browsing, search, authentication, and star detail pages. Users can search for movies using full-text queries, browse paginated results, view detailed information about individual stars and movies, and log in securely as either customers or employees. The site uses AJAX to communicate with REST-style servlet endpoints and features a scalable backend deployed using a multi-service, containerized architecture running on Kubernetes.

---

## ✨ Features

- **Login System**  
  Customers and employees can log in securely. Sessions are managed using JWT stored in cookies.

- **Movie Search with Filters**  
  Search by movie title, director, year, and star name using full-text and wildcard queries.

- **Autocomplete Search Bar**  
  While typing in the search bar, AJAX requests return matching titles in real time.
  
- **Genre and Character Browsing**  
  Click on a genre to view a list of movies that belong to it. You can also browse movies alphabetically by clicking on letters A–Z or `*` for titles that don't start with a letter.

- **Movie Details Page**  
  Click on a movie to view its year, director, genre(s), rating, and the starring actors.

- **Star Pages**  
  Click on a star’s name to view all movies they’ve appeared in.

- **Employee Dashboard**  
  Employees can log in to access admin tools such as adding movies, stars, and genres using form submissions.

- **Scalable Architecture**  
  Login and movie services run in isolated containers with Kubernetes for scaling and routing.

---

## 🖼️ General Use

### 🔐 Login Page  
<img width="1920" height="1170" alt="login" src="https://github.com/user-attachments/assets/79d30aa1-7cf8-4974-8fb3-5eb1f77d069c" />

### 🔎 Search and Filter Movies  
<img width="1920" height="1170" alt="main_page" src="https://github.com/user-attachments/assets/5fb20a1d-ee2e-4c40-a87a-062e019f5702" />

### 🎬 View Movie List  
<img width="1920" height="1170" alt="movie_list" src="https://github.com/user-attachments/assets/db56c266-7b96-4204-a095-cd5716eb72b3" />

### 🛒 View Cart
<img width="1920" height="1170" alt="shopping_cart" src="https://github.com/user-attachments/assets/721402d1-a828-4ffb-a35a-b38d5722743a" />

### ⚙️ Employee Tools  
<img width="1920" height="1170" alt="add_star" src="https://github.com/user-attachments/assets/7a3d7033-75c4-4192-9093-589c9e7b94f7" />

---

## 🛠️ Technologies Used

- **Frontend**:  
  HTML, CSS, JavaScript, and AJAX used to build a responsive UI and fetch data from the backend without reloading pages.

- **Backend**:  
  Java Servlets exposing REST-style endpoints to handle logic for login, search, star details, and admin tools.

- **Build Tool**:  
  Maven is used for managing dependencies and building the WAR packages for deployment.

- **XML Parsing**:  
  SAX parser is used to efficiently read large XML files and load initial data (movies, genres, stars) into the database.

- **Database**:  
  MySQL with:
  - **Master-Slave Replication** for separating read and write operations to improve scalability.
  - **Prepared Statements** to prevent SQL injection and improve query efficiency.
  - **Batch Inserts** to load large amounts of data (e.g. from XML) faster by reducing round trips.
  - **Stored Procedures** for inserting new movies and stars through the employee dashboard.
  - **Indexes and FULLTEXT Indexing** on relevant columns to speed up search and filtering.
  - **Connection Pooling** via Tomcat JDBC for better handling of concurrent queries.

- **Architecture**:  
  Dockerized microservices with clear separation:
  - `fabflix-login` (authentication)
  - `fabflix-star` (search and star-related functionality)

- **Deployment**:  
  Kubernetes (K8s) for container orchestration on AWS EC2 instances, with Ingress Controller and Load Balancer for routing and scalability.

- **Security**:  
  - Passwords encryption
  - JWT stored in cookies for session management  
  - Servlet filters used to restrict access to authenticated users based on their role (customer and employee)
