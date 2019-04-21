/**
 * TransparentTransmission
 */
public class TransparentTransmission {
  public static final String zeroBitFlagString = "01111110";
  public static final String byteFlagString = "7E";

  /*
   * 零比特填充
   */
  public static String zeroBitStuffingEncode(String infoString) {
    System.out.println("[INFO] Zero Bit Stuffing Starting...");
    System.out.println("Source data: " + infoString);

    String result = new String();

    int counter = 0;
    try {
      for (int i = 0; i < infoString.length(); i++) {
        if (infoString.charAt(i) != '1' && infoString.charAt(i) != '0') {
          throw new Exception("Illegal string, only binary values allowed.");
        }

        if (infoString.charAt(i) == '1') {
          counter++;
          result += infoString.charAt(i);
        } else {
          result += infoString.charAt(i);
          counter = 0;
        }

        if (counter == 5) {
          result += '0';
          counter = 0;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }

    System.out.println("Sending string: " + zeroBitFlagString + " | " + result + " | " + zeroBitFlagString);

    result = zeroBitFlagString + result + zeroBitFlagString;
    return result;
  }

  public static String zeroBitStuffingDecode(String encodedString) {
    System.out.println("Received! Decoding...");
    String result = new String();
    int counter = 0;

    encodedString = encodedString.split(zeroBitFlagString)[1];

    for (int i = 0; i < encodedString.length(); i++) {
      if (encodedString.charAt(i) == '1') {
        counter++;
        result += encodedString.charAt(i);
      } else {
        result += encodedString.charAt(i);
        counter = 0;
      }

      if (counter == 5) {
        if ((i + 2) != encodedString.length()) {
          result += encodedString.charAt(i + 2);
        } else {
          result += '1';
        }
        i += 2;
        counter = 1;
      }
    }

    return result;
  }

  /*
   * 字节填充
   */
  public static String byteStuffingEncode(String infoString) {
    System.out.println("[INFO] Byte Stuffing Starting...");
    System.out.println("Source data: " + infoString);

    String result = new String();

    for (int i = 0; i < infoString.length(); i++) {
      if (infoString.charAt(i) == '7') {
        if (infoString.charAt(i + 1) == 'E') {
          result += '/';
          result += infoString.charAt(i);
        } else {
          result += infoString.charAt(i);
        }
      } else if (infoString.charAt(i) == '/') {
        result += '/';
        result += infoString.charAt(i);
      } else {
        result += infoString.charAt(i);
      }
    }

    System.out.println("Sender string: " + byteFlagString + " | " + result + " | " + byteFlagString);
    result = byteFlagString + result + byteFlagString;
    return result;
  }

  public static String byteStuffingDecode(String encodedString) {
    System.out.println("[INFO] Received! Decoding...");

    String result = new String();

    for (int i = 0; i < encodedString.length(); i++) {
      if (encodedString.charAt(i) == '/') {
        if (encodedString.charAt(i + 1) == '/') {
          result += encodedString.charAt(i);
          i++;
        } else if (encodedString.charAt(i + 1) == '7') {
          if (encodedString.charAt(i + 2) == 'E') {
            result += encodedString.charAt(i + 1);
            i++;
          } else {
            result += encodedString.charAt(i);
          }
        } else {
          result += encodedString.charAt(i);
        }
      } else {
        result += encodedString.charAt(i);
      }
    }

    return result;
  }

  public static void main(String[] args) {
    String zeroBitStuffString = "0110011111101111111111101111110110";
    String zeroBitSenderString = zeroBitStuffingEncode(zeroBitStuffString);
    String decodeZeroBitSender = zeroBitStuffingDecode(zeroBitSenderString);
    System.out.println("Receiver string: " + decodeZeroBitSender);
    
    String byteStuffString = "347D7E807E40AA7D";
    String byteSenderString = byteStuffingEncode(byteStuffString);
    String decodeByteString = byteStuffingDecode(byteSenderString);
    System.out.println("Receiver string: " + decodeByteString);
  }
}