import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * UDPClient
 */
public class UDPClient {
  private static final int UDPServerPort = 8888;
  private static final int UDPClientPort = 9999;

  private static final int FilterError = 10;
  private static final int FilterLost = 10;

  public static void main(String[] args) throws Exception {
    System.out.println("[INFO] UDP Client started. Sending packets to port: " + UDPServerPort);

    DatagramSocket socket = new DatagramSocket(UDPClientPort);
    InetAddress ip = InetAddress.getByName("localhost");
    byte senderPacketBuffer[] = null;
    byte[] receiverPacketBuffer = new byte[65536];

    DataHandler dataHandler = new DataHandler();

    Scanner scanner = new Scanner(System.in);
    int index = 0;
    while (true) {
      System.out.print("Sending Packet " + index + ": ");
      String input = scanner.nextLine();
      senderPacketBuffer = input.getBytes();

      DatagramPacket senderDataPacket = new DatagramPacket(senderPacketBuffer, senderPacketBuffer.length, ip,
          UDPServerPort);
      socket.send(senderDataPacket);

      DatagramPacket receiverDataPacket = new DatagramPacket(receiverPacketBuffer, receiverPacketBuffer.length);
      socket.receive(receiverDataPacket);
      System.out.println("Server sent back: " + dataHandler.byteStreamToString(receiverPacketBuffer));

      index++;
      if (input.equals("bye")) {
        break;
      }
    }

    scanner.close();
    socket.close();
  }
}