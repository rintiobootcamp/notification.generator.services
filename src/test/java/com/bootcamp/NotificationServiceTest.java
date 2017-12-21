package com.bootcamp;

import com.bootcamp.application.Application;
import com.bootcamp.commons.utils.GsonUtils;
import com.bootcamp.commons.ws.usecases.pivotone.NotificationInput;
import com.bootcamp.crud.NotificationCRUD;
import com.bootcamp.entities.Notification;
import com.bootcamp.services.NotificationService;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Bello and Ibrahim@abladon on 12/9/17.
 */
@RunWith(PowerMockRunner.class)
@WebMvcTest(value = NotificationService.class, secure = false)
@ContextConfiguration(classes = {Application.class})
@PrepareForTest(NotificationCRUD.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class NotificationServiceTest {

    private final Logger logger = LoggerFactory.getLogger(NotificationServiceTest.class);
    Gson gson = new Gson();
    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void createTest() throws Exception {
        List<Notification> notifications = loadDataNotificationFromJsonFile();
        for (Notification notification : notifications) {
            PowerMockito.mockStatic(NotificationCRUD.class);
            logger.debug(gson.toJson(notification).toString());
            Mockito.when(NotificationCRUD.create(notification)).thenReturn(true);
        }
    }

    @Test
    public void getAllNotification() throws Exception {
        List<Notification> notifications = loadDataNotificationFromJsonFile();
        PowerMockito.mockStatic(NotificationCRUD.class);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.
                when(NotificationCRUD.read()).thenReturn(notifications);
        List<Notification> resultNotifications = notificationService.readAll();
        Assert.assertEquals(notifications.size(), resultNotifications.size());
        logger.debug(gson.toJson(resultNotifications));

    }

    @Test
    public void delete() throws Exception {
        List<Notification> notifications = loadDataNotificationFromJsonFile();
        Notification notification = notifications.get(1);

        PowerMockito.mockStatic(NotificationCRUD.class);
        Mockito.when(NotificationCRUD.delete(notification)).thenReturn(true);
    }

    @Test
    public void update() throws Exception {
        List<Notification> notifications = loadDataNotificationFromJsonFile();
        Notification notification = notifications.get(1);

        PowerMockito.mockStatic(NotificationCRUD.class);
        Mockito.when(NotificationCRUD.update(notification)).thenReturn(true);
    }

    public File getFile(String relativePath) throws Exception {

        File file = new File(getClass().getClassLoader().getResource(relativePath).toURI());

        if (!file.exists()) {
            throw new FileNotFoundException("File:" + relativePath);
        }

        return file;
    }

    public List<Notification> loadDataNotificationFromJsonFile() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile("data-json" + File.separator + "notification.json");

        String text = Files.toString(new File(dataFile.getAbsolutePath()), Charsets.UTF_8);

        Type typeOfObjectsListNew = new TypeToken<List<Notification>>() {
        }.getType();
        List<Notification> notifications = GsonUtils.getObjectFromJson(text, typeOfObjectsListNew);

        return notifications;
    }

    public List<NotificationInput> loadDataNotificationInputFromJsonFile() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile("data-json" + File.separator + "input.json");

        String text = Files.toString(new File(dataFile.getAbsolutePath()), Charsets.UTF_8);

        Type typeOfObjectsListNew = new TypeToken<List<NotificationInput>>() {
        }.getType();
        List<NotificationInput> notifications = GsonUtils.getObjectFromJson(text, typeOfObjectsListNew);

        return notifications;
    }
}

    /*public boolean checkEventAndgenerateNotification(NotificationInput input) throws FileNotFoundException, SQLException, IOException {
        String chaine = NotificationService.readFileToString();
        Gson gson = new Gson();
        Type type = new TypeToken<List<NotificationGn>>() {
        }.getType();
        List<NotificationGn> notificationgns = gson.fromJson( chaine, type );

        for (NotificationGn notificationgn : notificationgns) {
            //Si l'action de l'input est égale à l'une des actions du event dictionnary et
            //si la génation est activer, on génére une notification
            if (notificationgn.getAction().equalsIgnoreCase( input.getAction().toString() ) && notificationgn.isGen_event()) {
                //Construire l'objet notificztion
                Notification notification = new Notification();
                notification.setLibelle( notificationgn.getLibelle() );
                notification.setAction( notificationgn.getAction() );
                notification.setEntityId( input.getEntityId() );
                notification.setEntityType( input.getEntityType() );

                notification.setContenuGsm( notificationService.getSmsMessage( input, notificationgn.getDiffusions().get( 0 ).getMessage() ) );
                notification.setContenuMail( notificationService.getMailMessage( input, notificationgn.getDiffusions().get( 1 ).getMessage() ) );
                notification.setContenuMobileApp( notificationService.getMobileMessage( input, notificationgn.getDiffusions().get( 3 ).getMessage() ) );
                notification.setContenuWebApp( notificationService.getWebMessage( input, notificationgn.getDiffusions().get( 2 ).getMessage() ) );
                return true;
            }
        }
        return false;
    }*/
