package fr.RoueDeLaFortune.services;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class LoggerService {
    private static TextArea logTextArea = new TextArea();

    static {
        logTextArea.setEditable(false);
        logTextArea.setMinSize(450, 150); // largeur, hauteur
    }

    public static void log(String message) {
        Platform.runLater(() -> logTextArea.appendText(message + "\n"));
    }

    public static void clearLog() {
        Platform.runLater(logTextArea::clear);
    }

    public static TextArea getLogTextArea() {
        return logTextArea;
    }
}