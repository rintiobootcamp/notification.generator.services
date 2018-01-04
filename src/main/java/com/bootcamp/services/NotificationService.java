package com.bootcamp.services;

import com.bootcamp.classes.MessageApp;
import com.bootcamp.classes.NotificationGn;
import com.bootcamp.commons.ws.usecases.pivotone.NotificationInput;
import com.bootcamp.commons.constants.DatabaseConstants;
import com.bootcamp.commons.models.Criteria;
import com.bootcamp.commons.models.Criterias;
import com.bootcamp.crud.NotificationCRUD;
import com.bootcamp.crud.PagUserCRUD;
import com.bootcamp.crud.PreferenceCRUD;
import com.bootcamp.entities.Notification;
import com.bootcamp.entities.Preference;
import com.bootcamp.sender.SenderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Bignon on 11/27/17.
 */
@Component
public class NotificationService implements DatabaseConstants {

    @Value("${event_dictionnary_path}")
     String eventDictionnary;

    @Value("${client_web_base_path}")
    String web_path;

    @Value("${username:mboabello}")
     String USER_NAME;

    @Value("${port:465}")
     String port;

    @Value("${password:Bafetimbi92}")
     String PASSWORD;

    @Value("${host:mail.yahoo.fr}")
     String host;

    public String returnInfos(){
        return "eventDictionnary: "+eventDictionnary+"\n"+
                "web_path: "+web_path+"\n"+
                "USER_NAME: "+USER_NAME+"\n"+
                "port: "+port+"\n"
                ;

    }

    public Notification create(Notification notification) throws SQLException {
        notification.setDateCreation(System.currentTimeMillis());
        notification.setDateMiseAJour(System.currentTimeMillis());
        notification.setSendMail(false);
        notification.setSendSms(false);
        notification.setSendMobil(false);
        notification.setSendWebApp(false);
        NotificationCRUD.create(notification);
        return notification;
    }

    public Notification update(Notification notification) throws SQLException {
        notification.setDateMiseAJour(System.currentTimeMillis());
        NotificationCRUD.update(notification);
        return notification;
    }

    public boolean delete(int id) throws SQLException {
        Notification notification = read(id);
        return NotificationCRUD.delete(notification);
    }

