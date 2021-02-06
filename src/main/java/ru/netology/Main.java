package ru.netology;

public class Main {

    public static void main(String[] args) {

        int port = 9999;
        int numberOfThreads = 64;

        Server server = new Server(port, numberOfThreads);

        server.listen();

    }
}




