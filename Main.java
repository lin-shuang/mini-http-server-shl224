/* CSE 264 - Fall 2022
 * Homework #8 - Mini HTTP Server
 * Name: Shuang Lin
 * Date: 27 November 2022
 */

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

public class Main {
    // The port can be any small integer that isn't being used by another program
    // See: https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers for a list
    // of already assigned port numbers
    private static final int port = 8567;

    public static void main(String[] args) {
        try {
            System.out.println("Mini HTTP Server Starting Up");
            // Create a new Server Socket to listen on port for a new connection request
            ServerSocket s = new ServerSocket(port);
            for (;;) {
               
                // Wait for a new TCP connection to come in from a client and 
                // accept it when it does. Return a reference to the socket 
                // that will be used to communicate with the client.
                Socket newSocket = s.accept();
                String addressOfConnectingSocket = ((InetSocketAddress)newSocket.getRemoteSocketAddress()).getAddress().getHostAddress();
                System.out.println("New connection from: " + addressOfConnectingSocket);
                
                // Create a new handler object to handle the requests of the 
                // client that just connected.
                ClientHandler handler = new ClientHandler(newSocket);
                
                // Give the handler its own thread to handle requests to that 
                // the server can handle multiple clients simultaneously.
                new Thread(handler).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    // Socket used to handle the client requests.
    private Socket socket;

    public ClientHandler(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            BufferedReader request = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream response = new DataOutputStream(socket.getOutputStream());
            List<String> headers = new ArrayList<>();

            try {
                String firstLine = request.readLine();
                if (firstLine.length() > 0) {
                    // Read Headers, one per line of the request
                    String line;
                    while ((line = request.readLine()).length() > 0) {
                        headers.add(line);
                    }
                    // Break the request line up unto individual token
                    String[] tokens = firstLine.split(" ");
                    
                    // The first token is the method name (GET, POST, etc.)
                    String method = tokens[0];
                    
                    // The second token is the resource being requested (eg. /index.html)
                    String resource = tokens[1];

                    //check if requested resource is a file
                    if(resource.contains(".")){

                        //add Last-Modified header based on requested resource
                        File file = new File("");
                        String currentDirectory = file.getAbsolutePath();
                        file = new File(currentDirectory + resource);

                        if(file.lastModified() > 0){
                            headers.add("Last-Modified: "+httpDate(file.lastModified()));
                        }

                        //add Content-Type header based on requested resource
                        tokens = resource.split("\\.");
                        String contentType = tokens[1]; //file extension
                        switch (contentType) {
                            case "gif":
                                contentType = "image/gif";
                                break;
                            case "jpg":
                                contentType = "image/jpeg";
                                break;
                            case "jpeg":
                                contentType = "image/jpeg";
                                break;
                            case "png":
                                contentType = "image/png";
                                break;
                            case "pdf":
                                contentType = "application/pdf";
                                break;
                            case "xls":
                                contentType = "application/vnd.ms-excel";
                                break;
                            case "xlsx":
                                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                                break;
                            case "htm":
                                contentType = "text/html";
                                break;
                            case "html":
                                contentType = "text/html";
                                break;
                            default:
                                contentType = "";
                                break;
                        }
                        if(contentType.length() > 0){
                            headers.add("Content-Type: "+contentType);
                        }
                    }
                    
                    // Dump the entire request to the console for debugging
                    dumpRequest(firstLine, headers);
                    
                    // Process the request based on the method used (GET is the only
                    // one we're implementing for now
                    int responseCode = 0;
                    switch (method) {
                        case "GET":
                            responseCode = processGET(resource, headers, response);
                            
                            //log requests into access.log file
                            logAccess(((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress(), 
                                        firstLine, responseCode);
                            break;
                        case "POST":
                            System.err.println(method + " method not implemented.");
                            break;
                        case "HEAD":
                            System.err.println(method + " method not implemented.");
                            break;
                        case "PUT":
                            System.err.println(method + " method not implemented.");
                            break;
                        case "DELETE":
                            System.err.println(method + " method not implemented.");
                            break;
                        case "TRACE":
                            System.err.println(method + " method not implemented.");
                            break;
                        case "OPTIONS":
                            System.err.println(method + " method not implemented.");
                            break;
                        default:
                            System.err.println("Unknown method: " + method);
                            break;
                    }
                }
            } catch (Exception e) {
               // If we get an i/o error, tell the user that the resource is unavailable
               response.writeBytes("HTTP/1.1 404 ERROR\n\n");
            }
            // Clean up once the request has been processed
            request.close();
            response.close();
        } catch (Exception ex1) {
            System.out.println("Internal error: " + ex1.getMessage());
        }
    }

    // Write out the request header lines to the console
    private void dumpRequest(String firstLine, List<String> headers) {
        System.out.println(firstLine);
        for (String headerLine : headers) {
            System.out.println(headerLine);
        }
        System.out.println();
    }

    private int processGET(String resource, List<String> headers, DataOutputStream out) {
        try {

            // Default to index.html
            if (resource.endsWith("/")) {
                resource += "index.html";
            }

            // Create file path from requested resource compatable with the host OS
            String path = ("." + resource).replace('/', File.separatorChar);
            int length = (int) new File(path).length();
            byte[] b = new byte[length];

            // Read the requested resource into an array of bytes
            FileInputStream resourceStream;
            try {
                resourceStream = new FileInputStream(path);
                resourceStream.read(b);
            } catch (IOException ex) {
                out.writeBytes("HTTP/1.1 404 ERROR\n\n");
                return 404;
            }

            // Write HTTP response line to client
            out.writeBytes("HTTP/1.1 200 OK\n");
            
            // Write out the headers
            out.writeBytes("Content-Length:" + length + "\n");
            out.writeBytes("Connection: close\n");
            
            // Blank line ends the header section
            out.writeBytes("\n"); 
            
            // Send the requested resource to the client
            out.write(b, 0, length);
            
            // Return code 200 means "Successful"
            return 200;
        } catch (IOException ex) {
            try {
                out.writeBytes("HTTP/1.1 500 ERROR\n\n");
                return 500;
            } catch (IOException ex1) {
                System.out.println("Internal error: " + ex1.getMessage());
                return 500;
            }
        }
    }

    public void logAccess(InetAddress address, String firstLine, int responseCode){
        //create log file
        try {
            File logFile = new File("access.log");
            FileWriter logWrite = new FileWriter(logFile.getName(), true);
            logWrite.write(address+" - ");
            logWrite.write('"'+firstLine+'"');
            logWrite.write(" "+responseCode+"\n");
            logWrite.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String httpDate(long dateMS) {

        SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        httpDate.setTimeZone(TimeZone.getTimeZone("GMT") );

        return httpDate.format(dateMS);
    }
}