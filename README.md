# CS 122B Project 1 

### Both of us worked on it always at the same time. We had a lot of trouble and had to keep restarting due to random dependency issues so the work was very split. Majd worked on the html and servlets while Yaman started off the servlets because Majd was having a lot of trouble with set up in the beginning. Since Majd did work a little bit more on the actual coding and searching up how to use all these new tools when there were hundreds of errors we were getting, Yaman focused on the AWS instance making sure that the instance ran properly and connected to our local files. We both sat together and worked at the same time so we saw the work and process of each others' tasks. We finally added some small notes to the body tag to add a little valentines theme to the website. We both agree that we equally contributed to this project. Hope you enjoy the final product!

# CS 122B Project 2

### Pattern matching used: %ABC%

### js was 80% majd. java was 80% me. html was 50 50. css was 90% majd. 

# CS 122B Project 3

### Yaman did all of task 6 and Task 1. Majd did all of Task 4. Majd did all of task 5 except the Employee Login Page. Task 3 was worked on before so really both of us. Task 2 was both of us too.

## prepared statements in files:
- CheckoutSerlvet
- ConfirmationSerlvet
- LoginInterface
- casts124
- mains243
- star.MovieListServlet
- star.SingleStarServlet
- star.SingleMovieServlet

Optimizations:
- using .addBatch()
- using a % to batch only 500 hundered at a time -> using less massive chunks of memory
- Added an index for title and director since they were sort of the "primary key" used to identify a movie from the useful data in casts124.
- Turned off foreign_key_checks when inserting because I knew it was safe to do so.

The inconsistencies file in repo: https://github.com/uci-jherold2-2025spring-cs122b/2025-spring-cs-122b-ym/blob/project3/inconsitencies

VIDEO:
https://www.dropbox.com/scl/fi/7qn75y9s4y05j0sob22cz/CS122b-Project-3-Video-Demo.mp4?rlkey=wusmmjoi3srzv07a50tx4ndif&e=1&st=0w98f161&dl=0


# CS 122B Project 4

Majd did most of the full text search and fuzzy search. Yaman just fixed some bugs, so the enter button works properly. Yaman did task 2 and 3. Majd and Yaman did task 4. 

## Connection pooling

- src/WebContent/META-INF/context.xml <-- where the pooling is configured
- src/common.Dashboard/AddMovieServlet
- src/Dashobard/AddStarServlet
- src/star.Checkout/CheckoutServlet
- src/star.Checkout/ConfirmationSerlvet
- src/common.Dashboard/DashboardServlet
- src/common.Dashboard/LoginDashboardServlet
- src/login/LoginServlet
- src/star.MainPageServlet
- src/star.MovieListServlet
- src/SearchServlet
- src/star.Checkout/ShoppingCartServlet
- src/star.SingleMovieServlet
- src/star.SingleStarServlet
- src/supers/BaseServlet

Explanation: the connection pooling keeps the TCP open, so it stays in the pool of connections instead of constantly creating and closing connections constantly which can be very inefficient. The main creation of the datascource will be found in the Super class BaseServlet which all the other servlets inherit. where we say dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb"); which instead of just giving us the ability to open one connection allows to get the whole pool of connections.

Connection pooling with two backend SQL:
- The Master and Slaves instances have their own connection pools. Writes only go to the master pool, while reads reach both pools. This causes the number of reads to practically double. So basically, SELECT type statements will go to both instances while INSERT type statements would only go to the Master Instance, then go back to the master pool because of its write property.


## Master and Slave
- src/WebContent/META-INF/context.xml
- src/star.Checkout/ConfirmationServlet
- src/supers/BaseServlet

## How read/write requests were routed to Master/Slave SQL?
- The main instance uses a load balancer to distribute incoming requests between the backend servers. Read requests are handled locally by each backend instance using their own localhost database connection. Meanwhile, all write operations are directed to the master SQL instance using its private IP address.
 
Demo video: https://www.dropbox.com/scl/fi/ebfw3fhbjyii92e3e8xa8/Project4DemoMajdAndYaman.mp4?rlkey=1l655vst02ommfcvuds54l0n1&st=oeq2cr9h&dl=0

# Project 5
https://www.dropbox.com/scl/fi/gfz17x1v3w7pj5qxjptu7/project5.mkv?rlkey=pqtfcz8ti2k6bjncqz2al6c2w&st=ue8etm43&dl=0
The jwt from the task 4 part at the start is there because the link is the same, so when I was testing task 4 before, I simply forgot to delete the cookie, and it wasn't long enough for the cookie to expire. I did have another very similar run where I didn't forget to delete it and when I opened up the application part, there was no jwt just as you want. I just don't want to re-demo all of it just because of that, so please just take my word for it lol. Thank you.
