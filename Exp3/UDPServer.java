import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * UDPServer
 */
public class UDPServer {

  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  private static final byte[] zeroByte = { (byte) 0 };

  public static void main(String[] args) throws Exception {
    DatagramSocket socket = new DatagramSocket(UDPServerPort);
    byte[] senderBuffer = new byte[65536];
    byte[] receiverBuffer = new byte[65536];
    byte[] expectedChecksumRaw = new byte[65536];

    InetAddress ip = InetAddress.getByName("localhost");

    System.out.println("[INFO] Server listening on port: " + UDPServerPort);

    DataHandler dataHandler = new DataHandler();

    int index = 0;
    int invalid = -1;
    while (index < 24) {
      DatagramPacket receiverDataPacket = new DatagramPacket(receiverBuffer, receiverBuffer.length);
      socket.receive(receiverDataPacket);
      // Trim trailing zeros from received packet
      receiverBuffer = dataHandler.trimByteArray(receiverBuffer);
      System.out.println(Arrays.toString(receiverBuffer));

      // Find inserted zero in byte stream
      int pointer = 0;
      while (pointer < receiverBuffer.length) {
        if (receiverBuffer[pointer] == 0) {
          break;
        }
        pointer++;
      }

      System.out.println(pointer);

      // Get received buffer and expected checksum
      expectedChecksumRaw = Arrays.copyOfRange(receiverBuffer, pointer + 1, receiverBuffer.length);
      expectedChecksumRaw = dataHandler.appendByteArray(expectedChecksumRaw, zeroByte);
      receiverBuffer = Arrays.copyOf(receiverBuffer, pointer + 1);

      String actualChecksum = dataHandler.crcEncode(receiverBuffer);
      String expectedChecksum = dataHandler.byteStreamToString(expectedChecksumRaw);
      System.out.println("Actual: " + actualChecksum + " | Expected: " + expectedChecksum);

      if (actualChecksum.equals(expectedChecksum)) {
        System.out.println(
            "Client sent packet " + index + ": " + dataHandler.byteStreamToString(receiverBuffer) + " is as expected.");

        senderBuffer = String.valueOf(index).getBytes();

        DatagramPacket senderDataPacket = new DatagramPacket(senderBuffer, senderBuffer.length, ip, UDPClientPort);
        socket.send(senderDataPacket);

        index++;
      } else {
        System.out.println(
            "Client sent packet " + index + ": " + dataHandler.byteStreamToString(receiverBuffer) + " is invalid.");

        senderBuffer = String.valueOf(invalid).getBytes();

        DatagramPacket senderDataPacket = new DatagramPacket(senderBuffer, senderBuffer.length, ip, UDPClientPort);
        socket.send(senderDataPacket);
      }

      receiverBuffer = new byte[65536];
      expectedChecksumRaw = new byte[65536];
    }

    socket.close();
  }
}