public class foo {
    @Override
    public void open(AudioInputStream stream) throws
        IOException,
        LineUnavailableException {

        AudioInputStream is1;
        format = stream.getFormat();

        if (format.getEncoding()!=AudioFormat.Encoding.PCM_SIGNED) {
            is1 = AudioSystem.getAudioInputStream(
                AudioFormat.Encoding.PCM_SIGNED, stream );
        } else {
            is1 = stream;
        }
        format = is1.getFormat();
        InputStream is2;
        if (parent!=null) {
            ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
                parent,
                "Loading track..",
                is1);
            pmis.getProgressMonitor().setMillisToPopup(0);
            is2 = pmis;
        } else {
            is2 = is1;
        }

        byte[] buf = new byte[ 2^16 ];
        int totalRead = 0;
        int numRead = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        numRead = is2.read( buf );
        while (numRead>-1) {
            baos.write( buf, 0, numRead );
            numRead = is2.read( buf, 0, buf.length );
            totalRead += numRead;
        }
        is2.close();
        audioData = baos.toByteArray();
        AudioFormat afTemp;
        if (format.getChannels()<2) {
            afTemp = new AudioFormat(
                format.getEncoding(),
                format.getSampleRate(),
                format.getSampleSizeInBits(),
                2,
                format.getSampleSizeInBits()*2/8, // calculate frame size
                format.getFrameRate(),
                format.isBigEndian()
                );
        } else {
            afTemp = format;
        }

        setLoopPoints(0,audioData.length);
        dataLine = AudioSystem.getSourceDataLine(afTemp);
        dataLine.open();
        inputStream = new ByteArrayInputStream( audioData );
    }
}