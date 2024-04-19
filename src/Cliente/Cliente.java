package Cliente;

import Util.Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class Cliente {
    public static void main(final String args[]) throws IOException{
        Socket socket = new Socket(Util.ipServidor, Util.portaInicial);
        Integer novaPorta = null;

        PrintStream stream = new PrintStream(socket.getOutputStream());
        stream.println("Nova porta");


        while (novaPorta == null){
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNextLine()){

                String nextLine = scanner.nextLine();
                if (nextLine.contains("Nova porta:")){ // Nova porta:15501
                    nextLine = nextLine.replace("Nova porta:", "");
                    novaPorta = Integer.parseInt(nextLine.trim());
                    break;
                }
            }
            scanner.close();
        }

        stream.close();
        socket.close();

        socket = new Socket(Util.ipServidor, novaPorta);
        Scanner scanner = new Scanner(socket.getInputStream());
        stream = new PrintStream(socket.getOutputStream());
        stream.println("HTML");
        while (scanner.hasNextLine()){
            String nextLine = scanner.nextLine();
            if (nextLine.toUpperCase().contains("HTML")){
                File file = new File("index.html");
                if (file.exists()){
                    file.delete();
                }
                lerMensagemCriarArquivo(nextLine);
                stream.println("IMAGEM");
            }else if (nextLine.toUpperCase().contains("JPG")) {
                File file = new File("imagem.jpg");
                if (file.exists()){
                    file.delete();
                }
                lerMensagemCriarArquivo(nextLine);
                break;
            }
        }

        stream.close();
        scanner.close();
        socket.close();
    }

    private static void lerMensagemCriarArquivo(String nextLine) throws IOException {
        String mensagem = nextLine;
        String[] partes = mensagem.split("><");//<nome><extensao><conteudo>

        String nomeArquivo = partes[0].replace("<", "");
        String extensao = partes[1];
        String conteudo = partes[2].replace(">", "");

        File file = criarArquivo(nomeArquivo, extensao);

        byte[] data = Base64.getDecoder().decode(conteudo);
        Files.write(Paths.get(file.getAbsolutePath()),data);
    }

    private static File criarArquivo(String nomeArquivo, String extensao) throws IOException {
        String nomeArq = String.format("%s.%s", nomeArquivo, extensao );

        File file = new File(nomeArq);
        while (!file.createNewFile()){
            nomeArq = String.format("%s.%s", nomeArquivo, extensao );
            file = new File(nomeArq);
        }
        return file;
    }


}
