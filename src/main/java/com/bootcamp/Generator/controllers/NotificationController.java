package com.bootcamp.Generator.controllers;

import com.bootcamp.Generator.Classes.NotificationInput;
import com.bootcamp.entities.Notification;
import com.bootcamp.Generator.services.NotificationService;
import com.bootcamp.version.ApiVersions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestController("NotificationController")
@RequestMapping("/notifications")
@Api(value = "Notification API", description = "Notification API")
public class NotificationController {
    
    @Autowired
    NotificationService notificationService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Create a new notification", notes = "Create a new notification")
    public ResponseEntity<Boolean> checkEventAndgenerateNotification(NotificationInput input) throws FileNotFoundException, IOException, IOException {
        boolean result = false;
        HttpStatus httpStatus = null;
        
        try {
            result = notificationService.checkEventAndgenerateNotification(input);
            httpStatus = HttpStatus.OK;
        }catch (SQLException exception){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, httpStatus);
    }
    
    @RequestMapping(method = RequestMethod.PUT, value = "/")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Update a new notification", notes = "Update a new notification")
    public ResponseEntity<Notification> update(@RequestBody @Valid Notification notification) {

        HttpStatus httpStatus = null;

        try {
            notificationService.update(notification);
            httpStatus = HttpStatus.OK;
        }catch (SQLException exception){
            
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Notification>(notification, httpStatus);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Delete a notification", notes = "Delete a notification")
    public void delete(@PathVariable(name = "id") int id) {

        HttpStatus httpStatus = null;

        try {
            Notification notification = notificationService.delete(id);
            httpStatus = HttpStatus.OK;
        }catch (SQLException exception){
            
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Read a notification", notes = "Read a notification")
    public ResponseEntity<Notification> read(@PathVariable(name = "id") int id) {

        HttpStatus httpStatus = null;
        Notification notification = new Notification();
        try {
            notification = notificationService.read(id);
            httpStatus = HttpStatus.OK;
        }catch (SQLException exception){
            
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Notification>(notification, httpStatus);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Read a notification", notes = "Read a notification")
    public ResponseEntity<List<Notification>> read() {

        List<Notification> notifications = new ArrayList<Notification>();
        HttpStatus httpStatus = null;

        try {
            notifications = notificationService.read();
            httpStatus = HttpStatus.OK;
        }catch (SQLException e){           
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<List<Notification>>(notifications, httpStatus);
    }

}
