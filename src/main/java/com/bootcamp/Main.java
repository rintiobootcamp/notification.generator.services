/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootcamp;

import com.bootcamp.Classes.NotificationGn;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Bello
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Hello World");
        String chaine = readFileToString();
        Gson gson = new Gson();
        List<NotificationGn> notificationgns = gson.fromJson(chaine, List.class);
        
        System.out.println(notificationgns);
    }

    public static String readFileToString() throws IOException {
        String filePath = "D:/Epub/eventDictionnary.json";

        StringBuilder fileData = new StringBuilder(1000);//Constructs a string buffer with no characters in it and the specified initial capacity
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        String returnStr = fileData.toString();
        return returnStr;
    }

}
