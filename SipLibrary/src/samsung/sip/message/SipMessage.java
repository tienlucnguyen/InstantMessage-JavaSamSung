package samsung.sip.message;

import java.io.Serializable;

public class SipMessage implements Serializable {

    /**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private String type;
    private StatusLine statusLine;
    private HeaderField headerField;
    private MessageBody messageBody;
    private String SipMessage;

    public SipMessage(String type, StatusLine statusLine, HeaderField headerField, MessageBody messageBody) {
        this.type = type;
        this.statusLine = statusLine;
        this.headerField = headerField;
        this.messageBody = messageBody;
        this.SipMessage = "\n"+statusLine.getStatusLine() + "\n"
                + headerField.getHeaderField() + "\n"
                + messageBody.getMessageBody();
    }

    //statusLine = new StstusLine()
    public String getType() {
        return type;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HeaderField getHeaderField() {
        return headerField;
    }

    public MessageBody getMessageBody() {
        return messageBody;
    }

    public String getSipMessage() {
        return SipMessage;
    }

}
