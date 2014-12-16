package controller;

import java.awt.SecondaryLoop;
import java.awt.TextField;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class LoginController implements Initializable {
	@FXML
	javafx.scene.control.TextField taAor;

	// SERVER properties
	private String serverIP = "localhost";
	private ContactAddress serverContactAddress = new ContactAddress("server",
			serverIP);
	private AOR serverAOR = new AOR("server", "yahoo.com");
	// MY properties
	private ContactAddress myContactAddress;
	private ServerSocket agentServer;
	private String myName;
	private String myHost;
	private AOR myAOR;
	private String myIP;
	private Socket socketToServer;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		

	}

	public void onClickLogin(ActionEvent event){
		sendREGISTER("online");
	}
	
	public void sendREGISTER(String status) {
		ObjectOutputStream OO = null;
		try {
			myIP = Inet4Address.getLocalHost().getHostAddress();			
			String [] splitAor = null;
			splitAor =taAor.getText().split("@");
			myName = splitAor[0];
			myHost = splitAor[1];
			myAOR = new AOR(myName,myHost);
			myContactAddress = new ContactAddress(myName, myIP);

			// generate registerMessage
			StatusLine statusLine = new StatusLine("REGISTER", serverAOR,
					"SIP 2.0");

			HeaderField headerField = new HeaderField(myAOR,
					serverAOR, myContactAddress,
					serverContactAddress, myContactAddress);
			
			MessageBody messageBody = new MessageBody(status.length(), status);
			
			 SipMessage registerMessage = new SipMessage("request",
			 statusLine, headerField, messageBody);
		
			 System.out.println(registerMessage.getSipMessage());
			 
			 //send registerMessage
			 socketToServer = new Socket(serverIP,1992);
			 OO = new ObjectOutputStream(socketToServer.getOutputStream());
			 OO.writeObject(registerMessage);
			 

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
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

