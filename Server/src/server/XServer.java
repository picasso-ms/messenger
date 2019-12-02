package server;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;


class Server extends Thread
{

    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;
    private String login;
    List<String> s;


    public Server(Socket socket, List<String> s) throws IOException
    {

        this.s = s;
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter( new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true );

        login = in.readLine();
        Platform.runLater(() -> {
                    this.s.add(login + " is conected");
        });


        start();
    }




    @Override
    public void run() {
        String word;
        String message;
        String receiver;
        String sender;
        String delete;

        for (Server vr : ServerThread.serverList) {
            if(vr.login.equals(this.login))
                continue;
            vr.sendLogin(login);
            sendLogin(vr.login);
        }


        try {

            try {
                while (true) {

                    word = in.readLine();

                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    Handler handler = new Handler();
                    InputStream is = new ByteArrayInputStream(word.getBytes());

                    parser.parse(is,handler);
                    message = handler.getMessage();


                    sender = handler.getSender();

                    receiver = handler.getReceiver();
                    delete = handler.getDelete();
                    String finalReceiver = receiver;
                    String finalSender = sender;
                    String finalMessage = message;
                    Platform.runLater(() -> {
                        s.add(finalMessage.replace(finalSender,finalSender+"-->"+ finalReceiver));
                    });



                    if(delete!=null)
                    {
                        if(delete.equals(this.login)) {
                            s.add(this.login+" is disconected");
                            this.downService();


                            break;
                        }
                    }




                    for (Server vr : ServerThread.serverList) {
                        if(vr.login.equals(receiver) )
                        {
                            vr.send(message,sender);
                            this.send(message,receiver);



                        }


                    }

                }
            } catch (NullPointerException ignored) {} catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            this.downService();
        }
    }


    private void send(String msg,String partner) {

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> " +
                "<message partner=\""+partner+"\">"+msg+"</message>");

    }


    private void sendLogin(String loginMsg) {

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> " +
                "<login>"+loginMsg+"</login>");


    }



    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                this.interrupt();
                ServerThread.serverList.remove(this);
                for (Server vr : ServerThread.serverList)
                    vr.sendLogin(this.login+"!");
                for (Server vr : ServerThread.serverList) {


                    if(vr.equals(this)){
                        vr.interrupt();
                        ServerThread.serverList.remove(this);
                    }

                }
            }
        } catch (IOException ignored) {}
    }
}




class ServerThread extends Thread {

    public static final int PORT = 8090;
    public static LinkedList<Server> serverList = new LinkedList<>(); // список всех нитей - экземпляров
    List<String> s;
    ServerThread(List<String> s){
        this.s = s;
    }

    @Override
    public void run() {

        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);



            try {

                while (true) {

                    Socket socket = server.accept();
                    try {
                        serverList.add(new Server(socket,s));

                    } catch (IOException e) {

                        socket.close();
                    }
                }
            } finally {
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


public class XServer implements Initializable {

    public ListView<String> messageList;


    public LinkedList<Server>  serverList (){return ServerThread.serverList;}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {new ServerThread(messageList.getItems()).start(); });






    }




}
