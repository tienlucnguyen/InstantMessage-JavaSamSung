package samsung.sip.message;

/**
*
* @author tienlucnguyen
*/



import java.io.Serializable;

import samsung.sip.url.*;

public class HeaderField implements Serializable {

	private ContactAddress from;
	private ContactAddress to;
	private ContactAddress viaAgent;
	private ContactAddress viaServer;
	private ContactAddress contact;
	private String headerField;

	public HeaderField(ContactAddress from, ContactAddress to,
			ContactAddress viaAgent, ContactAddress viaServer,
			ContactAddress contact) {
		
		this.from = from;
		this.to = to;
		this.viaAgent = viaAgent;
		this.viaServer = viaServer;
		this.contact = contact;
		this.headerField = "From:" + from.getContactAddress() + "\n" + "To:"
				+ to.getContactAddress() + "\n" + "Via:"
				+ viaServer.getContactAddress() + "\n" + "Via:"
				+ viaAgent.getContactAddress() + "\n" + "Contact:"
				+ contact.getContactAddress() + "\n";
	}

	public ContactAddress getFrom() {
		return from;
	}

	public ContactAddress getTo() {
		return to;
	}

	public ContactAddress getViaAgent() {
		return viaAgent;
	}

	public ContactAddress getViaServer() {
		return viaServer;
	}

	public ContactAddress getContact() {
		return contact;
	}

	public String getHeaderField() {
		return headerField;
	}

	public void setFrom(ContactAddress from) {
		this.from = from;
	}

	public void setTo(ContactAddress to) {
		this.to = to;
	}

	public void setViaAgent(ContactAddress viaAgent) {
		this.viaAgent = viaAgent;
	}

	public void setViaServer(ContactAddress viaServer) {
		this.viaServer = viaServer;
	}

	public void setContact(ContactAddress contact) {
		this.contact = contact;
	}

	public void setHeaderField(String headerField) {
		this.headerField = headerField;
	}

}
