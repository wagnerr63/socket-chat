package Servidor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GravarPortaCliente {

    public static void gravarPortaCliente(Path caminhoArquivo, String informacao){
        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(informacao);
            writer.newLine();
            System.out.println("Porta destina ao cliente gravada com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo: " + e.getMessage());
        }
    }
}
