import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.Client;
import client.ClientConnectionFactory;


public class Main {
  final static Logger logger = LoggerFactory.getLogger("Main");

  public static void main(String... args) {

    logger.info("Entering application.");

    ClientConnectionFactory ccf = new ClientConnectionFactory();

    List<Client> list = new ArrayList<Client>();

    for (int i = 0; i < 10; i++) {
      list.add(ccf.createClient("localhost", 26002));
    }

    for (Client client : list) {
      try {
        client.connect();
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    int count = 0;
    while (true) {
      try {
        count++;
        if(list.isEmpty()) {break;}
        
        for (Client client : list) {
          client.sendData(count);
          Thread.sleep(500);
        }


        if ((count % 15) == 0) {
          Client client = list.remove(0);
          client.disconnect();
          
        }

      }
      catch (Exception es) {

      }

    }


  }

}
