/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootcamp;

import com.bootcamp.Classes.NotificationInput;
import com.bootcamp.services.NotificationService;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author edwigegédéon
 */
public class NotificationPrimitiveTest {
    NotificationService notificationService = new NotificationService();
    @Test
    public void generateNotificationTest() throws FileNotFoundException, SQLException{
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
}
