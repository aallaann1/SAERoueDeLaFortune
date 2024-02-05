package fr.RoueDeLaFortune.services;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatService {
    private static TextFlow chatTextFlow = new TextFlow();

    static {
        chatTextFlow.setPrefSize(450, 150);
    }

    public static void ajouterMessage(String message, boolean enGras) {
        Platform.runLater(() -> {
            Text texte = new Text(message + "\n");
            if (enGras) {
                texte.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            }
            chatTextFlow.getChildren().add(texte);
        });
    }

    public static TextFlow getChatTextArea() {
        return chatTextFlow;
    }
}
