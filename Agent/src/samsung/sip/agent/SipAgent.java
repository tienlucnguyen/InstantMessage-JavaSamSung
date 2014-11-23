package samsung.sip.agent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

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

public class SipAgent extends Application {

	private Socket socketRegister, socketOK, socketBye;
	private Socket socketChat, socketInvite;
	private String myIP;
	private String desIP = null;
	private int myPort = 1500;
	private int desPort = 1501;
	private String serverIP = "localhost";
	private AOR serverAOR = new AOR("server", "yahoo.com");
	private AOR desAOR = new AOR("tienluc", "yahoo.com");
	private ContactAddress serverContactAddress = new ContactAddress("server",
			serverIP);
	private ContactAddress MyContactAddress;
	private ContactAddress DesContactAddress;
	private SipMessage REGISTERMessage, CHATMessage, STOREMessage,
			INVITEMessage, OKMessage, BYEMessage;
	private ServerSocket serverAgent;
	private Socket socketAgent;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Agent.fxml"));
			Scene scene = new Scene(root, 719, 536);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Sip Agent");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void sendREGISTER(String status) {
		ObjectOutputStream OO = null;

		try {
			myIP = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {

			System.out.println(ex.toString());

		}

		MyContactAddress = new ContactAddress("ngocson", myIP);
		try {
			socketRegister = new Socket(serverIP, 1992);
			// Creat StatusLine
			StatusLine statusLine = new StatusLine("REGISTER", serverAOR,
					"SIP 2.0");
			// Creat HeaderField
			HeaderField headerField = new HeaderField(MyContactAddress,
					serverContactAddress, MyContactAddress,
					serverContactAddress, MyContactAddress);
			// Creat MessageBody
			int length = status.length();
			String Content = status;
			MessageBody messageBody = new MessageBody(length, Content);
			// Message
			REGISTERMessage = new SipMessage("request", statusLine,
					headerField, messageBody);
			// taEvent.append(REGISTERMessage.getSipMessage());
			// taEvent.setCaretPosition(taEvent.getDocument().getLength());
			OO = new ObjectOutputStream(socketRegister.getOutputStream());
			OO.writeObject(REGISTERMessage);
		} catch (IOException ex) {

			System.out.println(ex.toString());

		} finally {
			try {
				OO.close();
				socketRegister.close();
			} catch (IOException ex) {
				System.out.println(ex.toString());
			}
		}

	}

	public void sendINVITE() {
		try {
			// Creat INVITE Message
			// StatusLine: INVITE tienluc@yahoo.com SIP 2.0
			StatusLine statusLine = new StatusLine("INVITE", desAOR, "SIP 2.0");
			// HeaderField:
			// From: ngocson@myIP
			// To: tienluc@desIP
			// Via: Server
			// Via: ngocson@myIP
			// Contact: ngocson@myIP
			DesContactAddress = new ContactAddress("tienluc", desIP);
			HeaderField headerField = new HeaderField(MyContactAddress,
					DesContactAddress, serverContactAddress, MyContactAddress,
					MyContactAddress);
			// MessageBody:
			// Content-Length: 6
			// Content: INVITE
			MessageBody messageBody = new MessageBody(5, "INVITE");
			INVITEMessage = new SipMessage("request", statusLine, headerField,
					messageBody);

			// Send to server
			socketInvite = new Socket(serverIP, 1992);
			ObjectOutputStream inviteObject = new ObjectOutputStream(
					socketInvite.getOutputStream());
			inviteObject.writeObject(INVITEMessage);

			// taEvent.append(INVITEMessage.getSipMessage());
			// taEvent.setCaretPosition(taEvent.getDocument().getLength());
			socketInvite.close();
			inviteObject.close();
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
