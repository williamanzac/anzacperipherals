package anzac.peripherals.reference;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

public class Reference {
	public static final String MOD_ID = "ANZACPeripherals";
	public static final String MOD_NAME = "ANZAC Peripherals";
	public static final String VERSION = "1.7.10-1.0";
	public static final String CLIENT_PROXY_CLASS = "anzac.peripherals.proxy.ClientProxy";
	public static final String SERVER_PROXY_CLASS = "anzac.peripherals.proxy.ServerProxy";
	public static final String GUI_FACTORY_CLASS = "anzac.peripherals.client.gui.GuiFactory";

	public static GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes("anzac.peripherals".getBytes()),
			"[ANZACPeripherals]");

	public static class GuiIds {
		public static final int WORKBENCH = 1;
		public static final int ITEMSTORAGE = 2;
		public static final int ITEMROUTER = 3;
		public static final int CRAFTINGROUTER = 4;
		public static final int CHARGESTATION = 5;
		public static final int TURTLETELEPORTER = 6;
		public static final int FLUIDROUTER = 7;
		public static final int FLUIDSTORAGE = 8;
		public static final int RECIPESTORAGE = 9;
		public static final int REMOTEPROXY = 10;
		public static final int BREWINGSTATION = 11;
		public static final int ENCHANTER = 12;
		public static final int ANVIL = 13;
	}
}
