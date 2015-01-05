package anzac.peripherals.handler;

import static java.lang.Integer.MAX_VALUE;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import anzac.peripherals.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigurationHandler {
	private static final int DEFAULT_STORAGE_SIZE = 1048576;
	private static final int DEFAULT_MJ_MULTIPLIER = 20;
	private static final int DEFAULT_RF_MULTIPLIER = DEFAULT_MJ_MULTIPLIER * 10;
	private static final boolean DEFAULT_MODIFY = true;

	private static Configuration configuration;

	public static int hddSize;
	public static int mjMultiplier;
	public static int rfMultiplier;
	public static boolean modifyCC;

	public static void init(final File configurationFile) {
		if (configuration == null) {
			configuration = new Configuration(configurationFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		hddSize = configuration.getInt("hdd.size", CATEGORY_GENERAL, DEFAULT_STORAGE_SIZE, 0, MAX_VALUE,
				"The disk space limit for Hard Disk Drives");
		mjMultiplier = configuration.getInt("mj.multiplier", CATEGORY_GENERAL, DEFAULT_MJ_MULTIPLIER, 0, MAX_VALUE,
				"Use to convert between mj and turtle moves");
		rfMultiplier = configuration.getInt("rf.multiplier", CATEGORY_GENERAL, DEFAULT_RF_MULTIPLIER, 0, MAX_VALUE,
				"Use to convert between rf and turtle moves");
		modifyCC = configuration.getBoolean("modify.computercraft", CATEGORY_GENERAL, DEFAULT_MODIFY,
				"Modify ComputerCraft recpies");

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (Reference.MOD_ID.equalsIgnoreCase(event.modID)) {
			loadConfiguration();
		}
	}
}
