public class foo {
 public static boolean validUTF8(byte[] input) {
  int i = 0;
  // Check for BOM
  if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
    && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
   i = 3;
  }

  int end;
  for (int j = input.length; i < j; ++i) {
   int octet = input[i];
   if ((octet & 0x80) == 0) {
    continue; // ASCII
   }

   // Check for UTF-8 leading byte
   if ((octet & 0xE0) == 0xC0) {
    end = i + 1;
   } else if ((octet & 0xF0) == 0xE0) {
    end = i + 2;
   } else if ((octet & 0xF8) == 0xF0) {
    end = i + 3;
   } else {
    // Java only supports BMP so 3 is max
    return false;
   }

   while (i < end) {
    i++;
    octet = input[i];
    if ((octet & 0xC0) != 0x80) {
     // Not a valid trailing byte
     return false;
    }
   }
  }
  return true;
 }
}