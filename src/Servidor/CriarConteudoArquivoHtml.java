package Servidor;

public class CriarConteudoArquivoHtml {

    public static String conteudoArquivoHtml(String ip){
        String conteudo = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Arquivo transferido</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "        <h1>IP:Porta = "+ip+"</h1>\n" +
                "        <img src=\"imagem.jpg\" alt=\"Descrição da Imagem\">"+
                "</body>\n" +
                "</html>";
        return conteudo;
    }

}
