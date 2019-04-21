import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDPServer
 */
public class UDPServer {

  public static final int UDPServerPort = 8888;

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
    DatagramSocket datagramSocket = new DatagramSocket(UDPServerPort);
    byte[] receiver = new byte[65536];
    DatagramPacket dpReceive = null;

    System.out.println("[INFO] Server listening on port: " + UDPServerPort);

    while (true) {
      dpReceive = new DatagramPacket(receiver, receiver.length);
      datagramSocket.receive(dpReceive);
      System.out.println("Client sent: " + getData(receiver));

      if (getData(receiver).toString().equals("bye")) {
        System.out.println("Client said: bye.");
        System.out.println("Server shutting down...");
        break;
      }
      receiver = new byte[65536];
    }

    datagramSocket.close();
  }
}