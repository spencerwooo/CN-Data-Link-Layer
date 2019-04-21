import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * UDPClient
 */
public class UDPClient {
  private static final int UDPPort = 8888;

  public static void main(String[] args) throws Exception {
    System.out.println("[INFO] UDP Client started. Sending packets to port: " + UDPPort);

    DatagramSocket socket = new DatagramSocket();
    InetAddress ip = InetAddress.getByName("localhost");
    byte buf[] = null;

    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("Sending: ");
      String input = scanner.nextLine();
      buf = input.getBytes();

      DatagramPacket dataPacket = new DatagramPacket(buf, buf.length, ip, UDPPort);
      socket.send(dataPacket);

      if (input.equals("bye")) {
        break;
      }
    }

    scanner.close();
    socket.close();
  }
}