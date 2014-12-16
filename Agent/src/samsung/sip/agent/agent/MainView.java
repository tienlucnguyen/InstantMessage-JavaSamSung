package controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainView extends Application {

	private Stage primaryStage;

	public static void main(String[] args) {
		Application.launch(MainView.class, (java.lang.String[]) null);

	}
	
	@Override
	public void start(Stage primaryStage) {

		try {
			this.primaryStage = primaryStage;
			AnchorPane page = (AnchorPane) FXMLLoader.load(MainView.class
					.getResource("LogIn.fxml"));
			Scene scene = new Scene(page);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("SipMessenger");
			this.primaryStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
