package dev.tinelix.irc.android;

public class Profile {

    String name;
    String server;
    int port;
    boolean isConnected;

    Profile(String _describe, String _server, int _port, boolean _isConnected) {
        name = _describe;
        server = _server;
        port = _port;
        isConnected = _isConnected;
    }
}
