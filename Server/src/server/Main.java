package server;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 * проект реализует консольный многопользовательский чат.
 * вход в программу запуска сервера - в классе server.Main.
 * @author izotopraspadov, the tech
 * @version 2.0
 */




public class Main extends Application {



    public static void main(String[] args) throws IOException {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {



        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chatWindow.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("chatWindow.fxml"));
        Parent root = loader.load();

        XServer xs = loader.getController();
        //LinkedList<Server> sL = xs.serverList();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                for(Server s : ServerThread.serverList)
                    s.interrupt();
            }
        });


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(450);
        primaryStage.show();




    }
}