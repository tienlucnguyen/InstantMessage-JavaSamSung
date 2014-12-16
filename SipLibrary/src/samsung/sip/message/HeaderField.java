package samsung.sip.message;

import java.io.Serializable;

import samsung.sip.url.AOR;
import samsung.sip.url.ContactAddress;

public class HeaderField implements Serializable{
	   

    /**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private AOR from;
    private AOR to;
    private ContactAddress viaAgent;
    private ContactAddress viaServer;
    private ContactAddress contact;
    private String headerField;

    public HeaderField(AOR from, AOR to, ContactAddress viaAgent,
            ContactAddress viaServer, ContactAddress contact) {
        this.from = from;
        this.to = to;
        this.viaAgent = viaAgent;
        this.viaServer = viaServer;
        this.contact = contact;
        this.headerField = "From   : " + from.getAOR() + "\n"
                + "To     : " + to.getAOR() + "\n"
                + "Via    : " + viaServer.getContactAddress()+ "\n"
                + "Via    : " + viaAgent.getContactAddress() + "\n"
                + "Contact: " + contact.getContactAddress() + "\n";
    }

    public AOR getFrom() {
        return from;
    }

    public AOR getTo() {
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

}