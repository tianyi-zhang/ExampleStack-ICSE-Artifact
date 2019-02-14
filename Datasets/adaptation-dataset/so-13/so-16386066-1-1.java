public class foo {
    public int Register(Activity activity, int sensorSpeed) {
        m_activity = activity;  // current activity required for call to getWindowManager().getDefaultDisplay().getRotation()
        m_NormGravityVector = new float[3];
        m_NormMagFieldValues = new float[3];
        m_OrientationOK = false;
        int count = 0;
        Sensor SensorGravity = m_sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (SensorGravity != null) {
            m_sm.registerListener(this, SensorGravity, sensorSpeed);
            m_GravityAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
            count++;
        } else {
            m_GravityAccuracy = SENSOR_UNAVAILABLE;
        }
        Sensor SensorMagField = m_sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (SensorMagField != null) {
            m_sm.registerListener(this, SensorMagField, sensorSpeed);
            m_MagneticFieldAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;     
            count++;
        } else {
            m_MagneticFieldAccuracy = SENSOR_UNAVAILABLE;
        }
        return count;
    }
}