package languagetrainer;

public class LanguageServer {

    static public boolean server_mode;

    public static void main(String[] args) {
       
        int server_port = 0;

        if (args.length == 0) {
            
            server_mode = false;
            LanguageTrainer.run();
            
        } else if (args[0].equals("server")) {

            server_mode = true;

            for (int arg = 2; arg <= args.length; arg++) {
                String[] s = args[arg - 1].split("=");
                if (s[0].equals("port")) {
                    server_port = Integer.parseInt(s[1]);
                }
            }

            if (server_port == 0) {
                System.out.println("Usage : Tatoeba7.jar server port=4567");
            } else {
                new LanguageTcpServer(server_port).start();
            }
        }
    }
}
