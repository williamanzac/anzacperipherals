package anzac.peripherals.init;

import anzac.peripherals.block.BaseBlock;
import anzac.peripherals.block.ChargingStation;
import anzac.peripherals.block.CraftingRouter;
import anzac.peripherals.block.FluidRouter;
import anzac.peripherals.block.FluidStorage;
import anzac.peripherals.block.ItemRouter;
import anzac.peripherals.block.ItemStorage;
import anzac.peripherals.block.Note;
import anzac.peripherals.block.RecipeStorage;
import anzac.peripherals.block.Redstone;
import anzac.peripherals.block.TurtleTeleporter;
import anzac.peripherals.block.Workbench;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference;
import anzac.peripherals.tile.CraftingRouterTileEntity;
import anzac.peripherals.tile.DiamondChargeStationTileEntity;
import anzac.peripherals.tile.GoldChargeStationTileEntity;
import anzac.peripherals.tile.IronChargeStationTileEntity;
import anzac.peripherals.tile.ItemRouterTileEntity;
import anzac.peripherals.tile.ItemStorageTileEntity;
import anzac.peripherals.tile.NoteTileEntity;
import anzac.peripherals.tile.RedstoneTileEntity;
import anzac.peripherals.tile.WorkbenchTileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
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
	public static final ChargingStation ironchargestation = new ChargingStation(1);
	public static final ChargingStation goldchargestation = new ChargingStation(2);
	public static final ChargingStation diamondchargestation = new ChargingStation(3);
	public static final TurtleTeleporter ironturtleteleporter = new TurtleTeleporter(1);
	public static final TurtleTeleporter goldturtleteleporter = new TurtleTeleporter(2);
	public static final TurtleTeleporter diamondturtleteleporter = new TurtleTeleporter(3);

	public static void init() {
		GameRegistry.registerBlock(workbench, Names.Blocks.workbench);
		GameRegistry.registerBlock(recipestorage, Names.Blocks.recipestorage);
		GameRegistry.registerBlock(itemrouter, Names.Blocks.itemrouter);
		GameRegistry.registerBlock(fluidrouter, Names.Blocks.fluidrouter);
		GameRegistry.registerBlock(itemstorage, Names.Blocks.itemstorage);
		GameRegistry.registerBlock(fluidstorage, Names.Blocks.fluidstorage);
		GameRegistry.registerBlock(redstone, Names.Blocks.redstone);
		GameRegistry.registerBlock(note, Names.Blocks.note);
		GameRegistry.registerBlock(craftingrouter, Names.Blocks.craftingrouter);
		GameRegistry.registerBlock(ironchargestation, Names.Blocks.ironchargestation);
		GameRegistry.registerBlock(goldchargestation, Names.Blocks.goldchargestation);
		GameRegistry.registerBlock(diamondchargestation, Names.Blocks.diamondchargestation);
		GameRegistry.registerBlock(ironturtleteleporter, Names.Blocks.ironturtleteleporter);
		GameRegistry.registerBlock(goldturtleteleporter, Names.Blocks.goldturtleteleporter);
		GameRegistry.registerBlock(diamondturtleteleporter, Names.Blocks.diamondturtleteleporter);

		GameRegistry.registerTileEntity(NoteTileEntity.class, "anzac.peripherals.tile.NoteTileEntity");
		GameRegistry.registerTileEntity(RedstoneTileEntity.class, "anzac.peripherals.tile.RedstoneTileEntity");
		GameRegistry.registerTileEntity(WorkbenchTileEntity.class, "anzac.peripherals.tile.WorkbenchTileEntity");
		GameRegistry.registerTileEntity(ItemStorageTileEntity.class, "anzac.peripherals.tile.ItemStorageTileEntity");
		GameRegistry.registerTileEntity(ItemRouterTileEntity.class, "anzac.peripherals.tile.ItemRouterTileEntity");
		GameRegistry.registerTileEntity(CraftingRouterTileEntity.class,
				"anzac.peripherals.tile.CraftingRouterTileEntity");
		GameRegistry.registerTileEntity(IronChargeStationTileEntity.class,
				"anzac.peripherals.tile.IronChargeStationTileEntity");
		GameRegistry.registerTileEntity(GoldChargeStationTileEntity.class,
				"anzac.peripherals.tile.GoldChargeStationTileEntity");
		GameRegistry.registerTileEntity(DiamondChargeStationTileEntity.class,
				"anzac.peripherals.tile.DiamondChargeStationTileEntity");
	}
}
