package application;

import java.net.URL;
import java.util.ResourceBundle;

import model.Agent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController implements Initializable {

	// DEFINE TABLE
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

	// DEFINE VARIABLE
	private int iNum = 1;

	

	// create table data
	final ObservableList<Agent> data = FXCollections.observableArrayList(
			new Agent(iNum++, "auto@yahoo.com", "192.168.1.1", "Offline",1), new Agent(
					iNum++, "son@yahoo.com", "192.168.2.1", "Offline",2), new Agent(
					iNum++, "luc@yahoo.com", "192.168.3.3", "Offline",2));

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		iID.setCellValueFactory(new PropertyValueFactory<Agent, Integer>("rID"));
		iAccount.setCellValueFactory(new PropertyValueFactory<Agent, String>(
				"rAccount"));
		iAddress.setCellValueFactory(new PropertyValueFactory<Agent, String>(
				"rAddress"));
		iState.setCellValueFactory(new PropertyValueFactory<Agent, String>(
				"rState"));

		iOfflineMessage.setCellValueFactory(new PropertyValueFactory<Agent, Integer>("rOfflineMessage"));
		tableID.setItems(data);

	}
	public void onAddItem(ActionEvent event){
		//Add the data any time
		Agent entry = new Agent(iNum, accountInput.getText(), addressInput.getText(),
				stateInput.getText(), Integer.parseInt(offlineMessageInput.getText()));
		iNum++;
		
		//Insert data
		data.add(entry);
		
		//clear form
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
