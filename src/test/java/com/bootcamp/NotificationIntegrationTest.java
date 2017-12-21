package com.bootcamp;

import com.bootcamp.commons.utils.GsonUtils;
import com.bootcamp.commons.ws.usecases.pivotone.NotificationInput;
import com.bootcamp.entities.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;


/**
 * @author Ibrahim@abladon
 * <h2> The integration test for Notification controller</h2>
 * <p>
 * In this test class,
 * the methods :
 * <ul>
 * <li>create a notification </li>
 * <li>get one notification by it's id</li>
 * <li>get all notification</li>
 * <li>And update a notification have been implemented</li>
 * </ul>
 * before  getting started , make sure , the notification fonctionnel service is deploy and running as well.
 * you can also test it will the online running service
 * As this test interact directly with the local database, make sure that the specific database has been created
 * and all it's tables.
 * If you have data in the table,make sure that before creating a data with it's id, do not use
 * an existing id.
 * </p>
 */
public class NotificationIntegrationTest {
    private static Logger logger = LogManager.getLogger( NotificationIntegrationTest.class );
    Gson gson = new Gson();

    /**
     * The Base URI of notification fonctionnal service,
     * it can be change with the online URI of this service.
     */
    private String BASE_URI = "http://localhost:8093/notification";

    /**
     * The path of the Notification controller, according to this controller implementation
     */
    private String NOTIFICATION_PATH = "/notifications";

    /**
     * This ID is initialize for create , getById, and update method,
     * you have to change it if you have a save data on this ID otherwise
     * a error or conflit will be note by your test.
     */
    private int notificationId = 0;

    /**
     * This method create a new notification with the given id
     *
     * @throws Exception
     * @see Notification#id
     * <b>you have to chenge the name of
     * the notification if this name already exists in the database
     * else, the notification  will be created but not wiht the given ID.
     * and this will accure an error in the getById and update method</b>
     * Note that this method will be the first to execute
     * If every done , it will return a 200 httpStatus code
     */


    @Test(priority = 0, groups = {"NotificationTest"})
    public void checkEventAndgenerateNotificationTest() throws Exception {
        notificationId = 1;
        String createURI = BASE_URI + NOTIFICATION_PATH;
        NotificationInput input = getNotificationInputById( notificationId );
        Gson gson = new Gson();
        String notificationData = gson.toJson( input );
        Response response = given()
                .log().all()
                .contentType( "application/json" )
                .body( notificationData )
                .expect()
                .when()
                .post( createURI );
        logger.debug( response.getBody().prettyPrint() );
        Assert.assertEquals( response.statusCode(), 200 );
    }

    /**
     * Get All the notifications in the database
     * If every done , it will return a 200 httpStatus code
     *
     * @throws Exception
     */
    @Test(priority = 1, groups = {"NotificationTest"})
    public void getAllNotifications() throws Exception {
        String getAllNotificationURI = BASE_URI + NOTIFICATION_PATH;
        Response response = given()
                .log().all()
                .contentType( "application/json" )
                .expect()
                .when()
                .get( getAllNotificationURI );
        String resultText = response.getBody().print();
        notificationId = getLastObjectId( toObjList( resultText ) );
        logger.debug( response.getBody().prettyPrint() );
        Assert.assertEquals( response.statusCode(), 200 );

    }


    /**
     * This method get a notification with the given id
     *
     * @throws Exception
     * @see Notification#id
     * <b>
     * If the given ID doesn't exist it will log an error
     * </b>
     * Note that this method will be the second to execute
     * If every done , it will return a 200 httpStatus code
     */
    @Test(priority = 2, groups = {"NotificationTest"})
    public void getNotificationById() throws Exception {

        String getNotificationById = BASE_URI + NOTIFICATION_PATH + "/" + notificationId;
        Response response = given()
                .log().all()
                .contentType( "application/json" )
                .expect()
                .when()
                .get( getNotificationById );
        logger.debug( response.getBody().prettyPrint() );
        Assert.assertEquals( response.statusCode(), 200 );


    }


    /**
     * Update a notification with the given ID
     * <b>
     * the notification must exist in the database
     * </b>
     * Note that this method will be the third to execute
     * If every done , it will return a 200 httpStatus code
     *
     * @throws Exception
     */
    @Test(priority = 3, groups = {"NotificationTest"})
    public void updateNotification() throws Exception {
        String updateURI = BASE_URI + NOTIFICATION_PATH;
        Notification notification = loadDataNotificationFromJsonFile().get( 1 );
        notification.setId( notificationId );
        notification.setLibelle( "nouveau etat du projet1" );
        Gson gson = new Gson();
        String notificationData = gson.toJson( notification );
        Response response = given()
                .log().all()
                .contentType( "application/json" )
                .body( notificationData )
                .expect()
                .when()
                .put( updateURI );

        logger.debug( response.getBody().prettyPrint() );

        Assert.assertEquals( response.statusCode(), 200 );

    }

    /**
     * Delete a notification for the given ID
     * will return a 200 httpStatus code if OK
     *
     * @throws Exception
     */
    @Test(priority = 4, groups = {"NotificationTest"})
    public void deleteNotification() throws Exception {
        String deleteNotificationUI = BASE_URI + NOTIFICATION_PATH + "/" + notificationId;
        Response response = given()
                .log().all()
                .contentType( "application/json" )
                .expect()
                .when()
                .delete( deleteNotificationUI );
        Assert.assertEquals( response.statusCode(), 200 );

    }

    private List<Notification> toObjList(String text) throws Exception {
        Type typeOfObjectsListNew = new TypeToken<List<Notification>>() {
        }.getType();
        List<Notification> notifications = GsonUtils.getObjectFromJson( text, typeOfObjectsListNew );
        return notifications;
    }


    private NotificationInput getNotificationInputById(int id) throws Exception {
        List<NotificationInput> notifications = loadDataNotificationInputFromJsonFile();
        NotificationInput input = notifications.stream().filter( item -> item.getId() == id ).findFirst().get();
        return input;
    }

    private int getLastObjectId(List<Notification> list) throws Exception {
        Notification input = list.get( list.size() - 1 );
        return input.getId();
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
        File dataFile = getFile( "data-json" + File.separator + "input.json" );
        String text = Files.toString( new File( dataFile.getAbsolutePath() ), Charsets.UTF_8 );
        Type typeOfObjectsListNew = new TypeToken<List<NotificationInput>>() {
        }.getType();
        List<NotificationInput> notifications = GsonUtils.getObjectFromJson( text, typeOfObjectsListNew );
        return notifications;
    }

}
