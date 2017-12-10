package com.bootcamp.controllers;

import com.bootcamp.commons.exceptions.DatabaseException;
import com.bootcamp.entities.Notification;
import com.bootcamp.security.JwtAuthentification;
import com.bootcamp.services.NotificationService;
import com.bootcamp.version.ApiVersions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;


@RestController("NotificationController")
@RequestMapping("/notifications")
@Api(value = "Notification API", description = "Notification API")
public class NotificationController {
    
    @Autowired
    NotificationService notificationService;
    @Autowired
    HttpServletRequest request;

    @RequestMapping(method = RequestMethod.POST)
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Create a new notification", notes = "Create a new notification")
    public ResponseEntity<Boolean> checkEventAndgenerateNotification(@RequestParam("object") MultipartFile file) {

        HttpStatus httpStatus = null;
        
        try {
            notificationService.create(notification);
            httpStatus = HttpStatus.OK;
        }catch (SQLException exception){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Notification>(notification, httpStatus);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    @ApiVersions({"1.0"})
    @ApiOperation(value = "login", notes = "login")
    public String authentification(@RequestBody @Valid Notification notification) throws SQLException {

        HttpStatus httpStatus = null;
        String token = "";
        
        if(notificationService.getByLoginAndPwd(notification.getId(), notification.getLogin())){
            token = JwtAuthentification.addAuthentication(notification);
            httpStatus = HttpStatus.OK;        
        }

        return token;
    }
        //  THOSES METHODES ARE USELESS FOR THE MOMENT BY THEY WORK 
/*
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

    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Read a notification", notes = "Read a notification")
    public ResponseEntity<Notification> read() {

        Notification notification = new Notification();
        HttpStatus httpStatus = null;

        try {
            List<Notification> notifications = notificationService.read(request);
            httpStatus = HttpStatus.OK;
        }catch (SQLException | IllegalAccessException | DatabaseException | InvocationTargetException exception){
            
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Notification>(notification, httpStatus);
    }

   */
}
