package com.chat.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server extends Thread {

    private Socket clientSocket;

    private static Map<String, Socket> clients = new HashMap<>();

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Servidor iniciado. \nAguardando conexões.\n");

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                logConnection(clientSocket.getInetAddress().getHostAddress());

                new Server(clientSocket).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    public void run() {
        try {
            Scanner clientOutput = new Scanner(clientSocket.getInputStream());
            PrintStream clientInput = new PrintStream(clientSocket.getOutputStream());

            String username = clientOutput.nextLine();
            clients.put(username, clientSocket);
            System.out.println("Cliente adicionado: " + username);

            while (true) {
                String clientMessage = clientOutput.nextLine();
                if (clientMessage.startsWith("/send")) {
                    processMessage(clientMessage);
                } else if (clientMessage.equals("/users")) {
                    listClients(clientInput);
                } else if (clientMessage.equals("/sair")) {
                    clients.remove(username);
                    clientSocket.close();
                    System.out.println("Cliente desconectado: " + username);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String senderMessage) throws IOException {
        String[] parts = senderMessage.split(" ");
        String command = parts[0];
        String receiver = parts[2];

    }

    private void listClients(PrintStream clientInput) throws IOException {
        clientInput.println("Clientes conectados:");
        for (String client : clients.keySet()) {
            clientInput.println(client);
        }
    }

    private void sendMessage(Socket receiver, String message) throws IOException {
        
    }

    private void sendFile(Socket destinatario, String caminhoArquivo) throws IOException {
        
    }

    private static void logConnection(String adrressIP) throws IOException {
        
    }
}
