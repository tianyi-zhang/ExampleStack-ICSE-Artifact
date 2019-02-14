public class foo{
    private int initAudioRecord(int rate)
    {
        try
        {
            Log.v("===========Attempting rate ", rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
            bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

            if (bufferSize != AudioRecord.ERROR_BAD_VALUE)
            {
                // check if we can instantiate and have a success
                recorder = new AudioRecord(AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

                if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                {
                    Log.v("===========final rate ", rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);

                    return rate;
                }
            }
        }
        catch (Exception e)
        {
            Log.v("error", "" + rate);
        }

        return -1;
    }
}