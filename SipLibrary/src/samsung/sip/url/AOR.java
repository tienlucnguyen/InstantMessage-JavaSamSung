package samsung.sip.url;

import java.io.Serializable;

/**
 *
 * @author auto
 */

public class AOR implements Serializable {

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private String name;
	private String host;
	private String AOR;

	public AOR(String name, String host) {

		this.name = name;
		this.host = host;
		this.AOR = "sip:" + name + "@" + host;

	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public String getAOR() {
		return AOR;
	}

}
