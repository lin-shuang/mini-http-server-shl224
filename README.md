CSE 264 – Web Systems Programming – Fall 2022
Homework Assignment 8 - Mini HTTP Server
Due: Tue November 29th, 2022

Objective
To gain a better understanding of the HTTP protocol and how it might be implemented in a web server.

Description
The assignment is to take the Mini HTTP Server provided and add a couple of features to the server.

**Feature 1: Log file**
All industrial strength web servers keep log files that record each request to which the server responds. For example, here are some sample log entries from the Apache HTTP server:

127.0.0.1 - - [01/May/2012:19:12:57 -0400] "GET /xampp/splash.php HTTP/1.1" 200 1325 "-" "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0"
127.0.0.1 - - [01/May/2012:19:12:57 -0400] "GET /xampp/xampp.css HTTP/1.1" 200 4178 "http://localhost/xampp/splash.php" "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0"
127.0.0.1 - - [01/May/2012:19:12:57 -0400] "GET /xampp/img/blank.gif HTTP/1.1" 200 43 "http://localhost/xampp/splash.php" "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0"

Your task is to create a log file for the Mini HTTP Server:
1. When the server starts up, create a file named access.log in the server root directory (your project folder) – append if the file already exists.
2. Each time a request comes in add an entry to the log file containing the IP address of the client, the first line of the request and the response code and then flush the output stream:

127.0.0.1 - "GET /xampp/splash.php HTTP/1.1" 200

**Feature 2: Header Lines**
There are already a couple of response headers built into the server. You assignment is to add two more.

Additional Header #1: Content-Type header 
Implement the Content-Type response header by looking at the file extension of the resource (file) being requested and setting the appropriate content type. For example, if the resource being requested is flower.png, then you should add the following header to the list of headers:
Content-Type: image/png
A list of content types (otherwise known as internet media types or MIME types) can be found here:
 http://en.wikipedia.org/wiki/MIME_type
Implement this header for the following file types:
    • Images in gif, jpeg, or png format (.gif, .jpg, .png extension)
    • Pdf file (.pdf extension)
    • Excel spreadsheet (.xls or .xlsx extension)
    • HTML file (.htm or .html extension)
If the resource has none of the above extensions, then don't include a Content-Type header.

Additional Header #2: Last-Modified header 
Implement the Last-Modified header by looking up the modified date of the requested file and adding the Last-Modified header (with the date formatted in "HTTP-date" format!). For example:

Last-Modified: Tue, 15 Nov 1994 12:45:26 GMT 

You can use the java.io.File class to determine the last modified date of the file.
You can use the java.text.SimpleDateFormat class to format the date.

Use the following as a reference for HTTP headers:
http://en.wikipedia.org/wiki/List_of_HTTP_header_fields

Procedure
1. Accept the repo from gihub classroom.
2. Keep all the files where they are (don't add any folders).
3. Edit Main.java in VSC and add the changes.
4. Fill in the comment at the top of the Main.java file with your name, etc.
5. To compile and run, from the command line:
javac Main.java
java Main
6. Fire up your browser and type localhost:8567
7. Test the links in the loaded page.
8. Check that the log file is being created properly.
9. Commit with comment and Push.
10. Get to work on the Final Project.



