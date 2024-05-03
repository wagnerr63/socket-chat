package com.chat.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    processMessage(clientMessage, username, clientInput);
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

    private void processMessage(String senderMessage, String sender, PrintStream clientInput) throws IOException {
        String PATTERN = "/send\\s+(message|file)\\s+(\\w+)\\s+(.*)";
    
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(senderMessage);
        matcher.find();

        String command, receiver, message;

        try {
            command = matcher.group(1);
            receiver = matcher.group(2);
            message = matcher.group(3);
        }catch ( Exception e ) {
            sendMessage(clients.get(sender), "Comando inválido");
            return;
        }

        if (Objects.equals(receiver, sender)) {
            sendMessage(clients.get(sender), "Não é possível enviar mensagem para si próprio.");
            return;
        }


        if (!clients.containsKey(receiver)) {
            System.out.println("Destinatário não encontrado: " + receiver);
            sendMessage(clients.get(sender), "Destinatário "+receiver+" não encontrado.");
            return;
        }

        if (command.equals("message")) {
            sendMessage(clients.get(receiver), sender + ": " + message);
        } else if (command.equals("file")) {
            sendFile(clients.get(receiver), message, clientInput);
        }
    }

    private void listClients(PrintStream clientInput) throws IOException {
        clientInput.println("Clientes conectados:");
        for (String client : clients.keySet()) {
            clientInput.println(client);
        }
    }

    private void sendMessage(Socket receiver, String message) throws IOException {
        PrintWriter output = new PrintWriter(receiver.getOutputStream(), true);
        output.println(message);
    }

    private void sendFile(Socket receiver, String message, PrintStream clientInput)
    {
        clientInput.println("/file " + message);

        try
        {
            PrintWriter output = new PrintWriter(receiver.getOutputStream(), true);
            output.println("/receive " + message);

            for (int i = 0; i < 5; i++)
            {
                Thread.sleep(1000);
                if(clientSocket.getInputStream().available() > 0){
                    clientSocket.getInputStream().transferTo(receiver.getOutputStream());
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void logConnection(String adrressIP) throws IOException {
        var filePath = "logs.txt";
        try{
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(filePath, true);

            writer.write("IP:"+adrressIP+",Date:"+ Instant.now().toString()  +"\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("Erro ao salvar o log: " + adrressIP);
        }

    }
}
