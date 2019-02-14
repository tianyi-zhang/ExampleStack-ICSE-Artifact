public class foo{
        // UUID filtering in android 4.3 and 4.4 is broken.  See:
        //  http://stackoverflow.com/questions/18019161/startlescan-with-128-bit-uuids-doesnt-work-on-native-android-ble-implementation
        // This is a useful workaround to manually parse advertisement data.
        public List<UUID> parseUUIDs() {
            List<UUID> uuids = new ArrayList<UUID>();

            int offset = 0;
            while (offset < (bytes.length - 2)) {
                int len = bytes[offset++];
                if (len == 0)
                    break;

                int type = bytes[offset++];
                switch (type) {
                    case 0x02: // Partial list of 16-bit UUIDs
                    case 0x03: // Complete list of 16-bit UUIDs
                        while (len > 1) {
                            int uuid16 = bytes[offset++];
                            uuid16 += (bytes[offset++] << 8);
                            len -= 2;
                            uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                        }
                        break;
                    case 0x06:// Partial list of 128-bit UUIDs
                    case 0x07:// Complete list of 128-bit UUIDs
                        // Loop through the advertised 128-bit UUID's.
                        while (len >= 16) {
                            try {
                                // Wrap the advertised bits and order them.
                                ByteBuffer buffer = ByteBuffer.wrap(bytes, offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                                long mostSignificantBit = buffer.getLong();
                                long leastSignificantBit = buffer.getLong();
                                uuids.add(new UUID(leastSignificantBit, mostSignificantBit));
                            } catch (IndexOutOfBoundsException e) {
                                // Defensive programming.
                                //Log.e(LOG_TAG, e.toString());
                                continue;
                            } finally {
                                // Move the offset to read the next uuid.
                                offset += 15;
                                len -= 16;
                            }
                        }
                        break;
                    default:
                        offset += (len - 1);
                        break;
                }
            }
            return uuids;
        }
}