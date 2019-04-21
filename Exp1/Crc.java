import java.math.BigInteger;

/**
 * Crc
 */
public class Crc {
  /*
   * Java doesn't show binary number with leading zeros.
   */
  public static String showCompleteBinaryNumber(String binaryNumber, int binaryNumberDesignedLength) {
    while (binaryNumber.length() < binaryNumberDesignedLength) {
      binaryNumber = "0" + binaryNumber;
    }
    return binaryNumber;
  }

  /*
   * Get CRC code
   */
  public static String crcEncode(String infoString) {
    // CRC-CCITT = X16 + X12 + X5 + 1
    String genXString = "10001000000100001";
    // 待发送数据、生成多项式
    BigInteger infoStringData = new BigInteger(infoString, 2);
    BigInteger genXStringData = new BigInteger(genXString, 2);

    System.out.println("Encoding polynomial: CRC-CCITT " + genXString);

    // 待发送数据长度、生成多项式码长
    int infoStringLength = infoString.length();
    int genXStringLength = genXString.length();
    // 移位
    infoStringData = infoStringData.shiftLeft(genXStringLength - 1);
    genXStringData = genXStringData.shiftLeft(infoStringLength - 1);

    BigInteger bigInteger = new BigInteger("2");
    int length = infoStringLength + genXStringLength - 1;
    BigInteger flag = bigInteger.pow(length - 1);

    BigInteger bigZero = new BigInteger("0");

    for (int i = 0; i < infoStringLength; i++) {
      if (!infoStringData.and(flag).equals(bigZero)) {
        infoStringData = infoStringData.xor(genXStringData);
        genXStringData = genXStringData.shiftRight(1);
      } else {
        genXStringData = genXStringData.shiftRight(1);
      }
      flag = flag.shiftRight(1);
    }

    String crc = infoStringData.toString(2);
    crc = showCompleteBinaryNumber(crc, genXStringLength - 1);
    return crc;
  }

  public static void main(String[] args) {
    System.out.println("[INFO] Sender up...");
    // 发送数据
    String infoStringToSend = "011011111111111111111111110"; // "01101100011001110100110000110110";
    System.out.println("Info string to send: " + infoStringToSend);

    // 编码
    String crc = crcEncode(infoStringToSend);
    System.out.println("Info string CRC Code: " + crc);

    String infoStringSent = infoStringToSend + crc;
    System.out.println("Info string sent: " + infoStringSent);

    System.out.println("[INFO] Receiver up...");

    String infoStringReceived = "0110111111111111111111111100110001011001011";
    System.out.println("Info String received: " + infoStringReceived);

    String receivedCrc = crcEncode(infoStringReceived);
    System.out.println("Received CRC Code: " + receivedCrc);
  }
}