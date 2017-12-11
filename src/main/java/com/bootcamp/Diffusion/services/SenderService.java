package com.bootcamp.Diffusion.services;

import com.bootcamp.Tasks.SenderTask;
import com.bootcamp.commons.constants.DatabaseConstants;
import java.util.Timer;
import org.springframework.stereotype.Component;



/**
 * Created by Bignon on 11/27/17.
 */
@Component
public class SenderService implements DatabaseConstants {
   public void senderTimer(){
        Timer timer;
        timer = new Timer();
        timer.schedule(new SenderTask(), 1000, 5000);   
 }
}
