package samsung.sip.message;

/**
 *
 * @author tienlucnguyen
 */

import java.io.Serializable;

public class MessageBody implements Serializable {

	private int lengthContent;
	private String content;
	private String messageBody;

	public MessageBody(int lengthContent, String content) {

		this.lengthContent = lengthContent;
		this.content = content;
		this.messageBody = "Content-Length: " + lengthContent + "\n"
				+ "Content: " + content;
	}

	public int getLengthContent() {
		return lengthContent;
	}

	public String getContent() {
		return content;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setLengthContent(int lengthContent) {
		this.lengthContent = lengthContent;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
