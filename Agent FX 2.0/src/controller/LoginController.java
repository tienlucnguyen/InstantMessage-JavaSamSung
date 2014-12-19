package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.swing.JOptionPane;

import samsung.sip.message.HeaderField;
import samsung.sip.message.MessageBody;
import samsung.sip.message.SipMessage;
import samsung.sip.message.StatusLine;
import samsung.sip.url.AOR;
import samsung.sip.url.ContactAddress;

public class LoginController implements Initializable {
	@FXML
	TextField taAor;
	@FXML
	Parent mainRoot;

	// SERVER properties
	private final String serverIP = "192.168.137.105";
	private final int serverPort = 1992;
	private ContactAddress serverContactAddress = new ContactAddress("server",
			serverIP);
	private AOR serverAOR = new AOR("server", "yahoo.com");

	// MY properties
	private final int agentPort = 1993;
	private ContactAddress myContactAddress;
	private ServerSocket agentServer;
	private String myName;
	private String myHost;
	private AOR myAOR;
	private String myIP;
	private Socket socketToServer, socketFromServer;
	private int myID;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			agentServer = new ServerSocket(agentPort);
			new LoginRunning().start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class LoginRunning extends Thread {
		public void run() {
			loginRunning();
		}
	}

	public void onClickLogin(ActionEvent event) {
		sendREGISTER("online");
	}

	public void loginRunning() {
		while (true) {
			try {
				socketFromServer = agentServer.accept();
				ObjectInputStream OI = new ObjectInputStream(
						socketFromServer.getInputStream());
				SipMessage sipMessage = (SipMessage) OI.readObject();

				if (sipMessage != null) {
					if (sipMessage.getMessageBody().getContent()
							.equals("200 FAIL")) {
						JOptionPane.showMessageDialog(null,
								"Account isn't exist!!!");

					} else {
						myID = Integer.parseInt(sipMessage.getMessageBody()
								.getContent().split("=")[0]);
						System.out.println("myID = " + myID);
						showAgent();
						showOfflineDialog(sipMessage.getHeaderField().getTo()
								.getAOR());
						break;
					}

					OI.close();
					socketFromServer.close();
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

	public void showOfflineDialog(String aor) {
		FXMLLoader offlineDialogLoader = new FXMLLoader(
				LoginController.class.getResource("DialogOffline.fxml"));
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					Scene scene = new Scene(offlineDialogLoader.load());
					Stage offlineDialogStage = new Stage();
					offlineDialogStage.setScene(scene);
					offlineDialogStage.setTitle(aor);
					offlineDialogStage.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	public void showAgent() throws IOException {
		FXMLLoader agentLoader = new FXMLLoader(
				LoginController.class.getResource("Agent.fxml"));
		agentLoader.setControllerFactory(new Callback<Class<?>, Object>() {

			@Override
			public Object call(Class<?> cls) {
				if (cls == AgentController.class) {
					return new AgentController(taAor.getText(), myIP,
							agentServer, myID);
				} else {
					try {
						return cls.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}

			}
		});

		agentLoader.setController(null);
		agentLoader.setRoot(null);
		Parent agentRoot = agentLoader.load();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mainRoot.getScene().getWindow().hide();
				Scene scene = new Scene(agentRoot);
				Stage agentStage = new Stage();
				agentStage.initModality(Modality.APPLICATION_MODAL);
				// agentStage.initOwner(mainRoot.getScene().getWindow());
				// System.out.println(mainRoot.getScene().getWindow());
				agentStage.setScene(scene);
				agentStage.show();
				agentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						sendREGISTER("offline");
						System.exit(0);
					}
				});
			}
		});

	}

	public void sendREGISTER(String status) {
		ObjectOutputStream OO = null;
		try {
			myIP = Inet4Address.getLocalHost().getHostAddress();
			String[] splitAor = null;
			splitAor = taAor.getText().split("@");
			myName = splitAor[0];
			myHost = splitAor[1];
			myAOR = new AOR(myName, myHost);
			myContactAddress = new ContactAddress(myName, myIP);

			// generate registerMessage
			StatusLine statusLine = new StatusLine("REGISTER", serverAOR,
					"SIP 2.0");

			HeaderField headerField = new HeaderField(myAOR, serverAOR,
					myContactAddress, serverContactAddress, myContactAddress);

			MessageBody messageBody = new MessageBody(status.length(), status);

			SipMessage registerMessage = new SipMessage("request", statusLine,
					headerField, messageBody);

			System.out.println(registerMessage.getSipMessage());

			// send registerMessage
			socketToServer = new Socket(serverIP, serverPort);
			OO = new ObjectOutputStream(socketToServer.getOutputStream());
			OO.writeObject(registerMessage);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				OO.close();
				socketToServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}