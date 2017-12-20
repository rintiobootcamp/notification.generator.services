package com.bootcamp.sender;

import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.commons.ws.usecases.pivotone.UserWs;
import com.bootcamp.crud.PagUserCRUD;
import com.bootcamp.crud.PreferenceCRUD;
import com.bootcamp.entities.Notification;
import com.bootcamp.entities.PagUser;
import com.bootcamp.entities.Preference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 * @author Bello & Bignon
 */
public class SenderService {

    private static String[] getMailList(List<UserWs> users) {
        List<String> destinataires = new ArrayList<String>();

        for (UserWs user : users) {
            destinataires.add(user.getEmail());
        }
        return destinataires.toArray(new String[0]);
    }

    private static String getNumeroList(List<UserWs> users) {
        String destinataires = "";

        for (UserWs user : users) {
            if (destinataires.isEmpty()) {
                destinataires += "00229" + user.getNumero();
            } else {
                destinataires += "," + "00229" + user.getNumero();
            }

        }
        return destinataires;
    }

    private static List<UserWs> getUsersList(String entityType, int entityId) {
        List<UserWs> users = new ArrayList<UserWs>();
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("entityId", "=", entityId, "AND"));
        criterias.addCriteria(new Criteria("entityType", "=", entityType));
        List<Preference> preferences = PreferenceCRUD.read(criterias);

        for (Preference preference : preferences) {
            int userId = preference.getUserId();
            users.add(SenderService.getUser(userId));
        }
        return users;
    }

    private static UserWs getUser(int userId) {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", userId));
        PagUser user = PagUserCRUD.read(criterias).get(0);

        UserWs userWs = new UserWs();
        userWs.setId(user.getId());
        userWs.setNom(user.getNom());
        userWs.setEmail(user.getEmail());
        userWs.setNumero(user.getNumero());
        userWs.setUsername(user.getUsername());
        userWs.setPassword(user.getPassword());
        return userWs;
    }

    public static boolean sendNotification(Notification notification) {
        List<UserWs> users = SenderService.getUsersList(notification.getEntityType(), notification.getEntityId());
        if (users.isEmpty()) {
            return false;
        } else {
            String[] destinatairesMail = SenderService.getMailList(users);
            String destinatairesNumeros = SenderService.getNumeroList(users);

            try {
                MailSender.sendMail(notification.getContenuMail(), destinatairesMail);
            } catch (MessagingException ex) {
                Logger.getLogger(SenderService.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                SmsSender.sendSms(notification.getContenuGsm(), destinatairesNumeros);
            } catch (IOException ex) {
                Logger.getLogger(SenderService.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }

    }
}
