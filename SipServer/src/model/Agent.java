package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Agent {

	private final SimpleIntegerProperty rId;
	private final SimpleStringProperty rAccount;
	private final SimpleStringProperty rAddress;
	private final SimpleStringProperty rState;
	private final SimpleIntegerProperty rOfflineMessage;

	public Agent(int sID, String sAccount, String sAddress, String sState, int sOfflineMessage) {
		this.rId = new SimpleIntegerProperty(sID);
		this.rAccount = new SimpleStringProperty(sAccount);
		this.rAddress = new SimpleStringProperty(sAddress);
		this.rState = new SimpleStringProperty(sState);
		this.rOfflineMessage = new SimpleIntegerProperty(sOfflineMessage);

	}

	// Getter and Setter
	public Integer getRID() {
		return rId.get();
	}

	public void setRID(Integer v) {
		rId.set(v);
	}

	public String getRAccount() {
		return rAccount.get();
	}

	public void setRAccount(String v) {
		rAccount.set(v);
	}

	public String getRAddress() {
		return rAddress.get();
	}

	public void setRAddress(String v) {
		rAddress.set(v);
	}

	public String getRState() {
		return rState.get();
	}

	public void setRPrice(String v) {
		rState.set(v);
	}

	public Integer getROfflineMessage() {
		return rOfflineMessage.get();
	}

	public void setROfflineMessage(Integer v) {
		rOfflineMessage.set(v);
	}
}
