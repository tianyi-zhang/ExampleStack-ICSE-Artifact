public class foo {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

        Logger.d(TAG, "Bond state changed for: " + device.getAddress() + " new state: " + bondState + " previous: " + previousBondState);

        // skip other devices
        if (!device.getAddress().equals(mBluetoothGatt.getDevice().getAddress()))
            return;

        if (bondState == BluetoothDevice.BOND_BONDED) {
            // Continue to do what you've started before
            enableGlucoseMeasurementNotification(mBluetoothGatt);

            mContext.unregisterReceiver(this);
            mCallbacks.onBonded();
        }
    }
}