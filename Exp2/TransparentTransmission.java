/**
 * TransparentTransmission
 */
public class TransparentTransmission {
  /*
   * 零比特填充
   */
  public static String zeroBitStuffing(String infoString) {
    System.out.println("[INFO] Zeo Bit Stuffing Starting...");
    String flagString = "01111110";
    String[] subStrings = infoString.split(flagString);

    try {
      if (subStrings.length >= 3) {
        System.out.println(subStrings[0]);
        System.out.println(subStrings[1]);
        System.out.println(subStrings[2]);
      } else {
        throw new Exception("Illegal string.");
      }
    } catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }

    return infoString;
  }

  /*
   * 字节填充
   */
  public static String byteStuffing(String infoString) {
    System.out.println("[INFO] Byte Stuffing Starting...");
    String flagString = "7E";
    String[] subStrings = infoString.split(flagString);

    try {
      if (subStrings.length >= 3) {
        System.out.println(subStrings[0]);
        System.out.println(subStrings[1]);
        System.out.println(subStrings[2]);
      } else {
        throw new Exception("Illegal string.");
      }
    } catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }

    return infoString;
  }

  public static void main(String[] args) {
    String byteStuffString = "347D7E807E40AA7D";
    String byteSenderString = byteStuffing(byteStuffString);
    System.out.println(byteSenderString);

    String zeroBitStuffString = "0110011111101111111111101111110110";
    String zeroBitSenderString = zeroBitStuffing(zeroBitStuffString);
    System.out.println(zeroBitSenderString);
  }
}