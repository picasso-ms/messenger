package clientapp;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LoginController implements Initializable {
    public TextField loginField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void connect(Event event) {






        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chatWindow.fxml"));
        Parent p= null;
        try {
            p = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene newScene = new Scene(p);




        ChatWindowController cwc = loader.getController();

        cwc.setLogin(loginField.getText());

        cwc.setClientConnection(new ClientConnection("localhost",8090,loginField.getText()));
        cwc.readM();

        Stage appStage = (Stage)  ( (Node) event.getSource()).getScene().getWindow();
        appStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                cwc.getClientConnection().downService();
            }
        });
        appStage.setScene(newScene);
        appStage.setMinWidth(650);
        appStage.setMinHeight(450);
        appStage.show();





    }

    public void loginFieldListener(KeyEvent keyEvent) {

        if(keyEvent.getCode() ==  KeyCode.ENTER)
        {

            connect(keyEvent);
        }

    }

    public void connectBtnListener(ActionEvent actionEvent) {

        if(Pattern.matches("[a-zA-Z0-9]+",loginField.getText())&& loginField.getText().length()>=4)
            connect(actionEvent);
        else {
            loginField.setText("");
            loginField.setPromptText("Wrong login");
        }

    }
}
