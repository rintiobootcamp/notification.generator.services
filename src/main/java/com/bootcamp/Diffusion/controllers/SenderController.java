package com.bootcamp.Diffusion.controllers;

import com.bootcamp.Diffusion.services.SenderService;
import com.bootcamp.version.ApiVersions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("SenderController")
@RequestMapping("/send")
@Api(value = "Diffusion API", description = "Diffusion API")
public class SenderController {
    @Autowired
    SenderService senderService;
    
    @RequestMapping(method = RequestMethod.GET)
    @ApiVersions({"1.0"})
    @ApiOperation(value = "Diffuse a message", notes = "Diffuse a message")
    public void send() {
        HttpStatus httpStatus = null;
                try {
                senderService.senderTimer();
        } catch (Exception e) {
        }
               
             httpStatus = HttpStatus.OK;

    }

}
