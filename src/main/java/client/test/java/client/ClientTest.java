package client.test.java.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.Client;
import client.ClientConnectionFactory;
import protobuf.JdssAuditor.DisplayData;
import protobuf.JdssAuditor.DisplayData.Time;

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
    int count = 0;
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    Time time;
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

        time = Time.newBuilder().setTime("TIME " + formatter.format(new Date()).toString()).build();
        DisplayData displayData =
            DisplayData.newBuilder().setMessageType(DisplayData.AuditorMessageType.TIME).setTime(time).build();
        for (Client client : list) {
          client.sendData(displayData);
          Thread.sleep(500);
        }



        if ((count % 5) == 0) {
          Client client = list.remove(0);
          client.disconnect();
          remList.add(client);
        }


      }
      catch (Exception es) {
        logger.info("Exception with client:", es.getMessage() );
      }

    }


  }

}