    public Notification read(int id) throws SQLException {
        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("id", "=", id));
        List<Notification> notifications = NotificationCRUD.read(criterias);
        return notifications.get(0);
    }

    public List<Notification> readAll() throws SQLException {
        List<Notification> notifications = NotificationCRUD.read();
        return notifications;
    }

    public MessageApp buildMessage(Notification norification) {
        MessageApp msg = new MessageApp();
        msg.setTitre(norification.getLibelle());
        msg.setContenu(norification.getContenuMobileApp());
        msg.setDateCreation(norification.getDateCreation());
        return msg;
    }

    public List<MessageApp> getAppMessage(String mailUser, int size, long date) throws SQLException {
        List<MessageApp> messages = new ArrayList<MessageApp>();
        MessageApp msg = new MessageApp();
        msg.setTitre("Aucun message");
        msg.setContenu("Aucune notification depuis votre dernière connection");
        msg.setDateCreation(System.currentTimeMillis());

        Criterias criterias = new Criterias();
        criterias.addCriteria(new Criteria("dateCreation", ">", date));
        List<Notification> notifications = NotificationCRUD.read(criterias, 0, size);

        if (notifications.isEmpty()) {
            messages.add(msg);
            return messages;
        } else {
            for (Notification notification : notifications) {
                messages.add(this.buildMessage(notification));
            }
            return messages;
        }
    }

    public boolean checkEventAndgenerateNotification(NotificationInput input) throws FileNotFoundException, SQLException, IOException {
        String chaine = readFileToString();
        Gson gson = new Gson();
        Type type = new TypeToken<List<NotificationGn>>() {
        }.getType();
        List<NotificationGn> notificationgns = gson.fromJson(chaine, type);

        for (NotificationGn notificationgn : notificationgns) {
            //Si l'action de l'input est égale à l'une des actions du event dictionnary et
            //si la génation est activer, on génére une notification
            if (notificationgn.getAction().equalsIgnoreCase(input.getAction().toString()) && notificationgn.isGen_event()) {
                //Construire l'objet notificztion
                Notification notification = new Notification();
                notification.setLibelle(this.encodeString(notificationgn.getLibelle()));
                notification.setAction(notificationgn.getAction());
                notification.setEntityId(input.getEntityId());
                notification.setEntityType(input.getEntityType());
                notification.setContenuGsm(getSmsMessage(input, notificationgn.getDiffusions().get(0).getMessage()));
                notification.setContenuMail(getMailMessage(input, notificationgn.getDiffusions().get(1).getMessage()));
                notification.setContenuMobileApp(getMobileMessage(input, notificationgn.getDiffusions().get(3).getMessage()));
                notification.setContenuWebApp(getWebMessage(input, notificationgn.getDiffusions().get(2).getMessage()));
                notification = this.create(notification);

                System.out.println(notification.getAction());
                boolean bool = SenderService.sendNotification(notification);
                System.out.println(bool);
                return true;
            }
        }
        return false;
    }

    public String getSmsMessage(NotificationInput input, String baseMsg) throws SQLException {
        String action = input.getAction().toString().split("_")[0];
        String message = "";
        baseMsg = this.encodeString(baseMsg);
        String corps = ". Le projet est passé à ";
        String lien = web_path + "/" + input.getEntityType().toLowerCase() + "/" + input.getEntityId();
        if (action.equalsIgnoreCase("NEW") || action.equalsIgnoreCase("CLOSE")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + "\n" + lien;
        }

        if (action.equalsIgnoreCase("UPDATE")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + corps + input.getCurrentVersion() + "\n" + lien;
        }

        return message;
    }

    public String getMailMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().toString().split("_")[0];
        String lien = web_path + "/" + input.getEntityType().toLowerCase() + "/" + input.getEntityId();
        baseMsg = this.encodeString(baseMsg);
        String corps = ". Le projet est passé à ";
        if (action.equalsIgnoreCase("new") || action.equalsIgnoreCase("close")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + "\n" + lien;
        }

        if (action.equalsIgnoreCase("update")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + corps + input.getCurrentVersion() + "\n" + lien;
        }
        return message;
    }

    public String getWebMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().toString().split("_")[0];
        String lien = web_path + "/" + input.getEntityType().toLowerCase() + "/" + input.getEntityId();
        baseMsg = this.encodeString(baseMsg);
        String corps = ". Le projet est passé à ";
        if (action.equalsIgnoreCase("new") || action.equalsIgnoreCase("close")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + "\n" + lien;
        }

        if (action.equalsIgnoreCase("update")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + corps + input.getCurrentVersion() + "\n" + lien;
        }

        return message;
    }

    public String getMobileMessage(NotificationInput input, String baseMsg) throws SQLException {
        String message = "";
        String action = input.getAction().toString().split("_")[0];
        String lien = web_path + "/" + input.getEntityType().toLowerCase() + "/" + input.getEntityId();
        baseMsg = this.encodeString(baseMsg);
        String corps = ". Le projet est passé à ";
        if (action.equalsIgnoreCase("new") || action.equalsIgnoreCase("close")) {
            message = baseMsg + "\"" + input.getTitre() + "\"\n" + lien;
        }

        if (action.equalsIgnoreCase("update")) {
            message = baseMsg + "\"" + input.getTitre() + "\"" + corps + input.getCurrentVersion() + "\n" + lien;
        }

        return message;
    }

    private String readFileToString() throws IOException {
        String filePath = eventDictionnary;
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
    // mail  sender
    public void sendMail(String messageMail, String[] destinataires) throws MessagingException {

        System.setProperty("https.protocols", "TLSv1.1");
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.user", "mboabello");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.password", PASSWORD);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", host);

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(USER_NAME));
            InternetAddress[] toAddress = new InternetAddress[destinataires.length];

            // To get the array of addresses
            for (int i = 0; i < destinataires.length; i++) {
                toAddress[i] = new InternetAddress(destinataires[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject("Notification de la Plateforme monPAG");
            message.setText(messageMail);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, USER_NAME, PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }


//        return s;
private String encodeString(String message) {

        String returnStr = new String(message.getBytes(),Charset.forName("UTF-8"));
        return returnStr;

        //return message;
    }
}
