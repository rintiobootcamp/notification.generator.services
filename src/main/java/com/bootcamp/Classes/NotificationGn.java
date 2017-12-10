/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootcamp.Classes;

import java.util.List;

/**
 *
 * @author edwigegédéon
 */
public class NotificationGn {
   private String libelle;
   private String action;
   private boolean gen_event; 
   
   private List<Diffusion> diffusions;

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isGen_event() {
        return gen_event;
    }

    public void setGen_event(boolean gen_event) {
        this.gen_event = gen_event;
    }

    public List<Diffusion> getDiffusions() {
        return diffusions;
    }

    public void setDiffusions(List<Diffusion> diffusions) {
        this.diffusions = diffusions;
    }
   
   
}
