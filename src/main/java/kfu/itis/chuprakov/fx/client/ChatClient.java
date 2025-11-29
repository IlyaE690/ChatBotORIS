package kfu.itis.chuprakov.fx.client;

import kfu.itis.chuprakov.fx.ChatApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private ChatApplication chatApplication;
    private Socket socket;
    private ClientThread clientThread;

    public ChatClient(ChatApplication chatApplication) {
        this.chatApplication = chatApplication;
    }

    public void send(String message) {
        clientThread.out.println(message);
    }

    public void start() {
        String host = chatApplication.userConfig.host();
        int port = chatApplication.userConfig.port();
        try {
            socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            clientThread = new ClientThread(this, in, out);
            new Thread(clientThread).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientThread implements Runnable {
        private ChatClient chatClient;
        private BufferedReader in;
        private PrintWriter out;

        public ClientThread(ChatClient chatClient, BufferedReader in, PrintWriter out) {
            this.chatClient = chatClient;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = in.readLine();
                    chatClient.chatApplication.appendMessage(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}