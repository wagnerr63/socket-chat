package Servidor;

import Util.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(Util.portaInicial);
        System.out.println("Aguardando conexão");

        Integer portaCliente = Util.portaInicial;

        Queue<ServerSocket> filaExecucao = new LinkedList<>();
        // consumir fila
        ExecutarFila executarFila = new ExecutarFila(filaExecucao);
        executarFila.start();

        while (true) {
            Socket socket = server.accept();
            // Gerar nova porta;
            portaCliente++;
            CriarFila criarFila = new CriarFila(filaExecucao, portaCliente, socket);
            criarFila.start();
        }
    }
}
