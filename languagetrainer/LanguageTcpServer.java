package languagetrainer;

import java.io.*;
import java.net.*;
import languages.LanguageContext;
import languages.Language;
import dictionaries.GenericDictionary;

class LanguageServerThread extends Thread {

    private Socket socket = null;

    public LanguageServerThread(Socket socket) {
        super("LanguageServer Thread");
        this.socket = socket;

    }

    public void run() {

        try (BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());) {

            String inputline;
            inputline = inFromClient.readLine();
//            System.out.println("Server received " + inputline);

            String[] parts = inputline.split("=");
            String lang = parts[0];
            String sentence = parts[1];
            String response; 

            Language language = LanguageContext.get(lang);
            GenericDictionary dictionary = language.dictionary();
            dictionary.dictionaryWindowVisible(false);
            dictionary.setMatchInfo(false);
            dictionary.setMarkCorrection(false);

            response = dictionary.correctWordByDictionary(sentence);

//            System.out.println("Server replies <" + response + ">");
            byte[] ba={};
            try {
                try {
                    ba = response.getBytes("UTF8");
                } catch (Exception e) {
                };
                outToClient.write(ba,0,ba.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class LanguageTcpServer extends Thread {

    private int portNumber;

    public LanguageTcpServer(int portNumber) {
        super();
        this.portNumber = portNumber;
    }

    public void run() {
        System.out.println("Server starts listening on port " + portNumber);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                new LanguageServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + portNumber + ". Exiting...");
            System.exit(-1);
        }
    }
}
