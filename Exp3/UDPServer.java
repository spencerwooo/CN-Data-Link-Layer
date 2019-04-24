import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * UDPServer
 */
public class UDPServer {
  // Server and client listens on different ports
  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  // Add trailing zero (For DataHandler to print out byte array contents.)
  private static final byte[] zeroByte = { (byte) 0 };

  public static void main(String[] args) throws Exception {
    DatagramSocket socket = new DatagramSocket(UDPServerPort);
    InetAddress ip = InetAddress.getByName("localhost");
    DataHandler dataHandler = new DataHandler();

    byte[] senderBuffer = new byte[65536];
    byte[] receiverBuffer = new byte[65536];
    byte[] expectedChecksumRaw = new byte[65536];

    int index = 0;
    int invalid = -1;

    System.out.println("[INFO] Server listening on port: " + UDPServerPort);

    // Receive a total of 24 frames
    while (index < 24) {
      // Receive packets
      DatagramPacket receiverDataPacket = new DatagramPacket(receiverBuffer, receiverBuffer.length);
      socket.receive(receiverDataPacket);

      // Trim trailing zeros from received packet
      receiverBuffer = dataHandler.trimByteArray(receiverBuffer);

      // Frame received should be like: [Frame] + [0] + [Checksum]
      System.out.println(Arrays.toString(receiverBuffer));

      // Find inserted zero in byte stream
      int pointer = 0;
      while (pointer < receiverBuffer.length) {
        if (receiverBuffer[pointer] == 0) {
          break;
        }
        pointer++;
      }

      // Get received buffer and expected checksum
      expectedChecksumRaw = Arrays.copyOfRange(receiverBuffer, pointer + 1, receiverBuffer.length);
      expectedChecksumRaw = dataHandler.appendByteArray(expectedChecksumRaw, zeroByte);
      receiverBuffer = Arrays.copyOf(receiverBuffer, pointer + 1);

      String actualChecksum = dataHandler.crcEncode(receiverBuffer);
      String expectedChecksum = dataHandler.byteStreamToString(expectedChecksumRaw);
      System.out.println("Actual: " + actualChecksum + " | Expected: " + expectedChecksum);

      // Check if checksum is expected
      if (actualChecksum.equals(expectedChecksum)) {
        // Packet is valid, return packet index to client.
        System.out.println(
            "Client sent packet " + index + ": " + dataHandler.byteStreamToString(receiverBuffer) + " is as expected.");

        senderBuffer = String.valueOf(index).getBytes();
        DatagramPacket senderDataPacket = new DatagramPacket(senderBuffer, senderBuffer.length, ip, UDPClientPort);
        socket.send(senderDataPacket);

        // Packet index self-increase
        index++;
      } else {
        // Packet is invalid, demand a resend.
        System.out.println(
            "Client sent packet " + index + ": " + dataHandler.byteStreamToString(receiverBuffer) + " is invalid.");

        // Send back invalid value to client (invalid = -1)
        senderBuffer = String.valueOf(invalid).getBytes();
        DatagramPacket senderDataPacket = new DatagramPacket(senderBuffer, senderBuffer.length, ip, UDPClientPort);
        socket.send(senderDataPacket);
      }

      // Re-init byte buffers
      receiverBuffer = new byte[65536];
      expectedChecksumRaw = new byte[65536];
    }

    socket.close();
  }
}