package anzac.peripherals.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.peripherals.Recipe;
import anzac.peripherals.utility.InvUtils;

@Peripheral(type = "craftingrouter")
public class CraftingRouterTileEntity extends ItemRouterTileEntity {

	public static class CraftingRecipe extends Recipe {
		public ItemStack[] craftResult = new ItemStack[3];
	}

	private SimpleInventory inv = new SimpleInventory(9, "Crafting Router", 64);

	public InternalInventoryCrafting craftMatrix = new InternalInventoryCrafting(3, inv);
	public IInventory craftResult = new SimpleInventory(3, "", 64);

	public CraftingRouterTileEntity() {
		inv.addListner(this);
	}

	protected SimpleInventory getInventory() {
		return inv;
	}

	/**
	 * @param recipe
	 * @throws Exception
	 */
	@PeripheralMethod
	public boolean setRecipe(final CraftingRecipe recipe) throws Exception {
		clear();
		for (int i = 0; i < recipe.craftMatrix.length; i++) {
			final ItemStack info = recipe.craftMatrix[i];
			if (info != null) {
				craftMatrix.setInventorySlotContents(i, info);
			}
		}
		for (int i = 0; i < recipe.craftResult.length; i++) {
			final ItemStack info = recipe.craftResult[i];
			if (info != null) {
				craftResult.setInventorySlotContents(i, info);
			}
		}
		return hasValidRecipe();
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public CraftingRecipe getRecipe() {
		final CraftingRecipe recipe = new CraftingRecipe();
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			final ItemStack itemStack = craftMatrix.getStackInSlot(i);
			if (itemStack != null) {
				recipe.craftMatrix[i] = itemStack;
			}
		}
		for (int i = 0; i < craftResult.getSizeInventory(); i++) {
			final ItemStack itemStack = craftResult.getStackInSlot(i);
			if (itemStack != null) {
				recipe.craftResult[i] = itemStack;
			}
		}
		return recipe;
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public boolean hasValidRecipe() {
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			final ItemStack itemStack = craftMatrix.getStackInSlot(i);
			if (itemStack != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Clears the current recipe.
	 */
	@PeripheralMethod
	public void clear() {
		craftMatrix.clear();
		for (int i = 0; i < craftResult.getSizeInventory(); i++) {
			craftResult.setInventorySlotContents(i, null);
		}
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public boolean hasIngredients() {
		return craftMatrix.hasIngredients();
	}

	/**
	 * @param side
	 * @throws Exception
	 */
	@PeripheralMethod
	public void craft(final ForgeDirection side) throws Exception {
		craft(side, side.getOpposite());
	}

	/**
	 * @param side
	 * @param inputDir
	 * @throws Exception
	 */
	@PeripheralMethod
	public void craft(final ForgeDirection side, final ForgeDirection inputDir) throws Exception {
		if (!hasValidRecipe()) {
			throw new Exception("nothing to craft");
		}
		if (!hasIngredients()) {
			throw new Exception("does not have ingredients");
		}
		craftMatrix.setCrafting(true);
		routeIngredents(side, inputDir);
		craftMatrix.setCrafting(false);
	}

	private void routeIngredents(final ForgeDirection toDir, final ForgeDirection insertDir) throws Exception {
		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			final ItemStack stackInSlot = craftMatrix.getStackInSlot(i);
			if (stackInSlot != null) {
				final ItemStack copy = stackInSlot.copy();
				final int size = copy.stackSize;
				copy.stackSize -= InvUtils.routeTo(worldObj, xCoord, yCoord, zCoord, toDir, insertDir, copy);
				final int toDec = size - copy.stackSize;
				if (toDec > 0) {
					craftMatrix.decrStackSize(i, toDec);
				} else {
					throw new Exception("Unable to transfer all items to target.");
				}
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return InvUtils.stacksMatch(itemstack, craftMatrix.getStackInSlot(i));
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		getInventory().setInventorySlotContents(slot, stack);
	}
}
