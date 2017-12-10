/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootcamp;

import com.bootcamp.Classes.NotificationInput;
import com.bootcamp.services.NotificationService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Bignon
 */
public class NotificationPrimitiveTest {

    NotificationService notificationService = new NotificationService();
    @Test
    public void generateNotificationTest() throws FileNotFoundException, SQLException, IOException {
        NotificationInput input = new NotificationInput();
        input.setAction("new_project");
        input.setEntityId(2);
        input.setEntityType("PROJET");
        input.setTitre("la ville numerique de seme city");

        Assert.assertTrue(notificationService.checkEventAndgenerateNotification(input));

        NotificationInput input1 = new NotificationInput();
        input1.setAction("update_project_phase");
        input1.setEntityId(1);
        input1.setEntityType("PROJET");
        input1.setTitre("interconnexion des ministeres");
        input1.setAttributName("phase actuelle");
        input1.setLastVersion("pre_etude");
        input1.setCurrentVersion("financement");

        Assert.assertTrue(notificationService.checkEventAndgenerateNotification(input1));
    }

//    @Test
//    public void generateNotificationTest() throws FileNotFoundException, SQLException, IOException {
//        String chaine = readFileToString();
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<NotificationGn>>(){}.getType();
//        List<NotificationGn> notificationgns = gson.fromJson(chaine, type);
//
////        System.out.println(notificationgns);
//
//        for (NotificationGn notificationgn : notificationgns) {
//            notificationgn.getLibelle();
//        }
//    }
//
//    public static String readFileToString() throws IOException {
//        String filePath = "D:/Epub/eventDictionnary.json";
//
//        StringBuilder fileData = new StringBuilder(1000);//Constructs a string buffer with no characters in it and the specified initial capacity
//        BufferedReader reader = new BufferedReader(new FileReader(filePath));
//
//        char[] buf = new char[1024];
//        int numRead = 0;
//        while ((numRead = reader.read(buf)) != -1) {
//            String readData = String.valueOf(buf, 0, numRead);
//            fileData.append(readData);
//            buf = new char[1024];
//        }
//
//        reader.close();
//
//        String returnStr = fileData.toString();
//        return returnStr;
//    }
}
