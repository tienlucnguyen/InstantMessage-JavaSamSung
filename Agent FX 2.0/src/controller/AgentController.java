package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import samsung.sip.message.HeaderField;
import samsung.sip.message.MessageBody;
import samsung.sip.message.SipMessage;
import samsung.sip.message.StatusLine;
import samsung.sip.url.AOR;
import samsung.sip.url.ContactAddress;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class AgentController implements Initializable {
	// FORM
	@FXML
	Label lbMyAOR;
	@FXML
	TextField tfAddAOR;
	@FXML
	ListView<String> lvContact;
	@FXML
	TextArea taSchedule;

	// Properties of Agent

	private ServerSocket agentServer;
	private String myStringAOR;
	private AOR myAOR;
	private ContactAddress myContactAddress;
	private String myIP;
	private String myName;
	private String myHost;
	private int agentPort = 1993;

	private int myID;

	// Properties of SERVER
	private final String serverIP = "192.168.137.105";
	private final int serverPort = 1992;
	private ContactAddress serverContactAddress = new ContactAddress("server",
			serverIP);
	private AOR serverAOR = new AOR("server", "yahoo.com");

	// Variables
	ObservableList<String> listAOR = FXCollections.observableArrayList();
	private final String[] list = { "son@yahoo.com", "luc@yahoo.com",
			"quan@yahoo.com", "khang@yahoo.com" };

	public AgentController(String myAOR, String myIP, ServerSocket agentServer,
			int myID) {
		this.myStringAOR = myAOR;
		this.myIP = myIP;
		this.myID = myID;
		this.agentServer = agentServer;
		String[] split = myAOR.split("@");
		this.myName = split[0];
		this.myHost = split[1];
		this.myAOR = new AOR(myName, myHost);
		this.myContactAddress = new ContactAddress(myName, myIP);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Show Properties of Agent
		taSchedule.textProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				// TODO Auto-generated method stub
				taSchedule.setScrollTop(Double.MAX_VALUE);
			}
		});

		lbMyAOR.setText(" sip: " + this.myStringAOR);

		System.out.println(this.myIP);
		System.out.println(this.myID);
		System.out.println(this.agentServer);

		for (int i = 0; i < list.length; i++) {
			if (!list[i].equals(this.myStringAOR)) {
				listAOR.add(list[i]);
			}
		}

		new AgentRunning().start();

		lvContact.setItems(listAOR);

		// Agent Running

		// When double click into a ListView
		lvContact.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					String desAOR = lvContact.getSelectionModel()
							.getSelectedItem();
					try {
						sendINVITE(desAOR);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(desAOR);
				}

			}
		});

	}

	class AgentRunning extends Thread {
		public void run() {
			try {
				agentRunning();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Signout method
	public void onSignout(ActionEvent e) {
		sendREGISTER("offline");
		// System.out.println("sendOFFLINE");
		System.exit(0);
	}

	/*
	 * 1. Agent listen request, response from SERVER and other agent 1.1. Listen
	 * INVITE,200 FAIL(-desAOR fail), 200 OFFLINE (-desAOR offline) message from
	 * SERVER forward. 1.2. Listen 200 OK (-desAOR online) from desAgent. 1.3
	 * Listen offlineMessage forwarded by SERVER
	 */
	public void agentRunning() throws IOException {
		while (true) {
			Socket socketFrom = null;
			ObjectInputStream OI = null;
			try {
				socketFrom = agentServer.accept();
				OI = new ObjectInputStream(socketFrom.getInputStream());
				SipMessage sipMessage = (SipMessage) OI.readObject();

				if (sipMessage != null) {

					// Display
					System.out.println(sipMessage.getSipMessage());
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							taSchedule.appendText(sipMessage.getSipMessage());

						}
					});

					String messageType = sipMessage.getType();
					StatusLine statusLine = sipMessage.getStatusLine();
					HeaderField headerField = sipMessage.getHeaderField();
					MessageBody messageBody = sipMessage.getMessageBody();

					// received INVITE message -> send200 OK -> showDialog
					switch (messageType) {
					case "request":
						if (statusLine.getRequestName().equals("INVITE")) {
							String desStringAOR = headerField.getFrom()
									.getName()
									+ "@"
									+ headerField.getFrom().getHost();
							String desIP = headerField.getContact()
									.getAddress();
							int desID = Integer.parseInt(messageBody
									.getContent().split("=")[0]);
							send200(sipMessage, myID + "=200 OK");
							showDialog(desStringAOR, desIP, desID);

						}
						break;

					case "response":
						// receive 200 OK message -> showDialog
						if (messageBody.getContentLength() < 10) {
							String desStringAOR = headerField.getFrom()
									.getName()
									+ "@"
									+ headerField.getFrom().getHost();
							String desIP = headerField.getContact()
									.getAddress();

							int desID = Integer.parseInt(messageBody
									.getContent().split("=")[0]);
							showDialog(desStringAOR, desIP, desID);

						} else if (messageBody.getContent().equals("200 AOR FAIL")) {
							JOptionPane.showMessageDialog(null,
									"Account isn't exist!!!");
						} else {
							String desStringAOR = messageBody.getContent()
									.split("=")[1];
							System.out.println(desStringAOR);
							String desIP = "null";
							int desID = Integer.parseInt(messageBody
									.getContent().split("=")[0]);
							showDialog(desStringAOR, desIP, desID);
						}
						break;
					default:
						break;
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				socketFrom.close();
				OI.close();
			}
		}

	}

	public void send200(SipMessage sipMessage, String content)
			throws IOException {
		Socket socketToDesAgent = null;
		ObjectOutputStream OO = null;
		try {
			// StatusLine: SIP 2.0 200 OK
			StatusLine statusLine = new StatusLine("SIP 2.0", 200, "OK");

			// Header Field: From: Server
			// From: myAOR
			// To : desAOR
			// Via : myContact
			// Via : serverContact
			// Contact: myContact
			AOR desAOR = sipMessage.getHeaderField().getFrom();
			ContactAddress desContact = sipMessage.getHeaderField()
					.getContact();
			HeaderField headerField = new HeaderField(myAOR, desAOR,
					myContactAddress, serverContactAddress, myContactAddress);

			// MessageBody:
			// Content-Length: 5
			// Content : content
			MessageBody messageBody = new MessageBody(content.length(), content);

			SipMessage okMessage = new SipMessage("response", statusLine,
					headerField, messageBody);

			String desIP = desContact.getAddress();

			socketToDesAgent = new Socket(desIP, agentPort);
			OO = new ObjectOutputStream(socketToDesAgent.getOutputStream());
			OO.writeObject(okMessage);

			// Display
			System.out.println("send" + content);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			socketToDesAgent.close();
			OO.close();
		}

	}

	public void sendREGISTER(String status) {
		ObjectOutputStream OO = null;
		Socket socketToServer = null;
		try {
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

	public void sendINVITE(String desStringAor) throws IOException {

		String desName = desStringAor.split("@")[0];
		String desHost = desStringAor.split("@")[1];

		AOR desAOR = new AOR(desName, desHost);

		/*
		 * Generate INVITE Message
		 */

		// Status Line
		// INVITE desAOR SIP 2.0
		StatusLine statusLine = new StatusLine("INVITE", desAOR, "SIP 2.0");

		// HeaderField
		// From: myAOR
		// To: desAOR
		// Via: myContact
		// Via: serverContact
		// Contact: myContact
		HeaderField headerField = new HeaderField(myAOR, desAOR,
				myContactAddress, serverContactAddress, myContactAddress);

		// MessageBody
		String content = myID + "=INVITE";
		MessageBody messageBody = new MessageBody(content.length(), content);

		SipMessage inviteMessage = new SipMessage("request", statusLine,
				headerField, messageBody);

		/*
		 * Send to server
		 */

		Socket socketToServer = null;
		ObjectOutputStream OO = null;

		try {
			socketToServer = new Socket(serverIP, serverPort);
			OO = new ObjectOutputStream(socketToServer.getOutputStream());
			OO.writeObject(inviteMessage);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			socketToServer.close();
			OO.close();
		}

	}

	public void showDialog(String desStringAOR, String desIP, int desID)
			throws IOException {
		FXMLLoader dialogLoader = new FXMLLoader(
				AgentController.class.getResource("Dialog.fxml"));
		dialogLoader.setControllerFactory(new Callback<Class<?>, Object>() {

			@Override
			public Object call(Class<?> cls) {

				if (cls == DialogController.class) {
					return new DialogController(myStringAOR, myContactAddress,
							desStringAOR, desIP, myID, desID);
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

		dialogLoader.setController(null);
		dialogLoader.setRoot(null);
		Parent dialogRoot = dialogLoader.load();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				Scene scene = new Scene(dialogRoot);
				Stage dialogStage = new Stage();
				dialogStage.setScene(scene);
				dialogStage.show();
//				dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//					@Override
//					public void handle(WindowEvent event) {
//						System.out.println("Close Dialog");
//					}
//				});
			}
		});
	}

	public void onAddItem(ActionEvent e) {
		listAOR.add(tfAddAOR.getText());
		tfAddAOR.clear();
	}
}
