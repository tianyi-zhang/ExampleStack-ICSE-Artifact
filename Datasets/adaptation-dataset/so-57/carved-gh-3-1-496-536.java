public class foo{
	// http://stackoverflow.com/questions/1179672/unlimited-strength-jce-policy-files
	public static void removeCryptographyRestrictions() {
		Security.addProvider(new BouncyCastleProvider());
	    if (!isRestrictedCryptography()) {
	        return;
	    }
	    try {
	        /*
	         * Do the following, but with reflection to bypass access checks:
	         *
	         * JceSecurity.isRestricted = false;
	         * JceSecurity.defaultPolicy.perms.clear();
	         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
	         */
	        final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
	        final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
	        final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

	        final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
	        isRestrictedField.setAccessible(true);
	        isRestrictedField.set(null, false);

	        final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
	        defaultPolicyField.setAccessible(true);
	        final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

	        final Field perms = cryptoPermissions.getDeclaredField("perms");
	        perms.setAccessible(true);
	        ((Map<?, ?>) perms.get(defaultPolicy)).clear();

	        final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
	        instance.setAccessible(true);
	        defaultPolicy.add((Permission) instance.get(null));
	    } catch (final Exception e) {
	    	throw new RuntimeException("could not remove cryptography restrictions; " 
	    			+"try to install the \"Java Cryptography Extension (JCE) Unlimited " +
					"Strength Jurisdiction Policy Files\". Download available at " +
					"http://www.oracle.com/technetwork/java/javase/downloads/index.html\n\n"
					+"\ndetailed error message: " +e.getMessage());
	    }
	}
}