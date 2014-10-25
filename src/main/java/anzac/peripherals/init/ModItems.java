package anzac.peripherals.init;

import anzac.peripherals.item.AdvancedPeripheralFrame;
import anzac.peripherals.item.AdvancedProcessor;
import anzac.peripherals.item.BaseItem;
import anzac.peripherals.item.BasicPeripheralFrame;
import anzac.peripherals.item.BasicProcessor;
import anzac.peripherals.item.FluidStorageUnit;
import anzac.peripherals.item.HDD;
import anzac.peripherals.item.ItemStorageUnit;
import anzac.peripherals.item.MemoryCard;
import anzac.peripherals.item.TeleportFrame;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(Reference.MOD_ID)
public class ModItems {

	public static final BaseItem basiccpu = new BasicProcessor();
	public static final BaseItem advancedcpu = new AdvancedProcessor();
	public static final BaseItem hdd = new HDD();
	public static final BaseItem basicframe = new BasicPeripheralFrame();
	public static final BaseItem advancedframe = new AdvancedPeripheralFrame();
	public static final BaseItem teleportframe = new TeleportFrame();
	public static final BaseItem memorycard = new MemoryCard();
	public static final BaseItem itemstorageunit = new ItemStorageUnit();
	public static final BaseItem fluidstorageunit = new FluidStorageUnit();

	public static void init() {
		GameRegistry.registerItem(basiccpu, Names.Items.basiccpu);
		GameRegistry.registerItem(advancedcpu, Names.Items.advancedcpu);
		GameRegistry.registerItem(hdd, Names.Items.hdd);
		GameRegistry.registerItem(basicframe, Names.Items.basicframe);
		GameRegistry.registerItem(advancedframe, Names.Items.advancedframe);
		GameRegistry.registerItem(teleportframe, Names.Items.teleportframe);
		GameRegistry.registerItem(memorycard, Names.Items.memorycard);
		GameRegistry.registerItem(itemstorageunit, Names.Items.itemstorageunit);
		GameRegistry.registerItem(fluidstorageunit, Names.Items.fluidstorageunit);
	}
}
