/**
 * DataHandler
 */
public class DataHandler {
  /*
   * Convert byte stream to string.
   */
  public StringBuilder byteStreamToString(byte[] buf) {
    if (buf == null) {
      return null;
    }
    StringBuilder data = new StringBuilder();
    for (int i = 0; buf[i] != 0; i++) {
      data.append((char) buf[i]);
    }
    return data;
  }
}