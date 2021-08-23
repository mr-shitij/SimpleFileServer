import core.Request;
import core.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public Socket socket = null;

    public ObjectInputStream inObj = null;
    public ObjectOutputStream outObj = null;

    public Client(String serverName, int port) throws IOException {
        System.out.println("connecting to " + serverName + " on port " + port);
        this.socket = new Socket(serverName, port);
        System.out.println("just connected to " + this.socket.getRemoteSocketAddress());

    }

    public void sendDataRequestToServer(Request req) throws IOException {
        this.outObj = new ObjectOutputStream(socket.getOutputStream());
        this.outObj.writeObject(req);
    }

    public void printServerManual() throws IOException, ClassNotFoundException {
        sendDataRequestToServer(new Request("manual", "", ""));
        this.inObj = new ObjectInputStream(socket.getInputStream());
        Response response = (Response) this.inObj.readObject();
        if(response.getResponseCode() == Response.ResponseCode.RESPONSE_CODE_GET_RESULT) {
            System.out.println(response.getMessage());
        }
    }

    public Request getRequestFromString(String req) throws Exception {
        String[] ele = req.split(" ");
        String[] item = {"", "", ""};

        if(ele.length != 3){
            return null;
        }

        for(int i=0; i < 3; i++){
            for(int j=0; j < ele[i].length() - 1; j++){
                if(ele[i].charAt(j) == '\\' && ele[i].charAt(j + 1) != '\\'){
                    StringBuilder myStr = new StringBuilder(ele[i]);
                    myStr.setCharAt(j, ' ');
                    ele[i] = myStr.toString();
                }
		
		if(ele[i].contains("../")){
			return null;
		}
            }
            item[i]=ele[i];
        }
        return new Request(item[0], item[1], item[2]);
    }

    void getAndSaveDataOfServer(String fileName) throws IOException, ClassNotFoundException {
        this.inObj = new ObjectInputStream(socket.getInputStream());

        Response response = (Response) this.inObj.readObject();
        if(response.getResponseCode() == Response.ResponseCode.RESPONSE_CODE_GET_ERROR){
            System.out.println("unable to get file from server : " + response.getMessage());
            return;
        }

        File file = new File(System.getProperty("user.home")+ "/" + "GetShareAbleFromServer/" + fileName);
        OutputStream os= new FileOutputStream(file);
        os.write(response.getDataBuffer());

        while (true){
            if(socket.isClosed()){
                os.close();
                file.delete();
                break;
            }
            response = (Response) this.inObj.readObject();
            if(response.getResponseCode() == Response.ResponseCode.RESPONSE_CODE_GET_END){
                os.close();
                break;
            }
            os.write(response.getDataBuffer());
        }
        System.out.println("data received ..!!! " + fileName);
    }

    private void sendDataToServer(String fileName) throws IOException {
        File file = new File(System.getProperty("user.home") + "/" + "GetShareAbleFromServer/" + fileName);
        if(file.isFile() && file.exists()){
            outObj = new ObjectOutputStream(socket.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(file);
            int i;
            do {
                byte[] buf = new byte[1024];
                i = fileInputStream.read(buf);
                outObj.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_POST_RESULT, null, buf));

            } while (i != -1);

            outObj.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_POST_END, null, null));
            outObj.flush();
            fileInputStream.close();
            System.out.println("data sent successfully ..!!! " + fileName);

        } else {
            outObj.writeObject(new Response(Response.ResponseCode.RESPONSE_CODE_POST_ERROR, "file Does not exist", null));
            outObj.flush();
            System.out.println("data sent with error ..!!! " + fileName);
        }
    }

    public static void main(String[] args) {
        String serverName;
        int port = 1978;

        if(args.length == 0 || args.length > 2) {
            System.out.println("specify ip address and port properly to connect ..!!!");
            return;
        } else {
            try {
                serverName = args[0];
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("invalid port number");
                return;
            }
        }


        try{
            Client client = new Client(serverName, port);
            client.printServerManual();
            while(true) {
                System.out.print(":> ");
                Scanner sc = new Scanner(System.in);
                String inp = sc.nextLine();
                if(inp.equals("exit")){
                    client.socket.close();
                    System.exit(-1);
                }
                Request request = client.getRequestFromString(inp);
                if(request == null){
                    System.out.println("unable To Process Your Request ...!!!");
                } else if (request.getAction().equals("get")) {
                    client.sendDataRequestToServer(request);
                    client.getAndSaveDataOfServer(request.getFile());
                } else if (request.getAction().equals("post")) {
                    File file = new File(System.getProperty("user.home") + "/" + "GetShareAbleFromServer/" + request.getFile());
                    if(file.isFile() && file.exists()) {
                        client.sendDataRequestToServer(request);
                        client.sendDataToServer(request.getFile());
                    } else {
                        System.out.println("file : " + request.getFile() + " does not exist in GetShareAbleFromServer folder ...!!");
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("unable to connect to server.");
        } catch (Exception e) {
            System.out.println("invalid input.");
            e.printStackTrace();
        }
    }
}
