package com.chat.Client;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    static String userName;

    public static void main(String[] args) {
        final String SERVER = "localhost";
        final int SERVER_PORT = 12345;

        try {
            Socket socket = new Socket(SERVER, SERVER_PORT);
            System.out.println("Conectado ao servidor");

            Scanner input = new Scanner(socket.getInputStream());
            PrintStream output = new PrintStream(socket.getOutputStream());
            Scanner keyboard = new Scanner(System.in);

            userName = getUserName(keyboard);

            output.println(userName);

            Thread threadReceiver = new Thread(() -> receiveMessages(input));
            threadReceiver.start();

            while (true) {
                String message = keyboard.nextLine();

                if (message.startsWith("/send file")) {
                    String[] parts = message.split(" ");
                    String filePath = parts[3];
                    String receiver = parts[2];
                    sendFile(output, receiver, filePath);
                }
                else{
                    output.println(message);
                }

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
            try{
                String message = input.nextLine();
                if(message.contains("><")){
                    receiveFile(message);
                }
                else{
                    System.out.println(message);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void sendFile(PrintStream output, String receiver, String filePath) throws IOException {
        File file = new File(filePath.replace("\\", "/"));
        if (!file.exists()) {
            System.out.println("Arquivo não encontrado.");
            return;
        }
        Path path = Paths.get(file.getAbsolutePath());
        byte[] content = Files.readAllBytes(path);
        String fileContentBase64 = Base64.getEncoder().encodeToString(content);
        String fileName = file.getName();

        String fileExtension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = fileName.substring(lastDotIndex + 1);
        }

        // Obtendo o nome do arquivo sem a extensão
        String fileNameWithoutExtension = fileName;
        if (!fileExtension.isEmpty()) {
            fileNameWithoutExtension = fileName.substring(0, lastDotIndex);
        }
        String fileString = String.format("<%s><%s><%s>", fileNameWithoutExtension, fileExtension, fileContentBase64);

        output.println("/send file "+receiver+  " " + fileString);
    }

    private static void receiveFile(String message) throws IOException {
        String[] partes = message.split("><");//<nome><extensao><conteudo>

        String nomeArquivo = partes[0].replace("<", "");
        String extensao = partes[1];
        String conteudo = partes[2].replace(">", "");

        File file = criarArquivo(nomeArquivo, extensao);

        byte[] data = Base64.getDecoder().decode(conteudo);
        Files.write(Paths.get(file.getAbsolutePath()),data);
    }

    private static File criarArquivo(String nomeArquivo, String extensao) throws IOException {
        String nomeArq = String.format("%s.%s", nomeArquivo, extensao );
        String dirName = (System.getProperty("user.dir") + "/src/"+userName+"_files/").replace("\\", "/");
        String fileFullName = dirName + "/" + nomeArq;

        File dir = new File(dirName);       
        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(fileFullName);
        if(!file.exists()){
            file = new File(fileFullName);
        }
        return file;
    }
    
}
