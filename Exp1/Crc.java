import java.math.BigInteger;

/**
 * Crc
 */
public class Crc {
  /*
   * Java doesn't show binary number with leading zeros.
   */
  public static String showCompleteBinaryNumber(String binaryNumber, int binaryNumberDesignedLength) {
    while (binaryNumber.length() < binaryNumberDesignedLength - 1) {
      binaryNumber = "0" + binaryNumber;
    }
    return binaryNumber;
  }

  /*
   * Get CRC code
   */
  public static String crcEncode(String infoString) {
    // CRC-CCITT = X16 + X12 + X5 + 1
    String genXString = "1101"; // "10001000000100001";
    // 待发送数据、生成多项式
    long infoStringData = Long.parseLong(infoString, 2);
    long genXStringData = Long.parseLong(genXString, 2);

    System.out.println("Encoding polynomial: CRC-CCITT " + genXString);

    // 待发送数据长度、生成多项式码长
    int infoStringLength = infoString.length();
    int genXStringLength = genXString.length();
    // 移位
    infoStringData = infoStringData << (genXStringLength - 1);
    genXStringData = genXStringData << (infoStringLength - 1);

    BigInteger bigInteger = new BigInteger("2");
    int length = infoStringLength + genXStringLength - 1;
    long flag = bigInteger.pow(length - 1).longValue();

    for (int i = 0; i < infoStringLength; i++) {
      if ((infoStringData & flag) != 0) {
        infoStringData = infoStringData ^ genXStringData;
        genXStringData = genXStringData >> 1;
      } else {
        genXStringData = genXStringData >> 1;
      }
      flag = flag >> 1;
    }

    String crc = Long.toBinaryString(infoStringData);
    // crc = showCompleteBinaryNumber(crc, genXStringLength - 1);
    return crc;
  }

  public static void main(String[] args) {
    // 发送数据
    String infoStringToSend = "11111001"; // "01101100011001110100110000110110";
    System.out.println("Info string to send: " + infoStringToSend);

    // 编码
    String crc = crcEncode(infoStringToSend);
    System.out.println("CRC Code: " + crc);

    String infoStringSent = infoStringToSend + crc;
    System.out.println("Info string sent: " + infoStringSent);

    // String infoStringReceived = "01101100011001110100010000110110";
    // System.out.println("Info String received: " + infoStringReceived);

    String sentCrc = crcEncode(infoStringSent);
    System.out.println(sentCrc);
  }
}