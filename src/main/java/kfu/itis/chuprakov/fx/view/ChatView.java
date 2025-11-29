package kfu.itis.chuprakov.fx.view;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class ChatView extends BaseView {

    private AnchorPane root;
    private TextArea conversation;
    private TextArea input;
    private Button quitButton;

    @Override
    public Parent getView() {
        if (root == null) {
            createView();
        }
        return root;
    }

    public void append(String message) {
        if (message != null) {
            conversation.appendText(message + "\n");
        }
    }

    private void createView() {
        root = new AnchorPane();

        HBox topPanel = new HBox();

        quitButton = new Button("Выйти в меню");
        quitButton.setOnAction(event -> {
            getChatApplication().showStartView();
        });

        topPanel.getChildren().add(quitButton);

        AnchorPane.setTopAnchor(topPanel, 0.0);
        AnchorPane.setLeftAnchor(topPanel, 0.0);
        AnchorPane.setRightAnchor(topPanel, 0.0);

        conversation = new TextArea();
        conversation.setEditable(false);
        conversation.setWrapText(true);

        AnchorPane.setTopAnchor(conversation, 40.0);
        AnchorPane.setLeftAnchor(conversation, 0.0);
        AnchorPane.setRightAnchor(conversation, 0.0);
        AnchorPane.setBottomAnchor(conversation, 70.0);

        input = new TextArea();
        input.setMaxHeight(60);

        AnchorPane.setLeftAnchor(input, 0.0);
        AnchorPane.setRightAnchor(input, 0.0);
        AnchorPane.setBottomAnchor(input, 10.0);

        input.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    input.appendText("\n");
                } else {
                    String message = input.getText().trim();
                    if (!message.isEmpty()) {
                        String username = getChatApplication().userConfig.username();

                        if (message.startsWith("/")) {
                            getChatApplication().processMessage(message, username);
                        } else {
                            getChatApplication().getChatClient().send(username + ": " + message);
                            conversation.appendText("Вы: " + message);
                        }
                        input.clear();
                    }
                    event.consume();
                }
            }
        });

        root.getChildren().addAll(topPanel, conversation, input);
    }
}