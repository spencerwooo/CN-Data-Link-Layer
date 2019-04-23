import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    Integer.toHexString(crc);
    return Integer.toHexString(crc);
  }

  /**
   * Concat two byte arrays
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
}