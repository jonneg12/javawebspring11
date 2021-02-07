package ru.netology;


import ru.netology.server.Server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        int port = 9999;
        int threadPoolSize = 64;

        Server server = new Server(threadPoolSize);

        server.addHandler("GET", "/classic.html", (request, out) -> {
            try {
                final Path filePath = Paths.get("files" + request.getPath());
                final String mimeType = Files.probeContentType(filePath);
                final String template = Files.readString(filePath);
                final byte[] content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type : " + mimeType + "\r\n" +
                                "Content-Length : " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

    });
        server.listen(port);
}
}




