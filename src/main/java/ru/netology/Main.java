package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {

        int port = 9999;
        int numberOfThreads = 64;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        Server server = new Server(port, numberOfThreads);

        ServerSocket serverSocket = server.getServerSocket();

        while (true){
            try {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> server.connectionExecutor(socket));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}



