public class foo{
    protected void initDevices(){
    	MidiDevice device;
    	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    	
    	allTransmitters = new ArrayList<Transmitter>();
    	
    	for (int i = 0; i < infos.length; i++) {
    		try {
    			device = MidiSystem.getMidiDevice(infos[i]);
    			// does the device have any transmitters?
    			// if it does, add it to the device list
    			System.out.println(infos[i]);
    			    			
    			//get all transmitters
    			List<Transmitter> transmitters = device.getTransmitters();
    			// and for each transmitter create a new receiver using our own MidiInputReceiver
    			for(int j = 0; j < transmitters.size(); j++ ) {
    				Receiver receiver = new MidiInputReceiver(device.getDeviceInfo().toString());
    				transmitters.get(j).setReceiver(receiver);
    			}
    			
    			// open each device
    			device.open();
    			
    			// store transmitter for each device
    			allTransmitters.add(device.getTransmitter());
    			
    			// if code gets this far without throwing an exception print a success message
    			System.out.println(device.getDeviceInfo()+" was opened");

    		} catch (MidiUnavailableException e) {}
    	}
    }
}