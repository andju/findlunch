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
     * Instantiates a new Connection information.
     */
    public ConnectionInformation() {
    }

    /**
     * Instantiates a new Connection information.
     *
     * @param host the host
     * @param port the port
     */
    public ConnectionInformation(String host, int port) {
        this.host = host;
        this.port = port;
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
}
