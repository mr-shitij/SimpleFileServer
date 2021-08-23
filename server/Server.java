import handler.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    static int PORT = 1978;
    public static void main(String[] args) {

        if(args.length == 0 || args.length > 1) {
            System.out.println("to use specific port for server specify it correctly ..!!!");
        } else {
            try {
                PORT = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("invalid port number");
            }
        }

        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("server started on port : " + PORT + " ..!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
                new ClientHandler(socket).start();
            } catch (IOException e) {
                System.out.println("i/O error: " + e);
            }
        }
    }

}
