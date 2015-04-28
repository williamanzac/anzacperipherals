package anzac.peripherals.tile;

import static dan200.computercraft.api.ComputerCraftAPI.createSaveDirMount;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.handler.ConfigurationHandler;
import anzac.peripherals.peripherals.Recipe;
import anzac.peripherals.peripherals.RecipeDAO;
import anzac.peripherals.utility.InvUtils;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Peripheral(type = "craftingrouter")
public class CraftingRouterTileEntity extends ItemRouterTileEntity {
	private static final String SLOT = "slot";
	private static final String MATRIX = "matrix";
	private static final String RESULT = "result";

	private SimpleInventory inv = new SimpleInventory(9, "Crafting Router", 64);

	public InternalInventoryCrafting craftMatrix = new InternalInventoryCrafting(3, inv, 64);
	public IInventory craftResult = new SimpleInventory(3, "", 64);

	private final RecipeDAO recipeDAO = new RecipeDAO(getMount());

	public CraftingRouterTileEntity() {
		inv.addListener(this);
	}

	protected SimpleInventory getInventory() {
		return inv;
	}

	private IWritableMount getMount() {
		return createSaveDirMount(worldObj, "anzac/hdd/recipies", ConfigurationHandler.hddSize);
	}

	@PeripheralMethod
	public Recipe getRecipe(final int id) throws Exception {
		return recipeDAO.read(id);
	}

	@PeripheralMethod
	public boolean removeRecipe(final int id) throws Exception {
		recipeDAO.remove(id);
		return true;
	}

	@PeripheralMethod
	public boolean storeRecipe() throws Exception {
		if (!hasValidRecipe()) {
			return false;
		}
		recipeDAO.create(craftResult, craftMatrix);
		markDirty();
		return true;
	}

	/**
	 * @param recipe
	 * @throws Exception
	 */
	@PeripheralMethod
	public boolean setRecipe(final Recipe recipe) throws Exception {
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
	public Recipe getRecipe() {
		final Recipe recipe = new Recipe();
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
		try {
			for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
				final ItemStack stackInSlot = craftMatrix.getStackInSlot(i);
				if (stackInSlot != null) {
					final ItemStack copy = stackInSlot.copy();
					final int size = copy.stackSize;
					copy.stackSize -= InvUtils.routeTo(worldObj, xCoord, yCoord, zCoord, side, inputDir, copy);
					final int toDec = size - copy.stackSize;
					if (toDec > 0) {
						craftMatrix.decrStackSize(i, toDec);
					} else {
						throw new Exception("Unable to transfer all items to target.");
					}
				}
			}
		} finally {
			craftMatrix.setCrafting(false);
		}
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return InvUtils.itemMatched(itemstack, craftMatrix.getStackInSlot(i), true, true, true);
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		getInventory().setInventorySlotContents(slot, stack);
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		// read craft slots
		if (tagCompound.hasKey(MATRIX)) {
			final NBTTagList list = tagCompound.getTagList(MATRIX, Constants.NBT.TAG_COMPOUND);
			for (byte entry = 0; entry < list.tagCount(); entry++) {
				final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
				final int slot = itemTag.getByte(SLOT);
				if (slot >= 0 && slot < craftMatrix.getSizeInventory()) {
					final ItemStack stack = loadItemStackFromNBT(itemTag);
					craftMatrix.setInventorySlotContents(slot, stack);
				}
			}
		}
		// read result slots
		if (tagCompound.hasKey(RESULT)) {
			final NBTTagList list = tagCompound.getTagList(RESULT, Constants.NBT.TAG_COMPOUND);
			for (byte entry = 0; entry < list.tagCount(); entry++) {
				final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
				final int slot = itemTag.getByte(SLOT);
				if (slot >= 0 && slot < craftResult.getSizeInventory()) {
					final ItemStack stack = loadItemStackFromNBT(itemTag);
					craftResult.setInventorySlotContents(slot, stack);
				}
			}
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		// write craft slots
		NBTTagList list = new NBTTagList();
		for (byte slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			final ItemStack stack = craftMatrix.getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(SLOT, slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag(MATRIX, list);

		// write result slots
		list = new NBTTagList();
		for (byte slot = 0; slot < craftResult.getSizeInventory(); slot++) {
			final ItemStack stack = craftResult.getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(SLOT, slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag(RESULT, list);
	}

	@Override
	public void attach(final IComputerAccess computer) {
		super.attach(computer);
		final IWritableMount mount = getMount();
		computer.mountWritable("/recipies", mount);
		recipeDAO.setMount(mount);
	}

	@Override
	public void detach(final IComputerAccess computer) {
		super.detach(computer);
		computer.unmount("/recipies");
	}
}
