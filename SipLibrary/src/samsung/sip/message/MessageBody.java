package samsung.sip.message;

import java.io.Serializable;

public class MessageBody implements Serializable{

    /**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private int contentLength = 0;
    private String content = "";
    private String messageBody;

    public MessageBody(int contentLength, String content) {
        this.contentLength = contentLength;
        this.content = content;
        this.messageBody = "Content-Length: " + contentLength + "\n"
                         + "Content       : " + content;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContent() {
        return content;
    }

    public String getMessageBody() {
        return messageBody;
    }

}