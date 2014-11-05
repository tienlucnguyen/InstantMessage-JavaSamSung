package samsung.sip.url;

import java.io.Serializable;

/**
*
* @author auto
*/

public class ContactAddress implements Serializable{

	private String name;
    private String address;
    private String ContactAddress;
    
    public ContactAddress(String name, String address){
        this.name = name;
        this.address = address;
        this.ContactAddress = "sip: " + name + "@" + address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactAddress() {
        return ContactAddress;
    }
	
	
}
