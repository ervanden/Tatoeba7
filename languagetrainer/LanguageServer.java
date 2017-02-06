package languagetrainer;

import java.util.ArrayList;

public class LanguageServer {

    public static void main(String[] args) {

        int server_port;

        if (args.length == 0) {
            LanguageTrainer.run();
        } else if (args[0].equals("server")) {

            server_port = 6789;

            for (int arg = 2; arg <= args.length; arg++) {
                String[] s = args[arg - 1].split("=");
                if (s[0].equals("port")) {
                    server_port = Integer.parseInt(s[1]);
                }

            }

            new LanguageTcpServer(server_port).start();
        }
    }
}
