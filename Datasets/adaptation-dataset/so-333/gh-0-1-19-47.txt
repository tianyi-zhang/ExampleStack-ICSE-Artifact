package com.jbutewicz.flashlightbyjoe;

import java.lang.reflect.Method;

import android.os.IBinder;

class DroidLED {
	// This class is necessary to turn the LED both on and off on certain
	// Motorola phones. While some phones work with the more standard
	// setFlashMode, this is not universal on all phones. One specific phone
	// that works only based on this class is the Motorola OG Droid. This class
	// is adapted from Siddhpura Amit's answer on
	// stackoverflow.com/questions/5503480/use-camera-flashlight-in-android

	private Object svc = null;
	private Method getFlashlightEnabled = null;
	private Method setFlashlightEnabled = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DroidLED() throws Exception {
		try {
			// call ServiceManager.getService("hardware") to get an IBinder for
			// the service. This appears to be totally undocumented and not
			// exposed in the SDK whatsoever.
			Class sm = Class.forName("android.os.ServiceManager");
			Object hwBinder = sm.getMethod("getService", String.class).invoke(
					null, "hardware");

			// get the hardware service stub. this seems to just get us one
			// step closer to the proxy
			Class hwsstub = Class.forName("android.os.IHardwareService$Stub");
			Method asInterface = hwsstub.getMethod("asInterface",
					android.os.IBinder.class);
			svc = asInterface.invoke(null, (IBinder) hwBinder);

			// grab the class (android.os.IHardwareService$Stub$Proxy) so we
			// can reflect on its methods
			Class proxy = svc.getClass();

			// save methods
			getFlashlightEnabled = proxy.getMethod("getFlashlightEnabled");
			setFlashlightEnabled = proxy.getMethod("setFlashlightEnabled",
					boolean.class);
		} catch (Exception e) {
			throw new Exception("LED could not be initialized");
		}
	}

	public boolean isEnabled() {
		try {
			return getFlashlightEnabled.invoke(svc).equals(true);
		} catch (Exception e) {
			return false;
		}
	}

	public void enable(boolean tf) {
		try {
			setFlashlightEnabled.invoke(svc, tf);
		} catch (Exception e) {
		}
	}
}