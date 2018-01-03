import com.bootcamp.services.NotificationService;

import javax.mail.MessagingException;
import javax.management.Notification;

public class AppMain {
    public static void main(String[] strings) throws MessagingException {
        String[] destinataires={"bignonfebron@gmail.com"};
        NotificationService notificationService = new NotificationService();
        System.out.println(notificationService.returnInfos());
        //notificationService.sendMail("le message",destinataires);

    }
}
