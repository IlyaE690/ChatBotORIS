package kfu.itis.chuprakov.fx.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private ServerSocket serverSocket;

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

    private void send(String message, Client client) {

        client.out.println(message);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(5555);
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                Client client = new Client(in, out, this);
                new Thread(client).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Client implements Runnable {

        private BufferedReader in;
        private PrintWriter out;
        private ChatServer chatServer;

        public Client(BufferedReader in, PrintWriter out, ChatServer chatServer) {
            this.in = in;
            this.out = out;
            this.chatServer = chatServer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    chatServer.send(line, this);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}