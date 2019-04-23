import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UDPClient
 */
public class UDPClient {
  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  private static final int filterError = 10;
  private static final int filterLost = 10;

  private static final int requestTimeOutDelay = 5000;

  // 24 items in sender list
  private static final String[] senderList = { "Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
      "elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore", "magna", "aliqua", "Ut",
      "enim", "ad", "minim", "veniam" };

  enum FilterType {
    LOST, ALTERED;
  }

  /**
   * Filter byte array
   *
   * @param buf:        input byte array
   * @param FilterType: data lost or data altered
   * @return byte array
   */
  public static byte[] dataFilter(byte[] buf, FilterType filterType) {
    switch (filterType) {
    // Data packet lost
    case LOST:
      buf = new byte[65536];
      break;
    // Data packet errored
    case ALTERED:
      int len = buf.length;
      int randomNumber = ThreadLocalRandom.current().nextInt(0, len);
      buf[randomNumber] = buf[1];
      break;
    default:
      break;
    }
    return buf;
  }

  public static void main(String[] args) throws Exception {
    System.out.println("[INFO] UDP Client started. Sending packets to port: " + UDPServerPort);

    DatagramSocket socket = new DatagramSocket(UDPClientPort);
    DataHandler dataHandler = new DataHandler();
    InetAddress ip = InetAddress.getByName("localhost");
    byte[] senderPacketBuffer = new byte[65536];
    byte[] receiverPacketBuffer = new byte[65536];

    int senderListLength = senderList.length;
    int index = 0;

    while (index < senderListLength) {
      // One out of ten packet gets lost
      int lostPacket = ThreadLocalRandom.current().nextInt(1, filterLost);
      // One out of ten packet gets altered
      int errorPacket = ThreadLocalRandom.current().nextInt(1, filterError);

      String senderOriginalItem = senderList[index];

      System.out.println("[Sender]");
      System.out.println("Sending Packet " + index + ": " + senderOriginalItem);
      senderPacketBuffer = senderOriginalItem.getBytes();

      int checksum = dataHandler.crcEncode(senderPacketBuffer);

      System.out.println(Arrays.toString(senderPacketBuffer));
      System.out.println(checksum);

      if (lostPacket == 1) {
        senderPacketBuffer = dataFilter(senderPacketBuffer, FilterType.LOST);
      }

      if (errorPacket != 1) {
        senderPacketBuffer = dataFilter(senderPacketBuffer, FilterType.ALTERED);
      }

      System.out.println(Arrays.toString(senderPacketBuffer));

      try {
        if (lostPacket != 1) {
          DatagramPacket senderDataPacket = new DatagramPacket(senderPacketBuffer, senderPacketBuffer.length, ip,
              UDPServerPort);
          socket.send(senderDataPacket);
        } else {
          // Do nothing
        }

        // Set timeout for receiving server ack
        socket.setSoTimeout(requestTimeOutDelay);

        while (true) {
          try {
            DatagramPacket receiverDataPacket = new DatagramPacket(receiverPacketBuffer, receiverPacketBuffer.length);
            socket.receive(receiverDataPacket);

            String receivedString = dataHandler.byteStreamToString(receiverPacketBuffer);
            if (Integer.valueOf(receivedString) != -1) {
              System.out.println("Server sent back: " + receivedString);
              index++;
            } else {
              throw new Exception("Invalid packet.");
            }

            Thread.sleep(1000);
            break;
          } catch (SocketTimeoutException e) {
            System.out.println("Response timeout. Resending packet " + index);
            break;
          }
        }

      } catch (Exception e) {
        System.out.println(e + "Resending packet " + index);
      }
    }

    // scanner.close();
    socket.close();
  }
}