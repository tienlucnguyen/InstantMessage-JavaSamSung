package samsung.sip.message;

import java.io.Serializable;

import samsung.sip.url.AOR;

public class StatusLine implements Serializable{

    /**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private String statusLine;
    private String sipVersion;
    //Thong so su dung cho StatusLine cua Request
    private AOR requestAOR;
    private String requestName;
    //Thong so su dung cho StatusLine cua Response
    private int sipCode;
    private String phrase;

    //Khoi tao Request
    public StatusLine(String requestName,AOR requestAOR,  String sipVersion) {
        this.requestAOR = requestAOR;
        this.requestName = requestName;
        this.sipVersion = sipVersion;
        this.statusLine = requestName + " " + requestAOR.getAOR() + " " + sipVersion;
    }

    //Khoi tao Response
    public StatusLine(String sipVersion, int sipCode, String phrase) {
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

    public String getRequestName() {
        return requestName;
    }

    public AOR getRequestAOR() {
        return requestAOR;
    }

    public int getSipCode() {
        return sipCode;
    }

    public String getPhrase() {
        return phrase;
    }
}
