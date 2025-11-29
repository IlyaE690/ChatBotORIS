package kfu.itis.chuprakov.fx.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class StartView extends BaseView {

    private VBox root;
    private Button startButton;

    @Override
    public Parent getView() {
        if (root == null) {
            createView();
        }
        return root;
    }

    private void createView() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Chat Bot Application");
        title.setFont(Font.font(24));
        title.setTextAlignment(TextAlignment.CENTER);

        Label subtitle = new Label("Чат-бот с командами");
        subtitle.setFont(Font.font(14));
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setWrapText(true);

        startButton = new Button("Начать общение");
        startButton.setOnAction(event -> {
            getChatApplication().showUserConfigView();
        });

        VBox commandsInfo = new VBox(10);
        commandsInfo.setAlignment(Pos.CENTER_LEFT);



        root.getChildren().addAll(title, subtitle, startButton, commandsInfo);
    }
}