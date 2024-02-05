package fr.RoueDeLaFortune.services;
import fr.RoueDeLaFortune.ILangueUpdateable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LangueManager {
    private static Locale currentLocale = Locale.FRENCH;
    private static List<ILangueUpdateable> listeners = new ArrayList<>();

    public static void setLocale(Locale newLocale) {
        currentLocale = newLocale;
        notifyListeners();
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("messages", currentLocale);
    }

    public static void addLangueListener(ILangueUpdateable listener) {
        listeners.add(listener);
    }

    private static void notifyListeners() {
        for (ILangueUpdateable listener : listeners) {
            listener.updateLangue();
        }
    }

}
