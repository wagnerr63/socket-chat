package com.chat.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final String SERVER = "localhost";
        final int SERVER_PORT = 12345;

        try {
            Socket socket = new Socket(SERVER, SERVER_PORT);
            System.out.println("Conectado ao servidor");

            Scanner input = new Scanner(socket.getInputStream());
            PrintStream output = new PrintStream(socket.getOutputStream());
            Scanner keyboard = new Scanner(System.in);

            String userName = getUserName(keyboard);

            output.println(userName);

            Thread threadReceiver = new Thread(() -> receiveMessages(input));
            threadReceiver.start();

            while (true) {
                String message = keyboard.nextLine();

                output.println(message);

                if (message.equals("/sair")) {
                    break;
                }                
            }

            input.close();
            output.close();
            keyboard.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUserName(Scanner keyboard) throws IOException {
        System.out.print("Digite seu nome de usuário: ");
        return keyboard.nextLine();
    }

    private static void receiveMessages(Scanner input) {
        while (input.hasNextLine()) {
            System.out.println(input.nextLine());
        }
    }

    private static void sendFile(PrintStream output, String receiver, String filePath) throws IOException {
       
    }

    private static void receiveFile(String message) throws IOException {
       
    }
}
