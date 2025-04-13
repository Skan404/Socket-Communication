package org.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 6666;
        Scanner scanner = new Scanner(System.in);

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // 1. Odbieranie "ready" od serwera
            String serverResponse = (String) in.readObject();
            if ("ready".equals(serverResponse)) {
                System.out.println("Otrzymano ready od serwera");
            }



            // 2. Wysyłanie wartości n do serwera
            System.out.print("Podaj wartość n: ");
            int n = scanner.nextInt();
            out.writeObject(n);

            // 3. Odbieranie "ready for messages" od serwera
            serverResponse = (String) in.readObject();
            if ("ready for messages".equals(serverResponse)) {
                System.out.println("Otrzymano ready for messages od serwera");
            }

            Thread.sleep(10000);
            // 4. Wysyłanie n obiektów Message do serwera
            for (int i = 0; i < n; i++) {
                out.writeObject(new Message(i, "Message " + i));
            }

            // 5. Odbieranie "finished" od serwera
            serverResponse = (String) in.readObject();
            if ("finished".equals(serverResponse)) {
                System.out.println("Otrzymano finished od serwera");
            }

            System.out.println("Komunikacja z serwerem zakończona");
        }
    }
}
