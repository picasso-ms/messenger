package clientapp;

import javafx.application.Platform;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientConnection {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean ds = false;


    public void setMsgList(List<String> msgList) {
        this.msgList = msgList;
    }

    public List<String> getMsgList() {
        return msgList;
    }

    private List<String> msgList;

    public void setStory(HashMap<String, List<String>> story) {
        this.story = story;
    }

    public HashMap<String, List<String>> story;
    private List<String> loginList; // users login

    private String addr;
    private int port;


    private String login; // имя клиента
    private Date time;
    private String dtime;
    private String receiver;
    private SimpleDateFormat dt1;


    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setLoginList(List<String> loginList) {
        this.loginList = loginList;
    }






    public String getUserWord() {
        return userWord;
    }

    private String userWord;

    public void setUserWord(String userWord) {
        this.userWord = userWord;
    }

    private static int id  = 0;
    /**
     * для создания необходимо принять адрес и номер порта
     *
     * @param addr
     * @param port
     */

    public ClientConnection(String addr, int port,String login)
    {
        id++;
        this.addr = addr;
        this.port = port;
        this.login = login;
        try {
            this.socket = new Socket(addr, port);

        } catch (IOException e) {
            System.err.println("Socket failed");
        }

        try {


            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

            out.println(this.login);
            Timer timer = new Timer(true);
            timer.schedule(new ReadMsg(),0,1000);

        } catch (IOException e) {

            ClientConnection.this.downService();
        }

    }






    public void writeM(String userWord,String receiver)
    {

        this.receiver = receiver;

        try {

            time = new Date();
            dt1 = new SimpleDateFormat("HH:mm:ss");
            dtime = dt1.format(time);

            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?> " +
                    "<message type=\"send_message\" sender=\"" + login + "\" receiver=\""+receiver+"\" content=\"Test\">"+
                    "(" + dtime + ") " + login + ": " + userWord.replaceAll("\n",". ") + " </message>"); // отправляем на сервер

        } catch (Exception e) {
            ClientConnection.this.downService(); // в случае исключения тоже харакири

        }
    }


    /**
     * закрытие сокета
     */
    public void  downService() {
        try {

            if (!socket.isClosed()) {

                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><delete>"+ this.login+ "</delete>");
                loginList.clear();
                socket.close();
                in.close();
                out.close();
                ds  = true;
            }
        } catch (IOException ignored) {}
    }


    public class ReadMsg extends TimerTask {

        @Override
        public void run() {

            String str;
            String msg,userLogin,delete;

            try {
                while (true) {

                    if(ds == true)
                        break;
                    str = in.readLine(); // ждем сообщения с сервера


                    if(!str.contains("</login>")){
                        while(!str.contains("</message>"))
                        {
                            str+="\n"+ in.readLine();
                        }

                    }



                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    Handler handler = new Handler();
                    InputStream is = new ByteArrayInputStream(str.getBytes());

                    parser.parse(is,handler);
                    userLogin = handler.getLogin();
                    delete  = handler.getDelete();


                    String finalUserLogin = userLogin;


                    Platform.runLater(() -> {



                        if(finalUserLogin != null) {
                            if(finalUserLogin.endsWith("!"))
                            {
                                System.out.println(finalUserLogin);
                                loginList.remove(finalUserLogin.substring(0,finalUserLogin.length()-1));

                            }else{
                                System.out.println(finalUserLogin);
                                loginList.add(finalUserLogin);
                                story.put(finalUserLogin,new ArrayList<String>());
                            }
                        }
                        if(story.get(handler.getPartner())!= null) {
                            story.get(handler.getPartner()).add(handler.getMessage());
                            msgList.clear();
                            msgList.addAll(story.get(handler.getPartner()));
                        }

                    });

                }
            } catch (IOException e) {
                ClientConnection.this.downService();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }catch (NullPointerException ignored){}
        }
    }


}
