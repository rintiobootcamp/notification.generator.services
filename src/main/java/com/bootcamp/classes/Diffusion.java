/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootcamp.classes;

import com.bootcamp.commons.enums.CanalType;

/**
 *
 * @author edwigegédéon
 */
public class Diffusion {
    private String message;
    private CanalType canal;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CanalType getCanal() {
        return canal;
    }

    public void setCanal(CanalType canal) {
        this.canal = canal;
    }
    
    
}
