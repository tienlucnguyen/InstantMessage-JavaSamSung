package samsung.sip.message;


/**
 *
 * @author tienlucnguyen
 */
import samsung.sip.url.*;
import java.io.Serializable;

public class StatusLine implements Serializable {

	private String statusLine;
	private String sipVersion;
	// Thong so su dung cho StatusLine cua Request
	private AOR requestAOR;
	private String requestName;
	// Thong so su dung cho StatusLine cua Respone

	private int sipCode;
	private String phrase;

	// Khoi tao Request
	public StatusLine(String sipVersion, AOR requestAOR, String requestName) {

		this.sipVersion = sipVersion;
		this.requestAOR = requestAOR;
		this.requestName = requestName;
		this.statusLine = requestName + " " + requestAOR.getAOR() + " "
				+ sipVersion;
	}

	// Khoi tao Response
	public StatusLine(String sipVersion, int sipCode, String phrase) {
		super();
		this.sipVersion = sipVersion;
		this.sipCode = sipCode;
		this.phrase = phrase;
		this.statusLine = sipVersion + " " + sipCode + " " + phrase;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public String getSipVersion() {
		return sipVersion;
	}

	public AOR getRequestAOR() {
		return requestAOR;
	}

	public String getRequestName() {
		return requestName;
	}

	public int getSipCode() {
		return sipCode;
	}

	public String getPhrase() {
		return phrase;
	}

}
