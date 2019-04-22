import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDPServer
 */
public class UDPServer {

  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  private static final String ack = "Oh yeah, got the message.";

  public static StringBuilder getData(byte[] buf) {
    if (buf == null) {
      return null;
    }
    StringBuilder data = new StringBuilder();
    for (int i = 0; buf[i] != 0; i++) {
      data.append((char) buf[i]);
    }
    return data;
  }

  public static void main(String[] args) throws Exception {
    DatagramSocket socket = new DatagramSocket(UDPServerPort);
    byte[] senderBuffer = ack.getBytes();
    byte[] receiverBuffer = new byte[65536];

    InetAddress ip = InetAddress.getByName("localhost");

    System.out.println("[INFO] Server listening on port: " + UDPServerPort);

    DataHandler dataHandler = new DataHandler();

    int index = 0;
    while (true) {
      DatagramPacket receiverDataPacket = new DatagramPacket(receiverBuffer, receiverBuffer.length);
      socket.receive(receiverDataPacket);
      System.out.println("Client sent packet " + index + ": " + dataHandler.byteStreamToString(receiverBuffer));

      DatagramPacket senderDataPacket = new DatagramPacket(senderBuffer, senderBuffer.length, ip, UDPClientPort);
      socket.send(senderDataPacket);
      index++;

      if (dataHandler.byteStreamToString(receiverBuffer).toString().equals("bye")) {
        System.out.println("Client said: bye.");
        System.out.println("Server shutting down...");
        break;
      }
      receiverBuffer = new byte[65536];
    }

    socket.close();
  }
}