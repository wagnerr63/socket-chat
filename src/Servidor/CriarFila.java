package Servidor;

import Util.Util;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.Scanner;

public class CriarFila extends Thread{

    private Queue<ServerSocket> filaExecucao;
    private int novaPorta;
    private Socket socket;

    public CriarFila(Queue<ServerSocket> filaExecucao, int novaPorta, Socket socket){
        this.filaExecucao = filaExecucao;
        this.novaPorta = novaPorta;
        this.socket = socket;
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNextLine()){
                String nextLine = scanner.nextLine();
                if (nextLine.equals("Nova porta")){
                    //Gravar Porta Destinada ao Cliente
                    String informacao = "IP CLIENTE" + socket.getRemoteSocketAddress() + " | PORTA PADRÃO: " + Util.portaInicial + " | PORTA CLIENTE: " + novaPorta;
                    Path caminhoArquivo = Paths.get(System.getProperty("user.dir") + "\\src\\Servidor\\arquivoPortas.txt");
                    GravarPortaCliente.gravarPortaCliente(caminhoArquivo, informacao);
                    // Criar novo servidor;
                    ServerSocket serverComunicacao = new ServerSocket(novaPorta);
                    // Incrementar fila
                    filaExecucao.add(serverComunicacao);
                    // Informar nova porta
                    PrintStream stream = new PrintStream(socket.getOutputStream());
                    stream.println("Nova porta: " + novaPorta);
                    Thread.sleep(100);
                    stream.close();
                    socket.close();
                }
            }
        }catch (IOException | InterruptedException ignore){}
    }
}
