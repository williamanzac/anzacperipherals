package anzac.peripherals;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.proxy.IProxy;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.utility.LogHelper;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, acceptedMinecraftVersions = "1.8.9", dependencies = "after:ComputerCraft")
public class Peripherals {
	@Mod.Instance(Reference.MOD_ID)
	public static Peripherals instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxy proxy;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent e) {
		ConfigurationHandler.init(e.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
		proxy.registerKeyBindings();
		// proxy.registerNetwork();
		// ModItems.init();
		// ModBlocks.init();
		// ModFluids.init();
		LogHelper.info("Pre Initialization Complete!");
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent e) {
		// MinecraftForge.EVENT_BUS.register(new
		// KeyInputEventHandler());
		// NetworkRegistry.INSTANCE.registerGuiHandler(instance, new
		// GuiHandler());
		// Recipes.init();
		// AnzacPeripheralProvider.init();
		// AnzacRedstoneProvider.init();
		// TurtleUpgrades.init();
		// MinecraftForge.EVENT_BUS.register(new CommonProxy.ForgeHandlers());
		LogHelper.info("Initialization Complete!");
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent e) {
		LogHelper.info("Post Initialization Complete!");
	}
}
