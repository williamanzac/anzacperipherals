package anzac.peripherals.reference;

public class Names {

	public static class Items {
		public static final String basiccpu = "basiccpu";
		public static final String advancedcpu = "advancedcpu";
		public static final String hdd = "hdd";
		public static final String basicframe = "basicframe";
		public static final String advancedframe = "advancedframe";
		public static final String teleportframe = "teleportframe";
		public static final String memorycard = "memorycard";
		public static final String itemstorageunit = "itemstorageunit";
		public static final String fluidstorageunit = "fluidstorageunit";
		public static final String furnaceupgrade = "furnaceupgrade";
		public static final String enderchestupgrade = "enderchestupgrade";
	}

	public static class Blocks {
		public static final String generic = Reference.MOD_ID.toLowerCase() + ":generic";
		public static final String workbench = "workbench";
		public static final String recipestorage = "recipestorage";
		public static final String itemrouter = "itemrouter";
		public static final String fluidrouter = "fluidrouter";
		public static final String itemstorage = "itemstorage";
		public static final String fluidstorage = "fluidstorage";
		public static final String redstone = "redstone";
		public static final String note = "note";
		public static final String craftingrouter = "craftingrouter";
		public static final String chargestation = "chargestation";
		public static final String ironchargestation = "ironchargestation";
		public static final String goldchargestation = "goldchargestation";
		public static final String diamondchargestation = "diamondchargestation";
		public static final String turtleteleporter = "turtleteleporter";
		public static final String ironturtleteleporter = "ironturtleteleporter";
		public static final String goldturtleteleporter = "goldturtleteleporter";
		public static final String diamondturtleteleporter = "diamondturtleteleporter";
		public static final String remoteproxy = "remoteproxy";
		public static final String brewingstation = "brewingstation";
		public static final String enchanter = "enchanter";
		public static final String anvil = "anvil";
	}

	public static String getItemKey(final String unlocalizedName) {
		return "item." + Reference.MOD_ID.toLowerCase() + ":" + unwrapUnlocalizedName(unlocalizedName);
	}

	public static String getBlockKey(final String unlocalizedName) {
		return "tile." + Reference.MOD_ID.toLowerCase() + ":" + unwrapUnlocalizedName(unlocalizedName);
	}

	public static String getUpgradeKey(final String unlocalizedName) {
		return "upgrade." + unwrapUnlocalizedName(unlocalizedName) + ".adjective";
	}

	public static String unwrapUnlocalizedName(final String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	public static String getGUIKey(final String name) {
		return "tile." + Reference.MOD_ID.toLowerCase() + ":" + name + ".name";
	}
}
