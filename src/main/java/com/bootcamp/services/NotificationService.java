package com.bootcamp.services;

import com.bootcamp.Classes.NotificationGn;
import com.bootcamp.Classes.NotificationInput;
import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.crud.NotificationCRUD;
import com.bootcamp.entities.Notification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Bignon on 11/27/17.
 */
@Component
public class NotificationService implements DatabaseConstants {

    //@Value("${media.location}")
    @Value("${event.dictionnary.path}")
    String eventDictionnary;

    public String readFromFile() throws SQLException, FileNotFoundException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:/Epub/eventDictionnary.json"));
        return bufferedReader.toString();
    }

    public Notification create(Notification notification) throws SQLException {
        notification.setDateCreation(System.currentTimeMillis());
        notification.setDateMiseAJour(System.currentTimeMillis());
        NotificationCRUD.create(notification);
        return notification;
    }

    public Notification update(Notification notification) throws SQLException {
        notification.setDateMiseAJour(System.currentTimeMillis());
        NotificationCRUD.update(notification);

        return notification;
    }

    public Notification delete(int id) throws SQLException {
        Notification notification = read(id);
        NotificationCRUD.delete(notification);

        return notification;
    }

    public Notification read(int id) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", id));
        List<Notification> notifications = NotificationCRUD.read(criterias);

        return notifications.get(0);
    }

    public List<Notification> read() throws SQLException {
        List<Notification> notifications = NotificationCRUD.read();
        return notifications;
    }

    public boolean checkEventAndgenerateNotification(NotificationInput input) throws FileNotFoundException, SQLException, IOException {
        String chaine = readFileToString();
        Gson gson = new Gson();
        Type type = new TypeToken<List<NotificationGn>>() {
        }.getType();
        List<NotificationGn> notificationgns = gson.fromJson(chaine, type);

        for (NotificationGn notificationgn : notificationgns) {
            if (notificationgn.getAction().equalsIgnoreCase(input.getAction()) && notificationgn.isGen_event()) {
                Notification notification = new Notification();
                notification.setLibelle(notificationgn.getLibelle());
                notification.setAction(notificationgn.getAction());
                notification.setEntityId(input.getEntityId());
                notification.setEntityType(input.getEntityType());

                notification.setContenuGsm(getSmsMessage(input, notificationgn.getDiffusions().get(0).getMessage()));
                notification.setContenuMail(getMailMessage(input, notificationgn.getDiffusions().get(1).getMessage()));
                notification.setContenuMobileApp(getMobileMessage(input, notificationgn.getDiffusions().get(3).getMessage()));
                notification.setContenuWebApp(getWebMessage(input, notificationgn.getDiffusions().get(2).getMessage()));
                this.create(notification);
                return true;
            }
        }
        return false;
    }

    public String getSmsMessage(NotificationInput input, String baseMsg) throws SQLException {
        String action = input.getAction().split("_")[0];
        String message = "";
        if (action.equals("new") || action.equals("close")) {
            message = baseMsg + input.getTitre();
        }

        if (action.equals("update")) {
            message = baseMsg + input.getTitre() + " de " + input.getLastVersion() + " a " + input.getCurrentVersion();
        }

        return message;
    }

    public String getMailMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().split("_")[0];
        if (action.equals("new") || action.equals("close")) {
            message = baseMsg + input.getTitre();
        }

        if (action.equals("update")) {
            message = baseMsg + input.getTitre() + "\n Ancienne Valeur de " + input.getAttributName() + ": " + input.getLastVersion()
                    + "\n Nouvelle Valeur de " + input.getAttributName() + ": " + input.getCurrentVersion();
        }
        return message;
    }

    public String getWebMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().split("_")[0];
        if (action.equals("new") || action.equals("close")) {
            message = baseMsg + input.getTitre();
        }

        if (action.equals("update")) {
            message = baseMsg + input.getTitre() + "\n Ancienne Valeur de " + input.getAttributName() + ": " + input.getLastVersion()
                    + "\n Nouvelle Valeur de " + input.getAttributName() + ": " + input.getCurrentVersion();
        }
        return message;
    }

    public String getMobileMessage(NotificationInput input, String baseMsg) throws SQLException {
        String action = input.getAction().split("_")[0];
        String message = "";
        if (action.equals("new") || action.equals("close")) {
            message = baseMsg + input.getTitre();
        }

        if (action.equals("update")) {
            message = baseMsg + input.getTitre() + " de " + input.getLastVersion() + " a " + input.getCurrentVersion();
        }

        return message;
    }

    public static String readFileToString() throws IOException {
        String filePath = "D:/Epub/eventDictionnary.json";
        StringBuilder fileData = new StringBuilder(1000);//Constructs a string buffer with no characters in it and the specified initial capacity
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        String returnStr = fileData.toString();
        return returnStr;
    }
}
