package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int nextClientId = 1;

    public static void main(String[] args) throws Exception {
        int port = 6666;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serwer uruchomiony na porcie " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, nextClientId++).start();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private int clientId;

        public ClientHandler(Socket socket, int clientId) {
            this.clientSocket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                System.out.println("Nawiązano nowe połączenie z klientem. ID klienta: " + clientId);

                // 1. Serwer wysyła "ready"
                out.writeObject("ready");
                System.out.println("Wysłano 'ready' do klienta o ID: " + clientId);

                // 2. Oczekiwanie na wartość n od klienta
                int n = (int) in.readObject();

                // 3. Serwer wysyła "ready for messages"
                out.writeObject("ready for messages");
                System.out.println("Wysłano 'ready for messages' do klienta o ID: " + clientId);

                // 4. Oczekiwanie na n obiektów Message od klienta
                for (int i = 0; i < n; i++) {
                    Message message = (Message) in.readObject();
                    System.out.println("Odebrano wiadomość od klienta o ID: " + clientId + ": " + message.getContent());
                }

                // 5. Serwer wysyła "finished"
                out.writeObject("finished");
                System.out.println("Wysłano 'finished' do klienta o ID: " + clientId);
                System.out.println("Komunikacja z klientem o ID: " + clientId + " zakończona");
            } catch (Exception e) {
                System.out.println("Wystąpił problem z klientem o ID: " + clientId);
                e.printStackTrace();
            }

        }
    }
}
