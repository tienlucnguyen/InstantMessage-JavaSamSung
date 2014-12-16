package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
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
import model.Agent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class MainController implements Initializable {

	// ===============================================================================
	// DEFINE FORM
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
	private Socket serverSocket, socketToAgent;

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
			statement = connect.createStatement();
			resultSet = statement.executeQuery("Select * from agent");
			while (resultSet.next()) {
				iNum++;
				int id = resultSet.getInt(1);
				String aor = resultSet.getString(2);
				String address = resultSet.getString(3);
				String state = resultSet.getString(4);
				int offlineMessage = resultSet.getInt(5);

				data.add(new Agent(id, aor, address, state, offlineMessage));
				
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void serverRunning() {
		while (true) {
			try {
				serverSocket = SERVER.accept();
				ObjectInputStream OI = new ObjectInputStream(
						serverSocket.getInputStream());
				SipMessage sipMessage = (SipMessage) OI.readObject();

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
							// if you change the UI, do it here !
							taSchedule.appendText(sipMessage.getSipMessage());
						}
					});
					System.out.println(sipMessage.getSipMessage());

					StatusLine statusLine = sipMessage.getStatusLine();
					HeaderField headerField = sipMessage.getHeaderField();
					MessageBody messageBody = sipMessage.getMessageBody();

					String agentAOR = headerField.getFrom().getName() + "@"
							+ headerField.getFrom().getHost();

					// 2.
					// If sipMessage is request Message
					if (sipMessage.getType().equals("request")) {
						// System.out.println("request");
						// If request is REGISTER
						// if REGISTER online
						if (statusLine.getRequestName().equals("REGISTER")) {

							if (messageBody.getContent().equals("online")) {

								// Update database

								String sql = "UPDATE agent SET state='online' WHERE aor='"
										+ agentAOR + "'";
								statement.executeUpdate(sql);
								// statement.executeUpdate("INSERT INTO agent VALUE(7,'auto@yahoo.com','null','offline',0) ");
								loadTable();

							}
							// if REGISTER offline
							else {

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
		clearForm();

	}

	private void clearForm() {
		// TODO Auto-generated method stub
		accountInput.clear();
		addressInput.clear();
		stateInput.clear();
		offlineMessageInput.clear();
	}

}
