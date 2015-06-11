package anzac.peripherals;

import net.minecraftforge.common.MinecraftForge;
import anzac.peripherals.client.handler.KeyInputEventHandler;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.handler.GuiHandler;
import anzac.peripherals.init.AnzacPeripheralProvider;
import anzac.peripherals.init.AnzacRedstoneProvider;
import anzac.peripherals.init.ModBlocks;
import anzac.peripherals.init.ModFluids;
import anzac.peripherals.init.ModItems;
import anzac.peripherals.init.Recipes;
import anzac.peripherals.init.TurtleUpgrades;
import anzac.peripherals.proxy.CommonProxy;
import anzac.peripherals.proxy.IProxy;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.BaseTileEntity;
import anzac.peripherals.utility.LogHelper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "after:ComputerCraft;after:EnderStorage")
public class Peripherals {

	@Mod.Instance(Reference.MOD_ID)
	public static Peripherals instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxy proxy;

	public static final Table<Integer, String, BaseTileEntity> peripheralMappings = HashBasedTable.create();

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent e) {
		ConfigurationHandler.init(e.getSuggestedConfigurationFile());
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
		proxy.registerKeyBindings();
		proxy.registerNetwork();
		ModItems.init();
		ModBlocks.init();
		ModFluids.init();
		LogHelper.info("Pre Initialization Complete!");
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent e) {
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		Recipes.init();
		AnzacPeripheralProvider.init();
		AnzacRedstoneProvider.init();
		TurtleUpgrades.init();
		MinecraftForge.EVENT_BUS.register(new CommonProxy.ForgeHandlers());
		LogHelper.info("Initialization Complete!");
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent e) {
		LogHelper.info("Post Initialization Complete!");
	}
}
