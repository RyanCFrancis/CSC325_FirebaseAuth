package com.example.csc325_firebase_webview_auth.view;

import javafx.fxml.FXML;

import java.io.IOException;

public class SplashController {
    @FXML
    public void Start() throws IOException{
        App.setRoot("/files/MainMenu.fxml");
        //System.out.println("gds");
    }
}
