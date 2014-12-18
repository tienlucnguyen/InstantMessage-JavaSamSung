package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainView extends Application {

	private Stage primaryStage;

	public static void main(String[] args) {
		Application.launch(MainView.class, (java.lang.String[]) null);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		try {
			this.primaryStage = primaryStage;
			Parent page =  FXMLLoader.load(MainView.class
					.getResource("MainController.fxml"));
			Scene scene = new Scene(page);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("SIP SERVER");
			this.primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					//Platform.exit();
					//primaryStage.close();
					System.exit(0);
					
				}
			});

		} catch (Exception e) {
			Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null,
					e);

		}

	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

}
