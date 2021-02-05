package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Server {

    private int port;
    private int numberOfThreads;

    private final List<String> validPaths = Arrays.asList("/index.html", "/spring.svg", "/spring.png");
    private final String filesPath = "files";

    public Server(int port, int numberOfThreads) {
        this.port = port;
        this.numberOfThreads = numberOfThreads;
    }

    public ServerSocket getServerSocket() throws IOException {
        return new ServerSocket(port);
    }

    public void connectionExecutor(Socket socket) {
        try (
                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream())) {

            final String requestLine = reader.readLine();
            final String[] parts = requestLine.split(" ");

            System.out.println("continue processing " + Thread.currentThread().getName());

            //проверка правильности запроса
            if (parts.length != 3) {
                return;
            }

            final String path = parts[1];
            System.out.println(path);
            if (!validPaths.contains(path)) {
                writer.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                writer.flush();
                System.out.println(404 + " not found");
                return;
            }

            final Path filePath = Paths.get(filesPath + path);
            final String mimeType = Files.probeContentType(filePath);
            final long length = Files.size(filePath);

            writer.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content type : " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, writer);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

