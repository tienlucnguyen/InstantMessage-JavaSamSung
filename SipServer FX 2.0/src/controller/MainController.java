package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.server.SocketSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import samsung.sip.message.HeaderField;
import samsung.sip.message.MessageBody;
import samsung.sip.message.SipMessage;
import samsung.sip.message.StatusLine;
import samsung.sip.url.AOR;
import samsung.sip.url.ContactAddress;
import model.Agent;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController implements Initializable {

	// ===============================================================================
	// DEFINE FORM
	@FXML
	Parent mainRoot;
	@FXML
	TableView<Agent> tableID;
	@FXML
	TableColumn<Agent, Integer> iID;
	@FXML
	TableColumn<Agent, String> iAccount;
	@FXML
	TableColumn<Agent, String> iAddress;
	@FXML
	TableColumn<Agent, String> iState;
	@FXML
	TableColumn<Agent, Integer> iOfflineMessage;
	@FXML
	TextField accountInput;
	@FXML
	TextField addressInput;
	@FXML
	TextField stateInput;
	@FXML
	TextField offlineMessageInput;
	@FXML
	TextArea taSchedule;
	@FXML
	TabPane tabPane;

	// DEFINE VARIABLE

	// for connect database
	private final String url = "jdbc:mysql://localhost/sipserver_ver_2";
	private final String user = "auto";
	private final String password = "thuongkute";
	private static int iNum = 0;
	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;

	// table data
	final ObservableList<Agent> data = FXCollections.observableArrayList();

	// for implement protocol
	private ServerSocket SERVER;
	private Socket socketFromAgent, socketToAgent;
	private final int agentPort = 1993;
	private final String serverIP = "192.168.1.5";
	private final ContactAddress serverContactAddress = new ContactAddress(
			"server", serverIP);
	private final AOR serverAOR = new AOR("server", "yahoo.com");

	private SipMessage sipMessage;
	private SipMessage okMessage;
	private ObjectInputStream OI;
	private ObjectOutputStream OO;

	// =============================================================================
	// RUN WHEN FORM WAS LOADED
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*
		 * The first openning
		 */

		// Config ScrollBar for TextArea
		taSchedule.textProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				// TODO Auto-generated method stub
				taSchedule.setScrollTop(Double.MAX_VALUE);
			}
		});

		/*
		 * Load table data
		 */
		connectDatabase();
		loadTable();

		/*
		 * Set table value
		 */
		iID.setCellValueFactory(new PropertyValueFactory<Agent, Integer>("rID"));
		iAccount.setCellValueFactory(new PropertyValueFactory<Agent, String>(
				"rAccount"));
		iAddress.setCellValueFactory(new PropertyValueFactory<Agent, String>(
				"rAddress"));
		iState.setCellValueFactory(new PropertyValueFactory<Agent, String>(
				"rState"));

		iOfflineMessage
				.setCellValueFactory(new PropertyValueFactory<Agent, Integer>(
						"rOfflineMessage"));
		tableID.setItems(data);

		/*
		 * SERVER RUNNING
		 */
		try {
			SERVER = new ServerSocket(1992);
			new ServerRunning().start();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	class ServerRunning extends Thread {
		public void run() {
			serverRunning();
		}
	}

	private void loadTable() {
		// TODO Auto-generated method stub
		try {
			data.clear();
			iNum = 0;
			resultSet = statement.executeQuery("Select * from agent");
			while (resultSet.next()) {
				iNum++;
				int id = resultSet.getInt(1);
				String aor = resultSet.getString(2);
				String address = resultSet.getString(3);
				String state = resultSet.getString(4);
				int offlineMessage = resultSet.getInt(5);

				data.add(new Agent(id, aor, address, state, offlineMessage));
				// System.out.println("load");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void serverRunning() {
		while (true) {
			try {
				socketFromAgent = SERVER.accept();
				OI = new ObjectInputStream(socketFromAgent.getInputStream());
				sipMessage = (SipMessage) OI.readObject();

				/*
				 * process SipMessage 1. Show on Schedule, get properties of
				 * sipMessage 2. if is request message (REGISTER,INVITE,BYE,
				 * ...) -> response, 3. if is response message(...) -> process
				 */
				if (sipMessage != null) {

					// 1.
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							taSchedule.appendText(sipMessage.getSipMessage());
						}
					});

					System.out.println(sipMessage.getSipMessage());

					StatusLine statusLine = sipMessage.getStatusLine();
					HeaderField headerField = sipMessage.getHeaderField();
					MessageBody messageBody = sipMessage.getMessageBody();

					String agentAOR = headerField.getFrom().getName() + "@"
							+ headerField.getFrom().getHost();
					String agentIP = headerField.getContact().getAddress();

					// 2.
					// If sipMessage is request Message
					if (sipMessage.getType().equals("request")) {

						System.out.println("receive request");
						// If request is REGISTER
						// if REGISTER online
						if (statusLine.getRequestName().equals("REGISTER")) {

							System.out.println("receive REGISTER");
							if (messageBody.getContent().equals("online")) {

								// check database & send response 200
								resultSet = statement
										.executeQuery("SELECT id from agent WHERE aor='"
												+ agentAOR + "'");

								if (resultSet.next()) {

									send200(sipMessage, resultSet.getInt(1)+"=200 OK");
									statement
											.executeUpdate("UPDATE agent SET address='"
													+ agentIP
													+ "',state='online' WHERE id="
													+ resultSet.getInt(1)+"");
									loadTable();
								} else {

									send200(sipMessage, "200 FAIL");

								}
								// Update database

							}
							// if REGISTER offline
							else if (messageBody.getContent().equals("offline")) {
								resultSet = statement
										.executeQuery("SELECT aor from agent WHERE aor='"
												+ agentAOR + "'");

								if (resultSet.next()) {
									statement
											.executeUpdate("UPDATE agent SET address='null',state='offline' WHERE aor='"
													+ agentAOR + "'");
									loadTable();
								}

							}

						}

						// if request is INVITE message
						// if desAOR is online -> forward INVITE
						// if desAOR is offline -> send 200 OFFLINE
						// if desAOR is invalid -> send 200 FAIL
						if (statusLine.getRequestName().equals("INVITE")) {

							System.out.println("receive INVITE");

							String forwardStringAOR = headerField.getTo()
									.getName()
									+ "@"
									+ headerField.getTo().getHost();

							System.out.println(forwardStringAOR);

							resultSet = statement
									.executeQuery("SELECT * from agent WHERE aor='"
											+ forwardStringAOR + "'");
							if (resultSet.next()) {// if desAOR is valid

								System.out.println(resultSet.getString(4));

								if (resultSet.getString(4).equals("online")) {
									// forward
									String desIP = resultSet.getString(3);
									socketToAgent = new Socket(desIP, agentPort);
									ObjectOutputStream OO = new ObjectOutputStream(
											socketToAgent.getOutputStream());
									OO.writeObject(sipMessage);
									
									System.out.println("sendFORWARD");

								} else if (resultSet.getString(4).equals(
										"offline")) {
									// send 200 OFFLINE
									send200(sipMessage, resultSet.getInt(1)+"="+resultSet.getString(2)+"=OFFLINE");

								}
							}else{//if desAOR is invalid
								send200(sipMessage, "200 AOR FAIL");
							}
						}

					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void connectDatabase() {

		connect = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connect successfully!");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void onAddItem(ActionEvent event) throws SQLException {
		// Add the data any time
		iNum++;
		String aor = accountInput.getText();
		String address = addressInput.getText();
		String state = stateInput.getText();
		int offlineMessage = Integer.parseInt(offlineMessageInput.getText());

		// Insert data
		statement
				.executeUpdate("INSERT INTO agent VALUES(" + iNum + ",'" + aor
						+ "','" + address + "','" + state + "',"
						+ offlineMessage + ")");

		// Load table & clearForm
		loadTable();
		accountInput.clear();
		addressInput.clear();
		stateInput.clear();
		offlineMessageInput.clear();

	}

	public void send200(SipMessage sipMessage, String content) {
		try {
			// StatusLine: SIP 2.0 200 OK
			StatusLine statusLine = new StatusLine("SIP 2.0", 200, "OK");

			// Header Field: From: Server
			// From: ServerAOR
			// To : AgentAOR
			// Via : AgentContact
			// Via : ServerContact
			// Contact: ServerContact
			AOR agentAOR = sipMessage.getHeaderField().getFrom();
			ContactAddress agentContact = sipMessage.getHeaderField()
					.getContact();
			HeaderField headerField = new HeaderField(serverAOR, agentAOR,
					serverContactAddress, agentContact, serverContactAddress);

			// MessageBody:
			// Content-Length: 5
			// Content : content
			MessageBody messageBody = new MessageBody(content.length(), content);

			okMessage = new SipMessage("response", statusLine, headerField,
					messageBody);

			String agentIP = sipMessage.getHeaderField().getContact()
					.getAddress();

			socketToAgent = new Socket(agentIP, agentPort);
			OO = new ObjectOutputStream(socketToAgent.getOutputStream());
			OO.writeObject(okMessage);

			socketToAgent.close();
			OO.close();

			System.out.println("send: " + content);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
