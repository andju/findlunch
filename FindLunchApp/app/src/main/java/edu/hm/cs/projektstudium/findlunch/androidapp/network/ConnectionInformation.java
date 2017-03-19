package edu.hm.cs.projektstudium.findlunch.androidapp.network;


/**
 * The type Connection information
 * contains the information for
 * the connection to a REST API.
 */
public class ConnectionInformation {
    /**
     * The Host.
     */
    private String host;
    /**
     * The Port.
     */
    private int port;

    /**
     * The Https.
     */
    private boolean https;

    /**
     * Instantiates a new Connection information.
     */
    public ConnectionInformation() {
    }

    /**
     * Instantiates a new Connection information.
     *
     * @param host the host
     * @param port the port
     * @param https the https
     */
    public ConnectionInformation(String host, int port, boolean https) {
        this.host = host;
        this.port = port;
        this.https = https;
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets https.
     * @return the https
     */
    public boolean isHttps() {
        return https;
    }

    /**
     * Sets https.
     * @param https the https
     */
    public void setHttps(boolean https) {
        this.https = https;
    }
}
