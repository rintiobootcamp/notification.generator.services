package com.bootcamp;

import com.bootcamp.application.Application;
import com.bootcamp.commons.utils.GsonUtils;
import com.bootcamp.commons.ws.usecases.pivotone.NotificationInput;
import com.bootcamp.controllers.NotificationController;
import com.bootcamp.entities.Notification;
import com.bootcamp.services.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 *@author Ibrahim@abladon
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = NotificationController.class, secure = false)
@ContextConfiguration(classes = {Application.class})
public class NotificationControllerTest {
    private final Logger logger = LoggerFactory.getLogger( NotificationServiceTest.class );


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NotificationService notificationService;

    @Test
    public void createNotificationTest() throws Exception {
        Notification notification = loadDataNotificationFromJsonFile().get( 1 );

        Mockito.
                when( notificationService.create( notification ) ).thenReturn( notification );

    }

    @Test
    public void updateNotificationTest() throws Exception {
        Notification notification = loadDataNotificationFromJsonFile().get( 1 );
        notification.setLibelle( "modification de la notifacation" );
        Mockito.
                when( notificationService.update( notification ) ).thenReturn( notification );

        RequestBuilder requestBuilder =
                put( "/notifications" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectToJson( notification ) );

        MvcResult result = mockMvc.perform( requestBuilder ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        logger.debug( response.getContentAsString() );

        mockMvc.perform( requestBuilder ).andExpect( status().isOk() );


    }

    @Test
    public void getNotificationByIdForController() throws Exception{
        Notification notification = loadDataNotificationFromJsonFile().get( 1 );

        Mockito.
                when(notificationService.read(1)).thenReturn(notification);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/notifications/{id}",1)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        logger.debug(response.getContentAsString(),notification);

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
        logger.info("*********************************Test for get a notification by id in notification controller done *******************");

    }


    @Test
    public void getAllNotificationsTes() throws Exception{
        List<Notification> notifications =  loadDataNotificationFromJsonFile();
        System.out.println(notifications.size());
        Mockito.
                when(notificationService.readAll()).thenReturn(notifications);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/notifications")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        System.out.println(response.getContentAsString());

        mockMvc.perform(requestBuilder).andExpect(status().isOk());

    }

    @Test
    public void deleteNotificationTest() throws Exception{
        int id = 1;
        Mockito.
        when(notificationService.delete(id)).thenReturn(true);

        RequestBuilder requestBuilder =
                delete("/notifications/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        logger.debug(response.getContentAsString());

        mockMvc.perform(requestBuilder).andExpect(status().isOk());
       logger.info("*********************************Test for delete notification in notification controller done *******************");


    }

    @Test
    public void checkEventAndgenerateNotification() throws Exception{
        List<NotificationInput> notificationInputs = loadDataNotificationInputFromJsonFile();
        NotificationInput input = notificationInputs.get(1);
        Mockito.
                when(notificationService.checkEventAndgenerateNotification( input )).thenReturn(true);

        RequestBuilder requestBuilder =
                post( "/notifications" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectToJson( input ) );

        MvcResult result = mockMvc.perform( requestBuilder ).andReturn();

        MockHttpServletResponse response = result.getResponse();

        logger.debug( response.getContentAsString() );

        mockMvc.perform( requestBuilder ).andExpect( status().isOk() );

    }

    public static String objectToJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString( obj );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    public File getFile(String relativePath) throws Exception {

        File file = new File( getClass().getClassLoader().getResource( relativePath ).toURI() );

        if (!file.exists()) {
            throw new FileNotFoundException( "File:" + relativePath );
        }

        return file;
    }

    public List<Notification> loadDataNotificationFromJsonFile() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile( "data-json" + File.separator + "notification.json" );

        String text = Files.toString( new File( dataFile.getAbsolutePath() ), Charsets.UTF_8 );

        Type typeOfObjectsListNew = new TypeToken<List<Notification>>() {
        }.getType();
        List<Notification> notifications = GsonUtils.getObjectFromJson( text, typeOfObjectsListNew );

        return notifications;
    }

    public List<NotificationInput> loadDataNotificationInputFromJsonFile() throws Exception {
        //TestUtils testUtils = new TestUtils();
        File dataFile = getFile( "data-json" + File.separator + "input.json" );

        String text = Files.toString( new File( dataFile.getAbsolutePath() ), Charsets.UTF_8 );

        Type typeOfObjectsListNew = new TypeToken<List<NotificationInput>>() {
        }.getType();
        List<NotificationInput> notifications = GsonUtils.getObjectFromJson( text, typeOfObjectsListNew );

        return notifications;
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

}