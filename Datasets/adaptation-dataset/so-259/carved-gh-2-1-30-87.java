public class foo{
	/**
	 * Dynamically sets the logback log level for the given class to the specified level.
	 * 
	 * @param loggerName
	 *            Name of the logger to set its log level. If blank, root logger will be used.
	 * @param logLevel
	 *            One of the supported log levels: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF.
	 *            {@code null} value is considered as 'OFF'.
	 */
	public static boolean setLogLevel(String loggerName, String logLevel){
		String logLevelUpper = (logLevel == null) ? "OFF" : logLevel.toUpperCase();
		
		try {
			Package logbackPackage = Package.getPackage(LOGBACK_CLASSIC);
			if (logbackPackage == null) {
				// logger.info("Logback is not in the classpath!");
				// in eclipse logback is not in the classpath!
				// return false;
			}
			
			// Use ROOT logger if given logger name is blank.
			if ((loggerName == null) || loggerName.trim().isEmpty()) {
				loggerName = (String) getFieldVaulue(LOGBACK_CLASSIC_LOGGER, "ROOT_LOGGER_NAME");
			}
			
			// Obtain logger by the name
			Logger loggerObtained = LoggerFactory.getLogger(loggerName);
			if (loggerObtained == null) {
				// I don't know if this case occurs
				logger.warn("No logger for the name: {}", loggerName);
				return false;
			}
			
			Object logLevelObj = getFieldVaulue(LOGBACK_CLASSIC_LEVEL, logLevelUpper);
			if (logLevelObj == null) {
				logger.warn("No such log level: {}", logLevelUpper);
				return false;
			}
			
			Class<?>[] paramTypes = {
				logLevelObj.getClass()
			};
			Object[] params = {
				logLevelObj
			};
			
			Class<?> clz = Class.forName(LOGBACK_CLASSIC_LOGGER);
			Method method = clz.getMethod("setLevel", paramTypes);
			method.invoke(loggerObtained, params);
			
			logger.debug("Log level set to {} for the logger '{}'", logLevelUpper, loggerName);
			return true;
		} catch (Exception e) {
			logger.warn("Couldn't set log level to {} for the logger '{}'", logLevelUpper,
				loggerName, e);
			return false;
		}
	}
}