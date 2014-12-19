package controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainView extends Application {


	public static void main(String[] args) {
		//Application.launch(MainView.class, (java.lang.String[]) null);
		launch(args);

	}
	
	@Override
	public void start(Stage primaryStage) {

		try {
			Parent root = FXMLLoader.load(MainView.class
					.getResource("LogIn.fxml"));
			Scene scene = new Scene(root,349,224);
			primaryStage.setScene(scene);
			primaryStage.setTitle("SipMessenger");
			primaryStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
