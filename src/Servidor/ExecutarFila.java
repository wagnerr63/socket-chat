package Servidor;

import Util.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Queue;
import java.util.Scanner;

public class ExecutarFila extends Thread{

    private Queue<ServerSocket> filaExecucao;

    public ExecutarFila (Queue<ServerSocket> filaExecucao){
        this.filaExecucao = filaExecucao;
    }

    public void run() {
        while (true){
            ServerSocket serverSocket = filaExecucao.poll();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (serverSocket == null) continue;

            try {
                Socket socket = serverSocket.accept();
                PrintStream stream = new PrintStream(socket.getOutputStream());
                Scanner scanner = new Scanner(socket.getInputStream());
                while (scanner.hasNextLine()){
                    String nextLine = scanner.nextLine();
                    if (nextLine.equals("HTML")){
                        // Gerar enviar arquivo
                        String conteudoHtml = CriarConteudoArquivoHtml.conteudoArquivoHtml(socket.getRemoteSocketAddress().toString());
                        String conteudo = Base64.getEncoder().encodeToString(conteudoHtml.getBytes());
                        String mensagem = String.format("<%s><%s><%s>", "index", "html", conteudo);
                        stream.println(mensagem);

                    }else if (nextLine.equals("IMAGEM")){
                        // Enviar imagem
                        String caminhoArquivo = System.getProperty("user.dir") + "\\src\\Servidor\\imagem.jpg";
                        File file = new File(caminhoArquivo);
                        Path path = Paths.get(file.getAbsolutePath());
                        byte[] data = Files.readAllBytes(path);

                        String conteudo = Base64.getEncoder().encodeToString(data);
                        String mensagem = String.format("<%s><%s><%s>", "imagem", "jpg", conteudo);
                        stream.println(mensagem);
                        break;
                    }
                }

                scanner.close();
                stream.close();
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
