package kfu.itis.chuprakov.fx.view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import kfu.itis.chuprakov.fx.model.UserConfig;

public class UserConfigView extends BaseView {

    private VBox root;
    private TextField username;
    private TextField host;
    private TextField port;
    private Button startButton;
    private Button backButton;

    @Override
    public Parent getView() {
        if (root == null) {
            createView();
        }
        return root;
    }

    public void createView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Настройки подключения");
        title.setFont(Font.font(20));

        VBox form = new VBox(10);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(300);

        Label usernameLabel = new Label("Имя пользователя:");
        username = new TextField();
        username.setPromptText("Введите ваше имя");

        Label hostLabel = new Label("Хост сервера:");
        host = new TextField("127.0.0.1");
        host.setPromptText("127.0.0.1");

        Label portLabel = new Label("Порт:");
        port = new TextField("5555");
        port.setPromptText("5555");

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        backButton = new Button("Назад");
        backButton.setOnAction(event -> {
            getChatApplication().showStartView();
        });

        startButton = new Button("Подключиться");
        startButton.setOnAction(event -> {
            if (validateInput()) {
                UserConfig userConfig = new UserConfig(
                        username.getText().trim(),
                        host.getText().trim(),
                        Integer.parseInt(port.getText().trim())
                );
                getChatApplication().setUserConfig(userConfig);
                getChatApplication().startChat();
                getChatApplication().showChatView();
            }
        });

        buttonsBox.getChildren().addAll(backButton, startButton);

        form.getChildren().addAll(
                usernameLabel, username,
                hostLabel, host,
                portLabel, port,
                buttonsBox
        );

        root.getChildren().addAll(title, form);
    }

    private boolean validateInput() {
        if (username.getText().trim().isEmpty()) {
            showError("Введите имя пользователя");
            return false;
        }
        if (host.getText().trim().isEmpty()) {
            showError("Введите хост сервера");
            return false;
        }
        try {
            Integer.parseInt(port.getText().trim());
        } catch (NumberFormatException e) {
            showError("Порт должен быть числом");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        System.err.println("Ошибка: " + message);
    }
}