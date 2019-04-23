import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDPServer
 */
public class UDPServer {

  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  public static void main(String[] args) throws Exception {
    DatagramSocket socket = new DatagramSocket(UDPServerPort);
    byte[] senderBuffer = new byte[65536];
    byte[] receiverBuffer = new byte[65536];

    InetAddress ip = InetAddress.getByName("localhost");

    System.out.println("[INFO] Server listening on port: " + UDPServerPort);

    DataHandler dataHandler = new DataHandler();

    int index = 0;
    int invalid = -1;
    while (index < 24) {
      DatagramPacket receiverDataPacket = new DatagramPacket(receiverBuffer, receiverBuffer.length);
      socket.receive(receiverDataPacket);
      System.out.println("Client sent packet " + index + ": " + dataHandler.byteStreamToString(receiverBuffer));

      senderBuffer = String.valueOf(index).getBytes();

      DatagramPacket senderDataPacket = new DatagramPacket(senderBuffer, senderBuffer.length, ip, UDPClientPort);
      socket.send(senderDataPacket);

      index++;

      receiverBuffer = new byte[65536];
    }

    socket.close();
  }
}