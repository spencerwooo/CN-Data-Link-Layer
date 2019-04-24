import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UDPClient
 */
public class UDPClient {
  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  private static final int filterError = 10;
  private static final int filterLost = 10;

  // 5s timeout
  private static final int requestTimeOutDelay = 5000;
  // Add zero to end of frame
  private static final byte[] zeroByte = { (byte) 0 };

  // 24 items in sender list
  private static final String[] senderList = { "Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing",
      "elit", "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore", "magna", "aliqua", "Ut",
      "enim", "ad", "minim", "veniam" };

  // Frame lost or altered
  enum FilterType {
    LOST, ALTERED;
  }

  /**
   * Filter frame
   *
   * @param buf:        input byte array
   * @param FilterType: data lost or data altered
   * @return Filtered frame
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
      int randomNumber = ThreadLocalRandom.current().nextInt(0, len - 1);
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
    byte[] senderPacketBuffer = null;
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

      // Data to byte array
      senderPacketBuffer = senderOriginalItem.getBytes();
      // Add a trailing zero to the end of the sender packet
      senderPacketBuffer = dataHandler.appendByteArray(senderPacketBuffer, zeroByte);

      // Get checksum in string format
      String checksum = dataHandler.crcEncode(senderPacketBuffer);

      System.out.println(checksum);

      if (lostPacket == 1) {
        senderPacketBuffer = dataFilter(senderPacketBuffer, FilterType.LOST);
      }

      if (errorPacket == 1) {
        senderPacketBuffer = dataFilter(senderPacketBuffer, FilterType.ALTERED);
      }

      // Add checksum to end of frame
      senderPacketBuffer = dataHandler.appendByteArray(senderPacketBuffer, checksum.getBytes());

      // Packet to send: [Frame] + [0] + [Checksum]
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
            // Check received packet, expected value is frame index
            if (Integer.valueOf(receivedString) != -1) {
              System.out.println("Server sent back: " + receivedString);
              index++;
            } else {
              // If packet is "-1", then the received packet is invalid
              throw new Exception("Invalid packet. ");
            }

            Thread.sleep(1000);
            break;

          } catch (SocketTimeoutException e) {
            // Catch timeout exception
            System.out.println("Response timeout. Resending packet " + index);
            break;
          }
        }

      } catch (Exception e) {
        // Catch packet error and packet timeout exception, resend packet
        System.out.println(e + "Resending packet " + index);
      }
    }

    socket.close();
  }
}