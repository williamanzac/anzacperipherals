package anzac.peripherals.init;

import static cpw.mods.fml.common.registry.GameRegistry.registerBlock;
import static cpw.mods.fml.common.registry.GameRegistry.registerTileEntity;
import anzac.peripherals.block.Anvil;
import anzac.peripherals.block.BaseBlock;
import anzac.peripherals.block.BrewingStation;
import anzac.peripherals.block.ChargingStation;
import anzac.peripherals.block.Chat;
import anzac.peripherals.block.CraftingRouter;
import anzac.peripherals.block.Enchanter;
import anzac.peripherals.block.FluidRouter;
import anzac.peripherals.block.FluidStorage;
import anzac.peripherals.block.ItemRouter;
import anzac.peripherals.block.ItemStorage;
import anzac.peripherals.block.Note;
import anzac.peripherals.block.RecipeStorage;
import anzac.peripherals.block.Redstone;
import anzac.peripherals.block.RemoteProxy;
import anzac.peripherals.block.TurtleTeleporter;
import anzac.peripherals.block.Workbench;
import anzac.peripherals.item.TieredItem;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.AnvilTileEntity;
import anzac.peripherals.tile.BrewingStationTileEntity;
import anzac.peripherals.tile.ChatTileEntity;
import anzac.peripherals.tile.CraftingRouterTileEntity;
import anzac.peripherals.tile.DiamondChargeStationTileEntity;
import anzac.peripherals.tile.DiamondTurtleTeleporterTileEntity;
import anzac.peripherals.tile.EnchanterTileEntity;
import anzac.peripherals.tile.FluidRouterTileEntity;
import anzac.peripherals.tile.FluidStorageTileEntity;
import anzac.peripherals.tile.GoldChargeStationTileEntity;
import anzac.peripherals.tile.GoldTurtleTeleporterTileEntity;
import anzac.peripherals.tile.IronChargeStationTileEntity;
import anzac.peripherals.tile.IronTurtleTeleporterTileEntity;
import anzac.peripherals.tile.ItemRouterTileEntity;
import anzac.peripherals.tile.ItemStorageTileEntity;
import anzac.peripherals.tile.NoteTileEntity;
import anzac.peripherals.tile.RecipeStorageTileEntity;
import anzac.peripherals.tile.RedstoneTileEntity;
import anzac.peripherals.tile.RemoteProxyTileEntity;
import anzac.peripherals.tile.WorkbenchTileEntity;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(Reference.MOD_ID)
public class ModBlocks {

	public static final BaseBlock workbench = new Workbench();
	public static final BaseBlock recipestorage = new RecipeStorage();
	public static final BaseBlock itemrouter = new ItemRouter();
	public static final BaseBlock fluidrouter = new FluidRouter();
	public static final BaseBlock itemstorage = new ItemStorage();
	public static final BaseBlock fluidstorage = new FluidStorage();
	public static final BaseBlock redstone = new Redstone();
	public static final BaseBlock note = new Note();
	public static final BaseBlock craftingrouter = new CraftingRouter();
	public static final ChargingStation chargestation = new ChargingStation();
	public static final TurtleTeleporter turtleteleporter = new TurtleTeleporter();
	public static final RemoteProxy remoteproxy = new RemoteProxy();
	public static final BrewingStation brewingstation = new BrewingStation();
	public static final Enchanter enchanter = new Enchanter();
	public static final Anvil anvil = new Anvil();
	public static final BaseBlock chat = new Chat();

	public static void init() {
		registerBlock(workbench, Names.Blocks.workbench);
		registerBlock(recipestorage, Names.Blocks.recipestorage);
		registerBlock(itemrouter, Names.Blocks.itemrouter);
		registerBlock(fluidrouter, Names.Blocks.fluidrouter);
		registerBlock(itemstorage, Names.Blocks.itemstorage);
		registerBlock(fluidstorage, Names.Blocks.fluidstorage);
		registerBlock(redstone, Names.Blocks.redstone);
		registerBlock(note, Names.Blocks.note);
		registerBlock(craftingrouter, Names.Blocks.craftingrouter);
		registerBlock(chargestation, TieredItem.class, Names.Blocks.chargestation);
		registerBlock(turtleteleporter, TieredItem.class, Names.Blocks.turtleteleporter);
		registerBlock(remoteproxy, Names.Blocks.remoteproxy);
		registerBlock(brewingstation, Names.Blocks.brewingstation);
		registerBlock(enchanter, Names.Blocks.enchanter);
		registerBlock(anvil, Names.Blocks.anvil);
		registerBlock(chat, Names.Blocks.chat);

		registerTileEntity(NoteTileEntity.class, "anzac.peripherals.tile.NoteTileEntity");
		registerTileEntity(RedstoneTileEntity.class, "anzac.peripherals.tile.RedstoneTileEntity");
		registerTileEntity(WorkbenchTileEntity.class, "anzac.peripherals.tile.WorkbenchTileEntity");
		registerTileEntity(ItemStorageTileEntity.class, "anzac.peripherals.tile.ItemStorageTileEntity");
		registerTileEntity(ItemRouterTileEntity.class, "anzac.peripherals.tile.ItemRouterTileEntity");
		registerTileEntity(CraftingRouterTileEntity.class, "anzac.peripherals.tile.CraftingRouterTileEntity");
		registerTileEntity(IronChargeStationTileEntity.class, "anzac.peripherals.tile.IronChargeStationTileEntity");
		registerTileEntity(GoldChargeStationTileEntity.class, "anzac.peripherals.tile.GoldChargeStationTileEntity");
		registerTileEntity(DiamondChargeStationTileEntity.class,
				"anzac.peripherals.tile.DiamondChargeStationTileEntity");
		registerTileEntity(IronTurtleTeleporterTileEntity.class,
				"anzac.peripherals.tile.IronTurtleTeleporterTileEntity");
		registerTileEntity(GoldTurtleTeleporterTileEntity.class,
				"anzac.peripherals.tile.GoldTurtleTeleporterTileEntity");
		registerTileEntity(DiamondTurtleTeleporterTileEntity.class,
				"anzac.peripherals.tile.DiamondTurtleTeleporterTileEntity");
		registerTileEntity(FluidRouterTileEntity.class, "anzac.peripherals.tile.FluidRouterTileEntity");
		registerTileEntity(FluidStorageTileEntity.class, "anzac.peripherals.tile.FluidStorageTileEntity");
		registerTileEntity(RecipeStorageTileEntity.class, "anzac.peripherals.tile.RecipeStorageTileEntity");
		registerTileEntity(RemoteProxyTileEntity.class, "anzac.peripherals.tile.RemoteProxyTileEntity");
		registerTileEntity(BrewingStationTileEntity.class, "anzac.peripherals.tile.BrewingTileEntity");
		registerTileEntity(EnchanterTileEntity.class, "anzac.peripherals.tile.EnchanterTileEntity");
		registerTileEntity(AnvilTileEntity.class, "anzac.peripherals.tile.AnvilTileEntity");
		registerTileEntity(ChatTileEntity.class, "anzac.peripherals.tile.ChatTileEntity");
	}
}
