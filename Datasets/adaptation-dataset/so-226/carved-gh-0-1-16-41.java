public class foo{
    /**
     * 
     * @param context
     * @param attrs
     */
    public BluetoothDevicePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (bta != null)
        {
	        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
	        CharSequence[] entries = new CharSequence[pairedDevices.size()];
	        CharSequence[] entryValues = new CharSequence[pairedDevices.size()];
	        int i = 0;
	        for (BluetoothDevice dev : pairedDevices)
	        {
	            entries[i] = dev.getName();
	            entryValues[i] = dev.getAddress();
	            i++;
	        }
	        setEntries(entries);
	        setEntryValues(entryValues);
        }
    }
}