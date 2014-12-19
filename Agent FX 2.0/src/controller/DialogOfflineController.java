package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import samsung.sip.message.SipMessage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class DialogOfflineController implements Initializable {
	@FXML
	TextArea taDialogOffline;

	private int offlinePort = 1899;
	private ServerSocket offlineServer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		taDialogOffline.textProperty().addListener(
				new ChangeListener<Object>() {

					@Override
					public void changed(
							ObservableValue<? extends Object> observable,
							Object oldValue, Object newValue) {
						// TODO Auto-generated method stub
						taDialogOffline.setScrollTop(Double.MAX_VALUE);
					}
				});

		try {
			offlineServer = new ServerSocket(offlinePort);
			new OfflineDialogRunning().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class OfflineDialogRunning extends Thread {
		public void run() {
			while (true) {
				try {
					Socket socketOffline = offlineServer.accept();
					ObjectInputStream OI = new ObjectInputStream(
							socketOffline.getInputStream());
					SipMessage sipMessage = (SipMessage) OI.readObject();

					if (sipMessage != null) {

						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								taDialogOffline.appendText("\n<"
										+ sipMessage.getHeaderField().getFrom()
												.getAOR()
										+ ">: "
										+ sipMessage.getMessageBody()
												.getContent());

							}
						});
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
