package com.chat.Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    static Scanner input;
    static PrintStream output;
    static DataInputStream inputStream;

    public static void main(String[] args) {
        final String SERVER = "localhost";
        final int SERVER_PORT = 12345;

        try {
            Socket socket = new Socket(SERVER, SERVER_PORT);
            System.out.println("Conectado ao servidor");

            input = new Scanner(socket.getInputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            output = new PrintStream(socket.getOutputStream());
            Scanner keyboard = new Scanner(System.in);

            String userName = getUserName(keyboard);

            output.println(userName);

            Thread threadReceiver = new Thread(Client::receiveMessages);
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
            //e.printStackTrace();
            System.out.println("Erro ao conectar ao servidor. Tente novamente.");
        }
    }

    private static String getUserName(Scanner keyboard) throws IOException {
        System.out.print("Digite seu nome de usuário: ");
        return keyboard.nextLine();
    }

    private static void receiveMessages() {
        while (input.hasNextLine()) {
            String inputMessage = input.nextLine();

            if(inputMessage.startsWith("/file"))
            {
                try
                {
                    sendFile(inputMessage);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }else if(inputMessage.startsWith("/receive")){
                try
                {
                    receiveFile(inputMessage);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else {
                System.out.println(inputMessage);
            }

            System.out.println(input.nextLine());
        }
    }

    private static void sendFile(String filePath) throws IOException {
        filePath = filePath.substring(6);
        File file = new File(filePath);
        output.print(file.length());

        try (InputStream inputStream = new FileInputStream(filePath)){
            byte[] buffer = new byte[4096];
            int read;
            while((read = inputStream.read(buffer)) != -1){
                output.write(buffer, 0, read);
                output.flush();
            }

        } catch (FileNotFoundException e) {
            output.println("Arquivo não encontrado");
        }
    }

    private static void receiveFile(String message) throws IOException {
       int bytes = 0;

        Pattern pattern = Pattern.compile("[^\\\\]*$");
        Matcher matcher = pattern.matcher(message);
        String fileName = "";
        if (matcher.find()) fileName = matcher.group();
        if(fileName.isEmpty()) throw new RuntimeException("Nome do arquivo não encontrado");
        String path = new File(".").getCanonicalPath();

       try(FileOutputStream file = new FileOutputStream(path + "/"  + fileName)){
           long size = inputStream.readLong();

           byte[] buffer = new byte[4096];
           while ((bytes = inputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
               file.write(buffer,0,bytes);
               size -= bytes;
           }
       }catch (IOException e){
           e.printStackTrace();
       }
    }
}
