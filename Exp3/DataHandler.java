import java.util.Arrays;

/**
 * DataHandler
 */
public class DataHandler {

  private static int polynomial = 0x1021;

  /**
   * Convert byte stream to string.
   *
   * @param buf
   * @return
   */
  public String byteStreamToString(byte[] buf) {
    if (buf == null) {
      return null;
    }
    StringBuilder data = new StringBuilder();
    for (int i = 0; buf[i] != 0; i++) {
      data.append((char) buf[i]);
    }
    return data.toString();
  }

  /**
   * CRC encoding
   *
   * @param buf
   * @return
   */
  public String crcEncode(byte[] buf) {
    int crc = 0xffff;

    for (byte b : buf) {
      for (int i = 0; i < 8; i++) {
        boolean bit = ((b >> (7 - i) & 1) == 1);
        boolean c15 = ((crc >> 15 & 1) == 1);
        crc <<= 1;
        if (c15 ^ bit)
          crc ^= polynomial;
      }
    }

    crc &= 0xffff;
    return Integer.toString(crc);
  }

  /**
   * Concat two byte arrays
   *
   * @param buf1
   * @param buf2
   * @return
   */
  public byte[] appendByteArray(byte[] buf1, byte[] buf2) {
    byte[] buffer = new byte[buf1.length + buf2.length];

    for (int i = 0; i < buf1.length; i++) {
      buffer[i] = buf1[i];
    }
    for (int i = buf1.length, j = 0; i < buf1.length + buf2.length; i++, j++) {
      buffer[i] = buf2[j];
    }

    return buffer;
  }

  /**
   * Trim byte array with trailing zeros (leave a zero behind for recognition)
   *
   * @param buf
   * @return
   */
  public byte[] trimByteArray(byte[] buf) {
    int tail = buf.length - 1;
    while (tail >= 0 && buf[tail] == 0) {
      --tail;
    }
    return Arrays.copyOf(buf, tail + 1);
  }

  /**
   * From answer:
   * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
   *
   * @param bytes
   * @return
   */
  public String byteToHexString(byte[] bytes) {
    final char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }
}