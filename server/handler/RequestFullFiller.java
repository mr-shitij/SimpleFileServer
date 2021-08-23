package handler;

import manual.ServerManual;
import core.Request;
import core.Response;

import java.io.*;

public class RequestFullFiller {
    public static void processRequest(Request req, ClientHandler clientHandler) throws IOException, ClassNotFoundException {

        if((req.getFolder() == null || req.getFile() == null) || (req.getFolder().contains("../") || req.getFile().contains("../"))){
            OutputStream outToClient = clientHandler.socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToClient);
            out.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_ERROR, "Dont be too smart OK ..!!", null));
            out.flush();
            return;
        }

        if(req.getAction().equals("manual")){
            OutputStream outToClient = clientHandler.socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToClient);


            String manual = ServerManual.template + ServerManual.getManual("", System.getProperty("user.home") + "/" + "ShareAble", 1);

            out.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_GET_RESULT, manual, null));

            out.flush();

        } else if(req.getAction().equals("get")){
            OutputStream outToClient = clientHandler.socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToClient);
            File file = new File(System.getProperty("user.home") + "/" + "ShareAble/" + req.getFolder() + "/" + req.getFile());
            if(file.isFile() && file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                int i;
                do {
                    byte[] buf = new byte[1024];
                    i = fileInputStream.read(buf);
                    out.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_GET_RESULT, null, buf));

                } while (i != -1);

                out.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_GET_END, null, null));
                out.flush();
                fileInputStream.close();

            } else {
                out.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_GET_ERROR, "file Does not exist", null));
                out.flush();
            }

        } else if(req.getAction().equals("post")) {

            ObjectInputStream inObj = new ObjectInputStream(clientHandler.socket.getInputStream());
            Response response = (Response) inObj.readObject();

            if(response.getResponseCode() == Response.ResponseCode.RESPONSE_CODE_POST_ERROR) {
                System.out.println("Unable To Get File From Client : " + response.getMessage());
            }

            File file = new File(System.getProperty("user.home") + "/" + "ShareAble/" + "FromClientAdded/" + req.getFile());
            OutputStream os = new FileOutputStream(file);
            os.write(response.getDataBuffer());

            while (true) {
                if(clientHandler.socket.isClosed()) {
                    os.close();
                    file.delete();
                    break;
                }
                response = (Response) inObj.readObject();
                if(response.getResponseCode() == Response.ResponseCode.RESPONSE_CODE_POST_END) {
                    os.close();
                    break;
                }
                os.write(response.getDataBuffer());
            }

        } else {
            OutputStream outToClient = clientHandler.socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outToClient);
            out.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_ERROR, "file Does not exist", null));
            out.flush();

        }
    }
}
