package application;

import java.awt.event.TextEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import samsung.sip.message.HeaderField;
import samsung.sip.message.MessageBody;
import samsung.sip.message.SipMessage;
import samsung.sip.message.StatusLine;
import samsung.sip.url.AOR;
import samsung.sip.url.ContactAddress;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

public class SipServer extends Application {

	private int agent1Port = 1500;
	private int agent2Port = 1501;
	private Socket socketServer, socketAgent;
	private ServerSocket server;
	private SipMessage sipMessage;
	private SipMessage OKMessage, updateMessage;
	private String agent1IP, agent2IP;
	private AOR agent1AOR = new AOR("ngocson", "yahoo.com");
	private AOR agent2AOR = new AOR("tienluc", "yahoo.com");
	private ContactAddress agent1ContactAddress, agent2ContactAddress;
	private AOR serverAOR = new AOR("server", "yahoo.com");
	private ContactAddress serverContactAddress = new ContactAddress("server",
			"localhost");
	private Object[] user1Infor = new Object[] { 1, "ngocson@yahoo.com", null,
			"Offline", 0 };
	private Object[] user2Infor = new Object[] { 2, "ngocson@yahoo.com", null,
			"Offline", 0 };
	private final Object[] columnName = { "ID", "AOR", "Contact Address",
			"Status", "Offline Message" };
	private Object[][] rowData = new Object[][] { user1Infor, user2Infor };
	private ArrayList<SipMessage> user1Store = new ArrayList<SipMessage>();
	private ArrayList<SipMessage> user2Store = new ArrayList<SipMessage>();
	private int numberStore1;
	private int numberStore2;
	private TextArea textEvent;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource(
					"SipServer.fxml"));
			Scene scene = new Scene(root, 720, 400);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("SipServer");
			primaryStage.show();
			textEvent = (TextArea) scene.lookup("#textEvent");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runServer() throws IOException, ClassNotFoundException {

		while (true) {

			socketServer = server.accept();
			ObjectInputStream oi = new ObjectInputStream(
					socketServer.getInputStream());
			sipMessage = (SipMessage) oi.readObject();
			// if received Sip Message

			if (sipMessage != null) {

				sipMessage.getSipMessage();
				StatusLine statusLine = sipMessage.getStatusLine();
				HeaderField headerField = sipMessage.getHeaderField();
				MessageBody messageBody = sipMessage.getMessageBody();
				// Display SipMessage in taEvent
				textEvent.appendText(sipMessage.getSipMessage());
				// REGISTER Online
				if (messageBody.getContent().equals("Online")) {

					// If message from user1: ngocson@yahoo.com
					if (headerField.getFrom().getName().equals("ngocson")) {
						// update user1Infor
						user1Infor[2] = sipMessage.getHeaderField().getFrom()
								.getContactAddress();
						agent1IP = sipMessage.getHeaderField().getContact()
								.getAddress();
						agent1ContactAddress = new ContactAddress("ngocson",
								agent1IP);

						user1Infor[3] = "Online";
						// send200OK(agent1Port);
						// forwardSTORE("ngocson");
						// loadTable();
						// If message from user2: tienluc@yahoo.com
					} else if (headerField.getFrom().getName()
							.equals("tienluc")) {
						// update user2Infor
						user2Infor[2] = sipMessage.getHeaderField().getFrom()
								.getContactAddress();
						agent2IP = sipMessage.getHeaderField().getContact()
								.getAddress();
						agent2ContactAddress = new ContactAddress("tienluc",
								agent2IP);
						user2Infor[3] = "Online";
						// send200OK(agent2Port);
						// forwardSTORE("tienluc");
						// loadTable();

					}
					// REGISTER Offline
				} else {
					if (headerField.getFrom().getName().equals("ngocson")) {
						user1Infor[2] = null;
						agent1IP = null;
						// System.out.println(Agent1IP);
						agent1ContactAddress = new ContactAddress("ngocson",
								agent1IP);
						user1Infor[3] = "Offline";
						// loadTable();
						// Send updateMessage to tienluc@yahoo.com if tienluc is
						// online

					} else if (headerField.getFrom().getName()
							.equals("tienluc")) {
						user2Infor[2] = null;
						agent2IP = null;
						agent2ContactAddress = new ContactAddress("tienluc",
								agent2IP);
						user2Infor[3] = "Offline";
						// loadTable();
						// Send updateMessage to ngocson@yahoo.com if ngocson is
						// online

					}
				}
				// Receive INVITE
			} else if (sipMessage.getStatusLine().getRequestName()
					.equals("INVITE")) {
				// Neu la INVITE tu Agent1 (ngocson)
				if (sipMessage.getHeaderField().getFrom().getName()
						.equals("ngocson")) {
					// Neu tienluc@yahoo.com offline, send 200OK

					if (user2Infor[3].equals("Offline")) {
						// send200OK(Agent1Port);
					} else {
						// forwardMESSAGE("tienluc");
					}
					// Neu la INVITE tu Agent2 (tienluc)
				} else if (sipMessage.getHeaderField().getFrom().getName()
						.equals("tienluc")) {
					// Neu tienluc@yahoo.com offline, send 200OK
					if (user1Infor[3].equals("Offline")) {
						// send200OK(Agent2Port);
					} else {
						// forwardMESSAGE("ngocson");
					}
				}
			} // Receive BYE
			else if (sipMessage.getStatusLine().getRequestName().equals("BYE")) {
				if (sipMessage.getHeaderField().getFrom().getName()
						.equals("ngocson")) {
					// forwardMESSAGE("tienluc");
					// Neu la INVITE tu Agent2 (tienluc)
				} else if (sipMessage.getHeaderField().getFrom().getName()
						.equals("tienluc")) {
					// forwardMESSAGE("ngocson");

				}

			}
			// IF RECEIVER STORE MESSAGE
			else if (sipMessage.getType().equals("store")) {
				String storeTo = sipMessage.getHeaderField().getTo().getName();
				// If send to user1Infor: ngocson
				if (storeTo.equals("ngocson")) {
					user1Store.add(sipMessage);
					user1Infor[4] = user1Store.size();
					// loadTable();
				} else if (storeTo.equals("tienluc")) {
					user2Store.add(sipMessage);
					user2Infor[4] = user2Store.size();
					// oadTable();
				}

			} else if (sipMessage.getType().equals("response")) {
				if (sipMessage.getHeaderField().getFrom().getName()
						.equals("ngocson")) {
					// forwardMESSAGE("tienluc");
					// Neu la INVITE tu Agent2 (tienluc)
				} else if (sipMessage.getHeaderField().getFrom().getName()
						.equals("tienluc")) {
					// forwardMESSAGE("ngocson");

				}
			}

		}

	}

	public void send200OK(int port) {
		ObjectOutputStream OO = null;
		String from = sipMessage.getHeaderField().getFrom().getName();
		try {
			// Statusline: SIP 2.0 200 OK
			StatusLine statusLine = new StatusLine("SIP 2.0", 200, "OK");
			// Header Field: From: Server
			// From: Server
			// To: Agent
			// Via: Agent
			// Via Server
			// Contact: Server
			ContactAddress agentContactAddress = sipMessage.getHeaderField()
					.getFrom();
			HeaderField headerField = new HeaderField(serverContactAddress,
					agentContactAddress, agentContactAddress,
					serverContactAddress, serverContactAddress);
			// MessageBody:
			// Content-Length: 5
			// Content: 200OK
			//

			MessageBody mesageBody = new MessageBody(5, "200 OK");
			// OKMessage
			OKMessage = new SipMessage("response", statusLine, headerField,
					mesageBody);
			// Send OKMessage
			String agentIP = sipMessage.getHeaderField().getFrom().getAddress();
			socketAgent = new Socket(agentIP, port);
			OO = new ObjectOutputStream(socketAgent.getOutputStream());
			OO.writeObject(OKMessage);
			socketAgent.close();
			OO.close();
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		System.out.println("Sent 200 OK");
		// taEvent.append(OKMessage.getSipMessage());
		// taEvent.setCaretPosition(taEvent.getDocument().getLength());
	}

	public void sendUPDATE(AOR desAOR, ContactAddress desContactAddress,
			String Content, int port) {
		ObjectOutputStream updateObject = null;
		int length = 0;
		if (Content != null) {
			length = Content.length();

		}
		try {
			socketAgent = new Socket(desContactAddress.getAddress(), port);
			System.out.println(desContactAddress.getAddress());
			// StatusLine
			// UPDATE ngocson@yahoo.com SIP 2.0
			StatusLine statusLine = new StatusLine("UPDATE", desAOR, "SIP 2.0");
			// HeaderField
			// From:Server
			// To:desContactAddress
			// Via:Agent
			// Via: Server
			// Contact: server
			HeaderField headerField = new HeaderField(serverContactAddress,
					desContactAddress, desContactAddress, serverContactAddress,
					serverContactAddress);
			// MessageBody
			// Content-Length:
			// Content:
			MessageBody messageBody = new MessageBody(length, Content);
			// updateMessage
			updateMessage = new SipMessage("update", statusLine, headerField,
					messageBody);
			// Send updateMessage

			updateObject = new ObjectOutputStream(socketAgent.getOutputStream());
			updateObject.writeObject(updateMessage);
			System.out.println(updateMessage.getSipMessage());
			socketAgent.close();
			updateObject.close();

		} catch (IOException ex) {
			System.out.println(ex.toString());
		}

	}

	public void forwardSTORE(String To) {
		if (To.equals("auto")) {
			System.out.println(agent1IP);
			for (int i = 0; i < user1Store.size(); i++) {
				try {
					socketAgent = new Socket(agent1IP, agent1Port);
					ObjectOutputStream fowardObject = new ObjectOutputStream(
							socketAgent.getOutputStream());
					fowardObject.writeObject(user1Store.get(i));
					socketAgent.close();
					fowardObject.close();
				} catch (IOException ex) {

				}
			}
			user1Infor[4] = 0;
			user1Store.clear();
		} else if (To.equals("tienluc")) {
			// System.out.println(Agent2IP);
			for (int i = 0; i < user2Store.size(); i++) {
				try {
					socketAgent = new Socket(agent2IP, agent2Port);
					ObjectOutputStream fowardObject = new ObjectOutputStream(
							socketAgent.getOutputStream());
					fowardObject.writeObject(user2Store.get(i));
					socketAgent.close();
					fowardObject.close();
				} catch (IOException ex) {

				}
			}
			user2Infor[4] = 0;
			user2Store.clear();

		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
