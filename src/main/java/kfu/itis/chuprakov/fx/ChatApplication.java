package kfu.itis.chuprakov.fx;

import javafx.application.Application;
import javafx.stage.Stage;
import kfu.itis.chuprakov.fx.bot.ChatBot;
import kfu.itis.chuprakov.fx.client.ChatClient;
import kfu.itis.chuprakov.fx.model.UserConfig;
import kfu.itis.chuprakov.fx.view.BaseView;
import kfu.itis.chuprakov.fx.view.ChatView;
import kfu.itis.chuprakov.fx.view.StartView;
import kfu.itis.chuprakov.fx.view.UserConfigView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class ChatApplication extends Application {
    public UserConfig userConfig;
    private ChatClient chatClient;
    private ChatBot chatBot;
    private ChatView chatView;
    private UserConfigView userConfigView;
    private StartView startView;
    private BorderPane root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(event -> System.exit(0));
        BaseView.setChatApplication(this);

        startView = new StartView();
        userConfigView = new UserConfigView();
        chatView = new ChatView();
        chatClient = new ChatClient(this);
        chatBot = new ChatBot(this);

        root = new BorderPane();

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.show();

        showStartView();
    }

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public void startChat() {
        chatClient.start();
    }

    public ChatView getChatView() {
        return chatView;
    }

    public UserConfigView getUserConfigView() {
        return userConfigView;
    }

    public StartView getStartView() {
        return startView;
    }

    public void setView(BaseView view) {
        root.setCenter(view.getView());
    }

    public void showStartView() {
        setView(startView);
    }

    public void showUserConfigView() {
        setView(userConfigView);
    }

    public void showChatView() {
        setView(chatView);
        chatView.append("Бот: Добро пожаловать в чат!\n" +
                "Используйте команды с символом /:\n" +
                "/list - список команд\n" +
                "/weather Москва - погода\n" +
                "/exchange USD - курс валют\n" +
                "/quit - выход в главное меню\n\n"
        );
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public ChatBot getChatBot() {
        return chatBot;
    }

    public void appendMessage(String message) {
        chatView.append(message);
    }

    public void processMessage(String message, String username) {
        if (message.startsWith("/")) {
            if (message.equalsIgnoreCase("/quit")) {
                showStartView();
            } else {
                chatView.append(username + ": " + message);
                chatBot.processCommand(message, username);
            }
        }
    }
}