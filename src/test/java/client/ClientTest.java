package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.protobuf.client.Client;
import protocol.protobuf.client.ClientConnectionFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientTest {

  final static Logger logger = LoggerFactory.getLogger("client.test.java.client.ClientTest");

  public static void main(String... args) {

    logger.info("Starting Client Test");

    ClientConnectionFactory ccf = new ClientConnectionFactory();

    List<Client> list = new ArrayList<Client>();
    List<Client> remList = new ArrayList<Client>();

    for (int i = 0; i < 1; i++) {
      list.add(ccf.createClient("localhost", 6000));

    }

    for (Client client : list) {
      try {
        client.connect();
         Thread.sleep(100);
      }
      catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
/*    
    while(true) {
      try {
        Thread.sleep(30000L);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }*/
    int count = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

    while (true) {
      try {
        count++;
        if (list.isEmpty()) {
          list.addAll(remList);
          for (Client client : list) {
           client.connect();
          }
          remList.clear();
        }

   /*     Status status = Status.newBuilder().setHealth("GOOD").setErrors(5).setUptime(100).build();
        
        ProtoMessage data= ProtoMessage.newBuilder().setMessageType(MessageType.STATUS).setStatus(status).build();
        
        for (Client client : list) {
          client.sendMessage(data);
          Thread.sleep(1000);
        }*/

        Thread.sleep(1000);

/*
        if ((count % 15) == 0) {
          Client client = list.remove(0);
          client.disconnect();
          remList.add(client);
        }
*/


      }
      catch (Exception es) {
        logger.info("Exception with client:", es.getMessage() );
      }

    }


  }

}
