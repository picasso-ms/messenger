package clientapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable {
    @FXML
    public ListView <String> userList;
    public ListView <String> messageList;




    public HashMap<String,List<String>> story = new HashMap<String,List<String>>();

    public Label loginLabel;
    private String login;
    private String receiver;

    public TextArea inputField;
    public Button sendBtn;
    private boolean isReleased = true;
    private ClientConnection clientConnection;

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void setLogin(String login) {
        this.login = login;
        loginLabel.setText(this.login);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        userList.getSelectionModel().selectedItemProperty().addListener((v,oldValuse,newValue) -> {

            receiver = newValue;
            if(story.get(receiver)!=null)
                messageList.getItems().setAll(story.get(receiver));


        });

    }


    public void readM(){
        clientConnection.setLoginList(userList.getItems());
        clientConnection.setMsgList(messageList.getItems());
        clientConnection.setStory(story);

    }

    public void sendMessage()
    {

        if(inputField.getText().isEmpty())
            inputField.setPromptText("The field is empty");
        else if(receiver== null) {
            inputField.setText("");
            inputField.setPromptText("Choose a partner, please");
        }
        else{
            clientConnection.writeM(inputField.getText(),receiver);
            inputField.setText("");
        }


    }

    public void inputFieldListener(KeyEvent keyEvent) {


        if(keyEvent.getCode() == KeyCode.CONTROL)
        {
            isReleased = false;
        }
        if(keyEvent.getCode() ==  KeyCode.ENTER && !isReleased)
        {
            sendMessage();
        }


    }


    public void checkReleased(KeyEvent keyEvent) {
        if(keyEvent.getCode() ==  KeyCode.CONTROL)
        {
            isReleased = true;
        }
    }


    public void disconetc(ActionEvent actionEvent) {

        clientConnection.downService();
        Parent mainScene=null;


        try {
            mainScene = FXMLLoader.load(getClass().getResource("login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Scene newScene = new Scene(mainScene);
        Stage appStage = (Stage)  ( (Node) actionEvent.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.setMinWidth(650);
        appStage.setMinHeight(450);
        appStage.show();
    }


}
