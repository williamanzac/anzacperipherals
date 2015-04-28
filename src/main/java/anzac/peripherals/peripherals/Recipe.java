package anzac.peripherals.peripherals;

import net.minecraft.item.ItemStack;

public class Recipe {
	public static enum RecipeType {
		CRAFTING, PROCESSING
	}

	public RecipeType type;
	public final ItemStack[] craftMatrix = new ItemStack[9];
	public ItemStack[] craftResult = new ItemStack[3];
}
