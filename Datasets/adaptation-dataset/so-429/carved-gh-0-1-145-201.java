public class foo{
    /**
     * Gobbles up link data by parsing it and storing info in member fields
     *
     * @param link all the bytes from the .lnk file
     */
    private void parseLink(byte[] link) throws IOException {
        try {
            if (!isMagicPresent(link))
                throw new IOException("Invalid shortcut; magic is missing");

            // get the flags byte
            byte flags = link[0x14];

            // get the file attributes byte
            final int file_atts_offset = 0x18;
            byte file_atts = link[file_atts_offset];
            byte is_dir_mask = (byte) 0x10;
            directory = (file_atts & is_dir_mask) > 0;

            // if the shell settings are present, skip them
            final int shell_offset = 0x4c;
            final byte has_shell_mask = (byte) 0x01;
            int shell_len = 0;
            if ((flags & has_shell_mask) > 0) {
                // the plus 2 accounts for the length marker itself
                shell_len = bytesToWord(link, shell_offset) + 2;
            }

            // get to the file settings
            int file_start = 0x4c + shell_len;

            final int file_location_info_flag_offset_offset = 0x08;
            int file_location_info_flag = link[file_start + file_location_info_flag_offset_offset];
            local = (file_location_info_flag & 2) == 0;
            // get the local volume and local system values
            //final int localVolumeTable_offset_offset = 0x0C;
            final int basename_offset_offset = 0x10;
            final int networkVolumeTable_offset_offset = 0x14;
            final int finalname_offset_offset = 0x18;
            int finalname_offset = link[file_start + finalname_offset_offset] + file_start;
            String finalname = getNullDelimitedString(link, finalname_offset);
            if (local) {
                int basename_offset = link[file_start + basename_offset_offset] + file_start;
                String basename = getNullDelimitedString(link, basename_offset);
                realFileName = basename + finalname;
            } else {
                int networkVolumeTable_offset = link[file_start + networkVolumeTable_offset_offset] + file_start;
                int shareName_offset_offset = 0x08;
                int shareName_offset = link[networkVolumeTable_offset + shareName_offset_offset]
                        + networkVolumeTable_offset;
                String shareName = getNullDelimitedString(link, shareName_offset);
                realFileName = shareName + "\\" + finalname;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Could not be parsed, probably not a valid WindowsShortcut", e);
        }
    }
}