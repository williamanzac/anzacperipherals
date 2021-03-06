package anzac.peripherals.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes {

	public static void init() {
		final Object ironIngot = getOreDict(Items.iron_ingot);
		final Object redstoneDust = getOreDict(Items.redstone);
		final Object obsidian = getOreDict(Blocks.obsidian);
		final Object goldIngot = getOreDict(Items.gold_ingot);
		final Object enderPearl = getOreDict(Items.ender_pearl);
		final Object paper = getOreDict(Items.paper);
		final Object chest = getOreDict(Blocks.chest);
		final Object cauldron = Blocks.cauldron;
		final Object workbench = getOreDict(Blocks.crafting_table);
		final Object hopper = getOreDict(Blocks.hopper);
		final Object bucket = getOreDict(Items.bucket);
		final Object redstoneBlock = getOreDict(Blocks.redstone_block);
		final Object noteblock = getOreDict(Blocks.noteblock);
		final Object diamond = getOreDict(Items.diamond);
		final Object glass = getOreDict(Blocks.glass);
		final Object furnace = getOreDict(Blocks.furnace);
		final Object enchantingTable = getOreDict(Blocks.enchanting_table);
		final Object anvil = getOreDict(Blocks.anvil);

		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.basiccpu, " r ", "rir", " r ", 'i', ironIngot, 'r',
				redstoneDust));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.advancedcpu, " r ", "rgr", " r ", 'g', goldIngot, 'r',
				redstoneDust));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModItems.hdd, ironIngot, redstoneDust, ModItems.basiccpu));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.basicframe, "sis", "ibi", "sis", 'i', ironIngot, 's',
				"stone", 'b', ModItems.basiccpu));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.advancedframe, "sgs", "gag", "sgs", 'g', goldIngot, 's',
				"stone", 'a', ModItems.advancedcpu));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.teleportframe, "oeo", "eae", "oeo", 'o', obsidian, 'e',
				enderPearl, 'a', ModItems.advancedcpu));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModItems.memorycard, ModItems.basiccpu, paper));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModItems.itemstorageunit, ModItems.memorycard, chest,
				ModItems.advancedcpu));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModItems.fluidstorageunit, ModItems.memorycard, cauldron,
				ModItems.advancedcpu));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.workbench, "iwi", "ifi", "isi", 'i', ironIngot, 'w',
				workbench, 'f', ModItems.basicframe, 's', ModItems.itemstorageunit));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.recipestorage, "iwi", "ifi", "ihi", 'i', ironIngot, 'w',
				workbench, 'f', ModItems.basicframe, 'h', ModItems.hdd));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.itemrouter, "ihi", "iai", "isi", 'h', hopper, 'i',
				ironIngot, 'a', ModItems.advancedframe, 's', ModItems.itemstorageunit));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.fluidrouter, "bib", "iai", "isi", 'b', bucket, 'i',
				ironIngot, 'a', ModItems.advancedframe, 's', ModItems.fluidstorageunit));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.itemstorage, "ihi", "iai", "isi", 'a',
				ModItems.advancedframe, 's', ModItems.itemstorageunit, 'i', ironIngot, 'h', ModItems.hdd));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.fluidstorage, "ihi", "iai", "isi", 'a',
				ModItems.advancedframe, 's', ModItems.fluidstorageunit, 'i', ironIngot, 'h', ModItems.hdd));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.redstone, "iri", "rar", "iri", 'i', ironIngot, 'a',
				ModItems.advancedframe, 'r', redstoneBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.note, "ini", "nbn", "ini", 'i', ironIngot, 'b',
				ModItems.basicframe, 'n', noteblock));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.craftingrouter), ModBlocks.itemrouter,
				ModBlocks.recipestorage, ModBlocks.workbench);
		final ItemStack ironchargestation = new ItemStack(ModBlocks.chargestation, 1, 1);
		final ItemStack goldchargestation = new ItemStack(ModBlocks.chargestation, 1, 2);
		final ItemStack diamondchargestation = new ItemStack(ModBlocks.chargestation, 1, 3);
		GameRegistry.addRecipe(new ShapedOreRecipe(ironchargestation, "iri", "rpr", "ifi", 'i', ironIngot, 'r',
				redstoneBlock, 'p', ModBlocks.redstone, 'f', furnace));
		GameRegistry.addRecipe(new ShapedOreRecipe(goldchargestation, "iri", "rpr", "ifi", 'i', goldIngot, 'r',
				redstoneBlock, 'p', ironchargestation, 'f', furnace));
		GameRegistry.addRecipe(new ShapedOreRecipe(diamondchargestation, "iri", "rpr", "ifi", 'i', diamond, 'r',
				redstoneBlock, 'p', goldchargestation, 'f', furnace));
		final ItemStack ironturtleteleporter = new ItemStack(ModBlocks.turtleteleporter, 1, 1);
		final ItemStack goldturtleteleporter = new ItemStack(ModBlocks.turtleteleporter, 1, 2);
		final ItemStack diamondturtleteleporter = new ItemStack(ModBlocks.turtleteleporter, 1, 3);
		GameRegistry.addRecipe(new ShapedOreRecipe(ironturtleteleporter, "igi", "gpg", "igi", 'i', ironIngot, 'g',
				glass, 'p', ModItems.teleportframe));
		GameRegistry.addRecipe(new ShapedOreRecipe(goldturtleteleporter, "iri", "rpr", "iri", 'i', goldIngot, 'r',
				enderPearl, 'p', ironturtleteleporter));
		GameRegistry.addRecipe(new ShapedOreRecipe(diamondturtleteleporter, "iri", "rpr", "iri", 'i', diamond, 'r',
				enderPearl, 'p', goldturtleteleporter));
		final Block peripheral = GameRegistry.findBlock("ComputerCraft", "CC-Peripheral");
		final Block cable = GameRegistry.findBlock("ComputerCraft", "CC-Cable");
		final ItemStack wirelessStack = new ItemStack(peripheral, 1, 1);
		final ItemStack wiredStack = new ItemStack(cable, 1, 1);
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.remoteproxy, ModItems.basicframe, wirelessStack,
				wiredStack));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.brewingstation, "ici", "bBb", "ifi", 'i', ironIngot, 'c',
				ModItems.itemstorageunit, 'b', Blocks.brewing_stand, 'B', ModItems.basicframe, 'f',
				ModItems.fluidstorageunit));
		GameRegistry.addRecipe(new ShapelessOreRecipe(ModItems.furnaceupgrade, ModItems.basicframe, Blocks.furnace));
		if (Loader.isModLoaded("EnderStorage")) {
			final Block block = GameRegistry.findBlock("EnderStorage", "enderChest");
			GameRegistry.addRecipe(new ShapelessOreRecipe(ModItems.enderchestupgrade, ModItems.advancedframe, block));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.enchanter, "gtg", "fai", "gtg", 'g', goldIngot, 't',
				enchantingTable, 'f', ModItems.fluidstorageunit, 'a', ModItems.advancedframe, 'i',
				ModItems.itemstorageunit));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.anvil, "gtg", "fai", "gtg", 'g', goldIngot, 't', anvil,
				'f', ModItems.fluidstorageunit, 'a', ModItems.advancedframe, 'i', ModItems.itemstorageunit));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.chat, "gng", "nan", "gng", 'g', goldIngot, 'a',
				ModItems.advancedframe, 'n', noteblock));
	}

	private static Object getOreDict(final Block block) {
		return getOreDict(new ItemStack(block));
	}

	private static Object getOreDict(final Item item) {
		return getOreDict(new ItemStack(item));
	}

	private static Object getOreDict(final ItemStack stack) {
		final int[] oreIDs = OreDictionary.getOreIDs(stack);
		if (oreIDs.length == 0) {
			return stack;
		}
		return OreDictionary.getOreName(oreIDs[0]);
	}
}
