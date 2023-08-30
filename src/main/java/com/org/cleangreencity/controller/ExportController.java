package com.org.cleangreencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import static com.org.cleangreencity.controller.DashboardController.currentDashboard;

public class ExportController {



    public void exportToJSON(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(currentDashboard.getTitle()+".json"), currentDashboard);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("JSON data exported to dashboard.json");

    }
    public void exportToAnotherFormat(String jarFilePath ){

        if(jarFilePath!=null || currentDashboard!=null)
            try {
                URLClassLoader cl = new URLClassLoader(new URL[]{

                        new File(jarFilePath).toURI().toURL()});

                Method main = cl.loadClass("org.example.Main").getMethod("main", String[].class);
                main.invoke(null, new Object[]{new String[]{String.valueOf(currentDashboard.getDashboardId())}});
                cl.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }




}
