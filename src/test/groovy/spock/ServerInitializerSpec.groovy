package spock

import protocol.protomessage.server.Server
import spock.lang.Shared
import spock.lang.Specification

class ServerInitializerSpec extends Specification {

    @Shared
    Server server;

    def setup(){
        println "Creating new server"
        server = new Server();

    }
    def cleanup(){
        println "Shutting down server"
        server.shutdownServer();
    }

    def "Created with expected values"(){
        server.startServer();
        expect:
        server.getChannelConnections(100) == Collections.emptyList()
    }

}
