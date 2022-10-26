package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new ServerThread(serverSocket.accept()).start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static class ServerThread extends Thread {
        private final Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        public ServerThread(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                var closed = false;
                while (!closed)
                {
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    var request = in.readUTF();
                    closed=request.equals("close");
                    if (closed) {
                        out.writeUTF("close success");
                        break;
                    }
                    var response = createResponse(request);
                    out.writeUTF(response);

                }
                socket.close();
                in.close();
                out.close();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        private static String createResponse(String request) {
            var type=request.substring(0,2);
            request=request.substring(2);
            return switch (type) {
                case "11" -> reverseString(request);
                case "12" -> request.toUpperCase();
                case "13" -> request.toLowerCase();
                case "14" -> reverseCase(request);
                case "15" -> Integer.toString(countWord(request)) + ", " + Integer.toString(countVowel(request));
                default -> "Invalid request";
            };
        }
        private static String reverseString(String ip) {
            System.out.println("reverseString");
            var rev = new StringBuilder();
            for (int i = ip.length() - 1; i >= 0; i--) {
                rev.append(ip.charAt(i));
            }
            return rev.toString();
        }

        private static String reverseCase(String ip) {
            System.out.println("reverseCase");
            var res = new StringBuilder();
            for (int i = 0; i < ip.length(); i++) {
                res.append(Character.isUpperCase(ip.charAt(i))
                        ? Character.toLowerCase(ip.charAt(i))
                        : Character.toUpperCase(ip.charAt(i))) ;
            }
            return res.toString();
        }

        private static int countWord(String input) {
            System.out.print("countWord");
            if (input == null || input.isEmpty()) {
                return 0;
            }

            String[] words = input.split("\\s+");
            return words.length;
        }

        private static int countVowel(String sentence) {
            System.out.print("countVowel");
            int count = 0;
            for (int i = 0; i < sentence.length(); i++) {
                char ch = sentence.charAt(i);
                if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' || ch == ' ') {
                    count++;
                }
            }
            return count;
        }
    }
}
