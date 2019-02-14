public class foo{
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
			final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

			// Skip other devices
			if (mBluetoothGatt == null || !device.getAddress().equals(mBluetoothGatt.getDevice().getAddress()))
				return;

			DebugLogger.i(TAG, "Bond state changed for: " + device.getName() + " new state: " + bondState + " previous: " + previousBondState);

			switch (bondState) {
				case BluetoothDevice.BOND_BONDING:
					mCallbacks.onBondingRequired(device);
					break;
				case BluetoothDevice.BOND_BONDED:
					mCallbacks.onBonded(device);

					// Start initializing again.
					// In fact, bonding forces additional, internal service discovery (at least on Nexus devices), so this method may safely be used to start this process again.
					mBluetoothGatt.discoverServices();
					break;
			}
		}
}