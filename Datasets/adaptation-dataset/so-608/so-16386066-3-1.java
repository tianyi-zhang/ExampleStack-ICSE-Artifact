public class foo {
    @Override
    public void onSensorChanged(SensorEvent evnt) {
        int SensorType = evnt.sensor.getType();
        switch(SensorType) {
            case Sensor.TYPE_GRAVITY:
                if (m_NormGravityVector == null) m_NormGravityVector = new float[3];
                System.arraycopy(evnt.values, 0, m_NormGravityVector, 0, m_NormGravityVector.length);                   
                m_Norm_Gravity = (float)Math.sqrt(m_NormGravityVector[0]*m_NormGravityVector[0] + m_NormGravityVector[1]*m_NormGravityVector[1] + m_NormGravityVector[2]*m_NormGravityVector[2]);
                for(int i=0; i < m_NormGravityVector.length; i++) m_NormGravityVector[i] /= m_Norm_Gravity;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (m_NormMagFieldValues == null) m_NormMagFieldValues = new float[3];
                System.arraycopy(evnt.values, 0, m_NormMagFieldValues, 0, m_NormMagFieldValues.length);
                m_Norm_MagField = (float)Math.sqrt(m_NormMagFieldValues[0]*m_NormMagFieldValues[0] + m_NormMagFieldValues[1]*m_NormMagFieldValues[1] + m_NormMagFieldValues[2]*m_NormMagFieldValues[2]);
                for(int i=0; i < m_NormMagFieldValues.length; i++) m_NormMagFieldValues[i] /= m_Norm_MagField;  
                break;
        }
        if (m_NormGravityVector != null && m_NormMagFieldValues != null) {
            // first calculate the horizontal vector that points due east
            float East_x = m_NormMagFieldValues[1]*m_NormGravityVector[2] - m_NormMagFieldValues[2]*m_NormGravityVector[1];
            float East_y = m_NormMagFieldValues[2]*m_NormGravityVector[0] - m_NormMagFieldValues[0]*m_NormGravityVector[2];
            float East_z = m_NormMagFieldValues[0]*m_NormGravityVector[1] - m_NormMagFieldValues[1]*m_NormGravityVector[0];
            float norm_East = (float)Math.sqrt(East_x * East_x + East_y * East_y + East_z * East_z);
            if (m_Norm_Gravity * m_Norm_MagField * norm_East < 0.1f) {  // Typical values are  > 100.
                m_OrientationOK = false; // device is close to free fall (or in space?), or close to magnetic north pole.
            } else {
                m_NormEastVector[0] = East_x / norm_East; m_NormEastVector[1] = East_y / norm_East; m_NormEastVector[2] = East_z / norm_East;

                // next calculate the horizontal vector that points due north                   
                float M_dot_G = (m_NormGravityVector[0] *m_NormMagFieldValues[0] + m_NormGravityVector[1]*m_NormMagFieldValues[1] + m_NormGravityVector[2]*m_NormMagFieldValues[2]);
                float North_x = m_NormMagFieldValues[0] - m_NormGravityVector[0] * M_dot_G;
                float North_y = m_NormMagFieldValues[1] - m_NormGravityVector[1] * M_dot_G;
                float North_z = m_NormMagFieldValues[2] - m_NormGravityVector[2] * M_dot_G;
                float norm_North = (float)Math.sqrt(North_x * North_x + North_y * North_y + North_z * North_z);
                m_NormNorthVector[0] = North_x / norm_North; m_NormNorthVector[1] = North_y / norm_North; m_NormNorthVector[2] = North_z / norm_North;

                // take account of screen rotation away from its natural rotation
                int rotation = m_activity.getWindowManager().getDefaultDisplay().getRotation();
                float screen_adjustment = 0;
                switch(rotation) {
                    case Surface.ROTATION_0:   screen_adjustment =          0;         break;
                    case Surface.ROTATION_90:  screen_adjustment =   (float)Math.PI/2; break;
                    case Surface.ROTATION_180: screen_adjustment =   (float)Math.PI;   break;
                    case Surface.ROTATION_270: screen_adjustment = 3*(float)Math.PI/2; break;
                }
                // NB: the rotation matrix has now effectively been calculated. It consists of the three vectors m_NormEastVector[], m_NormNorthVector[] and m_NormGravityVector[]

                // calculate all the required angles from the rotation matrix
                // NB: see http://math.stackexchange.com/questions/381649/whats-the-best-3d-angular-co-ordinate-system-for-working-with-smartfone-apps
                float sin = m_NormEastVector[1] -  m_NormNorthVector[0], cos = m_NormEastVector[0] +  m_NormNorthVector[1];
                m_azimuth_radians = (float) (sin != 0 && cos != 0 ? Math.atan2(sin, cos) : 0);
                m_pitch_radians = (float) Math.acos(m_NormGravityVector[2]);
                sin = -m_NormEastVector[1] -  m_NormNorthVector[0]; cos = m_NormEastVector[0] -  m_NormNorthVector[1];
                float aximuth_plus_two_pitch_axis_radians = (float)(sin != 0 && cos != 0 ? Math.atan2(sin, cos) : 0);
                m_pitch_axis_radians = (float)(aximuth_plus_two_pitch_axis_radians - m_azimuth_radians) / 2;
                m_azimuth_radians += screen_adjustment;
                m_pitch_axis_radians += screen_adjustment;
                m_OrientationOK = true;                                 
            }
        }
        if (m_parent != null) m_parent.onSensorChanged(evnt);
    }
}