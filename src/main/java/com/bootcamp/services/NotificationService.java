package com.bootcamp.services;

import com.bootcamp.Classes.NotificationGn;
import com.bootcamp.Classes.NotificationInput;
import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.crud.NotificationCRUD;
import com.bootcamp.entities.Notification;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Bignon on 11/27/17.
 */

@Component
public class NotificationService implements DatabaseConstants{
    
    @Value("${event_dictionnary_path}")
    String eventDictionnary;

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
    
    public boolean checkEventAndgenerateNotification(NotificationInput input) throws FileNotFoundException, SQLException{
        Gson gson = new Gson();
        
         BufferedReader bufferedReader = new BufferedReader(new FileReader(eventDictionnary));
        //convert the json string back to object
        List<NotificationGn> notificationgns = gson.fromJson(bufferedReader, List.class);
        
        for (NotificationGn notificationgn : notificationgns) {
            if (notificationgn.getAction().equalsIgnoreCase(input.getAction()) && notificationgn.isGen_event()) {
                Notification notification = new Notification();
                notification.setLibelle(notificationgn.getLibelle());
                notification.setAction(notificationgn.getAction());
                notification.setEntityId(input.getEntityId());
                notification.setEntityType(input.getEntityType());
                
                notification.setContenuGsm(getSmsMessage(input,notificationgn.getDiffusions().get(0).getMessage()));
                notification.setContenuMail(getMailMessage(input,notificationgn.getDiffusions().get(1).getMessage()));
                notification.setContenuMobileApp(getMobileMessage(input,notificationgn.getDiffusions().get(3).getMessage()));
                notification.setContenuWebApp(getWebMessage(input,notificationgn.getDiffusions().get(2).getMessage()));
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
         message = baseMsg+input.getTitre();
        }
        
        if (action.equals("update")) {
        message = baseMsg + input.getTitre()+" de "+input.getLastVersion()+" a "+input.getCurrentVersion();
         }
        
        return message;
    }
    
    public String getMailMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().split("_")[0];
        if (action.equals("new") || action.equals("close")) {
         message = baseMsg+input.getTitre();
        }
        
        if (action.equals("update")) {
        message =baseMsg+input.getTitre()+"\n Ancienne Valeur de "+input.getAttributName()+": "+input.getLastVersion()
                                           +"\n Nouvelle Valeur de "+input.getAttributName()+": "+input.getCurrentVersion();
        }
        return message;
    }
    
    public String getWebMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().split("_")[0];
        if (action.equals("new") || action.equals("close")) {
         message = baseMsg+input.getTitre();
        }
        
        if (action.equals("update")) {
        message =baseMsg+input.getTitre()+"\n Ancienne Valeur de "+input.getAttributName()+": "+input.getLastVersion()
                                           +"\n Nouvelle Valeur de "+input.getAttributName()+": "+input.getCurrentVersion();
        }
        return message;
    }
    
    public String getMobileMessage(NotificationInput input, String baseMsg) throws SQLException {
        String action = input.getAction().split("_")[0];
        String message = "";
        if (action.equals("new") || action.equals("close")) {
         message = baseMsg+input.getTitre();
        }
        
        if (action.equals("update")) {
        message = baseMsg + input.getTitre()+" de "+input.getLastVersion()+" a "+input.getCurrentVersion();
         }
        
        return message;
    }
}
