package ru.netology;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App
{
    public static void main( String[] args ) throws IOException {
        int port = 9999;
        int numberOfThreads = 64;
        

        final ExecutorService threadPool = Executors.newFixedThreadPool(numberOfThreads);

        Server server = new Server(port);
        final ServerSocket serverSocket = server.getServerSocket();

        for (int i = 0; i < 1000; i++) {
            threadPool.submit(() -> {
                try {
                    server.requestProcessing(serverSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
           });
        }
    }
}
