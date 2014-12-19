package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import samsung.sip.message.HeaderField;
import samsung.sip.message.MessageBody;
import samsung.sip.message.SipMessage;
import samsung.sip.message.StatusLine;
import samsung.sip.url.AOR;
import samsung.sip.url.ContactAddress;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DialogController implements Initializable {
	/*
	 * Define FORM Define Variables Define some characteristic of Dialog
	 */
	@FXML
	TextArea taDialog;
	@FXML
	TextArea taSend;
	@FXML
	TextArea taSchedule;
	@FXML
	RadioButton rdState;
	@FXML
	Tab tabDesAOR;

	// Properties of SERVER
	private final String serverIP = "192.168.1.5";
	private final int serverPort = 1992;
	private ContactAddress serverContactAddress = new ContactAddress("server",
			serverIP);
	private AOR serverAOR = new AOR("server", "yahoo.com");

	private final int agentPort = 1993;
	// Properties of dialog
	private String desStringAOR, myStringAOR;
	private AOR desAOR, myAOR;
	private String desState, desIP;
	private ContactAddress myContactAddress, desContacAddress;
	private ServerSocket dialogServer;
	private int myPort,desPort;

	// Constructor
	public DialogController(String myStringAOR,
			ContactAddress myContactAddress, String desStringAOR, String desIP,
			int myID, int desID) {
		
		this.myStringAOR = myStringAOR;
		this.desStringAOR = desStringAOR;

		this.myAOR = new AOR(myStringAOR.split("@")[0],
				myStringAOR.split("@")[1]);
		this.desAOR = new AOR(desStringAOR.split("@")[0],
				desStringAOR.split("@")[1]);
		this.desIP = desIP;
		this.myPort = agentPort + myID + desID;
		this.desPort = agentPort + myID + desID;

		if (desIP.equals("null")) {
			this.desState = "offline";
		} else {
			this.desState = "online";
		}

		this.myContactAddress = myContactAddress;
		this.desContacAddress = new ContactAddress(desStringAOR.split("@")[0],
				desIP);
		try {
			this.dialogServer = new ServerSocket(myPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle) METHOD run when form was loaded
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		taSchedule.textProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				// TODO Auto-generated method stub
				taSchedule.setScrollTop(Double.MAX_VALUE);
			}
		});

		taDialog.textProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				// TODO Auto-generated method stub
				taSchedule.setScrollTop(Double.MAX_VALUE);
			}
		});

		tabDesAOR.setText(this.desStringAOR);
		rdState.setText(this.desState);

		System.out.println("desIP " + this.desIP);

		new DialogRunning().start();

	}

	class DialogRunning extends Thread {
		public void run() {
			dialogRunning();
		}
	}

	public void dialogRunning() {
		while (true) {
			Socket socketToAgent = null;
			ObjectInputStream OI = null;
			try {
				socketToAgent = dialogServer.accept();
				OI = new ObjectInputStream(
						socketToAgent.getInputStream());
				SipMessage sipMessage = (SipMessage) OI.readObject();

				/*
				 * If dialog receive SipMessage "online" MESSAGE message ->
				 * display BYE Message -> close Dialog
				 */
				if (sipMessage != null) {

					System.out.println("receiver SIP MESSAGE");

					String messageType = sipMessage.getType();
					StatusLine statusLine = sipMessage.getStatusLine();
					HeaderField headerField = sipMessage.getHeaderField();
					MessageBody messageBody = sipMessage.getMessageBody();
					//
					if (sipMessage.getType().equals("online")) {

						System.out.println("receive MESSAGE MESSAGE");
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								taSchedule.appendText("\n"
										+ sipMessage.getSipMessage());
								taDialog.appendText("\n<" + desStringAOR
										+ ">: " + messageBody.getContent());

							}
						});

					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					socketToAgent.close();
					OI.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		}

	}

	/*
	 * Send MESSAGE message type: online ~ desAgent online -> send to desAgent
	 * type: offline ~ desAgent offline -> send to SERVER to save
	 */
	public void sendMESSAGE(String type, String content) {
		if (type.equals("online")) {

			// Status Line: INVITE desAOR SIP 2.0
			StatusLine statusLine = new StatusLine("MESSAGE", desAOR, "SIP 2.0");

			// HeaderField
			// From: myAOR
			// To : desAOR
			// Via: myContactAddress
			// Via: desContactAddress
			// Contact: myContactAddress
			HeaderField headerField = new HeaderField(myAOR, desAOR,
					myContactAddress, desContacAddress, myContactAddress);

			// MessageBody
			MessageBody messageBody = new MessageBody(content.length(), content);

			SipMessage messageMessage = new SipMessage(type, statusLine,
					headerField, messageBody);

			Socket socketToAgent = null;
			ObjectOutputStream OO = null;
			try {
				socketToAgent = new Socket(desIP, desPort);
			    OO = new ObjectOutputStream(
						socketToAgent.getOutputStream());
				OO.writeObject(messageMessage);

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						taSchedule.appendText("\n"
								+ messageMessage.getSipMessage());
						taDialog.appendText("\n<" + myStringAOR + ">: "
								+ messageBody.getContent());

					}
				});

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					socketToAgent.close();
					OO.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} else if (type.equals("offline")) {
			// Status Line: INVITE desAOR SIP 2.0
			StatusLine statusLine = new StatusLine("MESSAGE", desAOR, "SIP 2.0");

			// HeaderField
			// From: myAOR
			// To : desAOR
			// Via: myContactAddress
			// Via: desContactAddress
			// Contact: myContactAddress
			HeaderField headerField = new HeaderField(myAOR, desAOR,
					myContactAddress, serverContactAddress, myContactAddress);

			// MessageBody
			MessageBody messageBody = new MessageBody(content.length(), content);

			SipMessage messageMessage = new SipMessage(type, statusLine,
					headerField, messageBody);

			Socket socketToServer = null;
			ObjectOutputStream OO = null;
			try {
			    socketToServer = new Socket(serverIP, serverPort);
				 OO = new ObjectOutputStream(
						socketToServer.getOutputStream());
				OO.writeObject(messageMessage);

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						taSchedule.appendText("\n"
								+ messageMessage.getSipMessage());
						taDialog.appendText("\n<" + myStringAOR + ">: "
								+ messageBody.getContent());

					}
				});

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onSend(ActionEvent e) {
		if (!taSend.getText().equals(null)) {
			if (desState.equals("online")) {
				sendMESSAGE("online", taSend.getText());
			} else {
				sendMESSAGE("offline", taSend.getText());
			}

			taSend.clear();
		}

	}

	public void onPressEnter(KeyEvent key) {
		if (key.getCode() == KeyCode.ENTER) {
			if (desState.equals("online")) {
				sendMESSAGE("online", taSend.getText());
			} else {
				sendMESSAGE("offline", taSend.getText());
			}
		}
	}
}
