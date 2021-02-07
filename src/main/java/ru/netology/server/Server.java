package ru.netology.server;

import ru.netology.server.handler.Handler;
import ru.netology.server.request.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService executorService;

    private Map<String, Map<String, Handler>> handlers;


    private final Handler notFoundHandler = ((request, out) -> {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length : 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
            System.out.println(404);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });


    public Server(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.handlers = new ConcurrentHashMap<>();
    }

    public void addHandler(String method, String path, Handler handler) {
        final Map<String, Handler> pathHandlerMap = handlers.get(method);
        if (pathHandlerMap == null) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }


    public void listen(int port) {


        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                executorService.submit(() -> handleConnection(socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        try (
                socket;
                final InputStream in = socket.getInputStream();
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            {
                Request request = Request.fromInputStream(in);

                Map<String, Handler> pathToHandlerMap = handlers.get(request.getMethod());
                if (pathToHandlerMap == null) {
                    notFoundHandler.handle(request, out);
                    return;
                }

                final Handler handler = pathToHandlerMap.get(request.getPath());
                if (handler == null) {
                    notFoundHandler.handle(request, out);
                    return;
                }

                handler.handle(request, out);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
