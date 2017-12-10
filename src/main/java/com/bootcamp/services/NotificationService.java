package com.bootcamp.services;

import com.bootcamp.Classes.NotificationGn;
import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.exceptions.DatabaseException;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.commons.ws.utils.RequestParser;
import com.bootcamp.crud.NotificationCRUD;
import com.bootcamp.entities.Notification;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
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
    
    public boolean checkEventAndgenerateNotification(String action, int entityId, String entityType) throws FileNotFoundException{
        Gson gson = new Gson();
        
         BufferedReader bufferedReader = new BufferedReader(new FileReader(eventDictionnary));
        //convert the json string back to object
        List<NotificationGn> notificationgns = gson.fromJson(bufferedReader, List.class);
        
        for (NotificationGn notificationgn : notificationgns) {
            if (notificationgn.getAction().equalsIgnoreCase(action) && notificationgn.isGen_event()) {
                Notification notification = new Notification();
                notification.setLibelle(notificationgn.getLibelle());
                notification.setAction(notificationgn.getAction());
                notification.setEntityId(entityId);
                notification.setEntityType(entityType);
                return true;              
            }
        }
        return false;
    }
    

}
