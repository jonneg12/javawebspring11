package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Server {

    private int port;
    final List<String> validPaths = Arrays.asList("/index.html", "/spring.svg", "/spring.png");
    final String filesPath = "files";


    public Server(int port) {
        this.port = port;
    }


    public ServerSocket getServerSocket() throws IOException {
        return new ServerSocket(port);
    }


    public void requestProcessing(ServerSocket serverSocket) throws IOException {
        System.out.println("start processing " + Thread.currentThread().getName());
        try (
                final Socket socket = serverSocket.accept();
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            final String requestLine = in.readLine();
            final String[] parts = requestLine.split(" ");

            System.out.println("continue processing " + Thread.currentThread().getName());

            // There must be definitely 3 parts
            if (parts.length != 3) {
                //just close socket
                return;
            }

            final String path = parts[1];
            System.out.println(path);

            if (!validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                System.out.println(404);
                return;
            }

            final Path filePath = Paths.get(filesPath + path);
            final String mimeType = Files.probeContentType(filePath);
            final long length = Files.size(filePath);

            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content type : " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
