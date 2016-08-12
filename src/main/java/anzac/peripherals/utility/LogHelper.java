package anzac.peripherals.utility;

import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import anzac.peripherals.reference.Reference;

public class LogHelper {
	private static void log(final Level logLevel, final String message) {
		FMLLog.log(Reference.MOD_NAME, logLevel, message);
	}

	private static void logException(final Level logLevel, final Throwable ex, final String message) {
		FMLLog.log(Reference.MOD_NAME, logLevel, ex, message);
	}

	public static void all(final String message) {
		log(Level.ALL, message);
	}

	public static void all(final String message, final Throwable ex) {
		logException(Level.ALL, ex, message);
	}

	public static void debug(final String message) {
		log(Level.DEBUG, message);
	}

	public static void debug(final String message, final Throwable ex) {
		logException(Level.DEBUG, ex, message);
	}

	public static void error(final String message) {
		log(Level.ERROR, message);
	}

	public static void error(final String message, final Throwable ex) {
		logException(Level.ERROR, ex, message);
	}

	public static void fatal(final String message) {
		log(Level.FATAL, message);
	}

	public static void fatal(final String message, final Throwable ex) {
		logException(Level.FATAL, ex, message);
	}

	public static void info(final String message) {
		log(Level.INFO, message);
	}

	public static void info(final String message, final Throwable ex) {
		logException(Level.INFO, ex, message);
	}

	public static void off(final String message) {
		log(Level.OFF, message);
	}

	public static void off(final String message, final Throwable ex) {
		logException(Level.OFF, ex, message);
	}

	public static void trace(final String message) {
		log(Level.TRACE, message);
	}

	public static void trace(final String message, final Throwable ex) {
		logException(Level.TRACE, ex, message);
	}

	public static void warn(final String message) {
		log(Level.WARN, message);
	}

	public static void warn(final String message, final Throwable ex) {
		logException(Level.WARN, ex, message);
	}
}
