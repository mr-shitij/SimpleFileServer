package handler;

import core.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    protected Socket socket;
    public ClientHandler(Socket clientSocket) {
        System.out.println("New Client connected");
        this.socket = clientSocket;
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Request req = (Request) objectInputStream.readObject();
                RequestFullFiller.processRequest(req, this);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Client Disconnected ..!!!");
    }
}
