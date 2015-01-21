package anzac.peripherals.tile;

import static cpw.mods.fml.common.registry.GameRegistry.findUniqueIdentifierFor;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.Peripherals;
import anzac.peripherals.annotations.Event;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.peripherals.Recipe;
import anzac.peripherals.utility.InvUtils;

/**
 * This block allows you to craft items via a connected computer. The interface has a crafting area that can only be set
 * via a connected Computer, it also has an internal input and output cache. This block is only usable when connected to
 * a Computer. Use the {@link WorkbenchTileEntity#setRecipe(Recipe)} method to set the desired item to craft. The
 * required items can be injected in to the internal cache or you can manually input the items. Use the
 * {@link WorkbenchTileEntity#craft()} method to craft the item. The crafted item will automatically go in to the output
 * cache. This peripheral should ignore item metadata of the supplied input items.
 */
@Peripheral(type = "workbench")
public class WorkbenchTileEntity extends BaseTileEntity implements ISidedInventory {
	private static final String INVENTORY = "inventory";
	private static final String SLOT = "slot";
	private static final String MATRIX = "matrix";

	public InternalInventoryCrafting craftMatrix = new InternalInventoryCrafting(3, this);
	public IInventory craftResult = new InventoryCraftResult();

	private final IInventory input = new SimpleInventory(9, "", 64);
	public final IInventory output = new SimpleInventory(1, "Computerized Workbench", 64);

	private final IInventory inv = InventoryWrapper.make().add(output).add(input);

	private final int[] SLOTS = InvUtils.createSlotArray(inv);

	private SlotCrafting craftSlot;

	/**
	 * Will set the current recipe for this peripheral.
	 * 
	 * @param recipe
	 *            a table containing the definition of the recipe. The recipe can also be obtained from a connected
	 *            {@link RecipeStorageTileEntity} using the {@link RecipeStorageTileEntity#loadRecipe(int)} method.
	 * @return {@code true} if the recipe was successfully defined.
	 */
	@PeripheralMethod
	public boolean setRecipe(final Recipe recipe) {
		craftMatrix.clear();
		for (int i = 0; i < recipe.craftMatrix.length; i++) {
			final ItemStack info = recipe.craftMatrix[i];
			if (info != null) {
				craftMatrix.setInventorySlotContents(i, info);
			}
		}
		updateCraftingRecipe();
		return hasValidRecipe();
	}

	public void updateCraftingRecipe() {
		final ItemStack matchingRecipe = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj);
		craftResult.setInventorySlotContents(0, matchingRecipe);
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
		return recipe;
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public boolean hasValidRecipe() {
		return craftResult.getStackInSlot(0) != null;
	}

	/**
	 * Clears the current recipe.
	 */
	@PeripheralMethod
	public void clear() {
		craftMatrix.clear();
		updateCraftingRecipe();
	}

	/**
	 * Will return a table with the {@link ItemStack} of each item in the internal cache.
	 * 
	 * @return A table of the internal contents.
	 * @throws Exception
	 */
	@PeripheralMethod
	public Map<Integer, ItemStack> contents() throws Exception {
		return InvUtils.contents(this, ForgeDirection.UNKNOWN);
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public boolean hasIngredients() {
		return craftMatrix.hasIngredients();
	}

	/**
	 * Will try and craft one unit of the specified recipe. The {@link PeripheralEvent#crafted} event will be fired if
	 * successful.
	 * 
	 * @throws Exception
	 */
	@PeripheralMethod
	public void craft() throws Exception {
		final EntityPlayer internalPlayer = Peripherals.proxy.getInternalFakePlayer((WorldServer) worldObj, xCoord,
				yCoord + 1, zCoord).get();
		if (craftSlot == null) {
			craftSlot = new SlotCrafting(internalPlayer, craftMatrix, craftResult, 0, 0, 0);
		}
		updateCraftingRecipe();
		ItemStack resultStack = craftResult.getStackInSlot(0);
		// AnzacPeripheralsCore.logger.info("craftResult: " + resultStack);
		if (resultStack == null) {
			throw new Exception("nothing to craft");
		}
		if (!hasIngredients()) {
			throw new Exception("does not have ingredients");
		}
		if (!hasSpace()) {
			throw new Exception("Not enough space in output");
		}
		final ItemStack notifyStack = resultStack.copy();
		craftMatrix.setCrafting(true);
		craftSlot.onPickupFromSlot(internalPlayer, resultStack);
		craftMatrix.setCrafting(false);
		InvUtils.addItem(output, resultStack, ForgeDirection.UNKNOWN);

		// clean fake player inventory (crafting handler support)
		final int sizeInventory = internalPlayer.inventory.getSizeInventory();
		for (int slot = 0; slot < sizeInventory; slot++) {
			final ItemStack stackInSlot = internalPlayer.inventory.getStackInSlot(slot);
			if (stackInSlot != null) {
				internalPlayer.inventory.setInventorySlotContents(slot, null);
			}
		}
		fireCraftedEvent(findUniqueIdentifierFor(notifyStack.getItem()).toString(), notifyStack.stackSize);
	}

	/**
	 * @return
	 */
	@PeripheralMethod
	public boolean hasSpace() {
		final ItemStack stackInSlot = output.getStackInSlot(0);
		final ItemStack resultStack = craftResult.getStackInSlot(0);
		return stackInSlot == null || InvUtils.canMergeItemStack(stackInSlot, resultStack);
	}

	/**
	 * @param name
	 * @param stackSize
	 */
	@Event(PeripheralEvent.crafted)
	private void fireCraftedEvent(final String name, final int stackSize) {
		fireEvent(PeripheralEvent.crafted, name, stackSize);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(final int side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(final int slot, final ItemStack stack, final int side) {
		return isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(final int slot, final ItemStack stack, final int side) {
		return slot == 0;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amt) {
		return inv.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		return inv.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		inv.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return output.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return output.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return output.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		return isConnected() && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(final int slot, final ItemStack stack) {
		return slot > 0;
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
			updateCraftingRecipe();
		}

		// read inventory slots
		if (tagCompound.hasKey(INVENTORY)) {
			final NBTTagList list = tagCompound.getTagList(INVENTORY, Constants.NBT.TAG_COMPOUND);
			for (byte entry = 0; entry < list.tagCount(); entry++) {
				final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
				final int slot = itemTag.getByte(SLOT);
				if (slot >= 0 && slot < getSizeInventory()) {
					final ItemStack stack = loadItemStackFromNBT(itemTag);
					setInventorySlotContents(slot, stack);
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

		// write craft slots
		list = new NBTTagList();
		for (byte slot = 0; slot < getSizeInventory(); slot++) {
			final ItemStack stack = getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte(SLOT, slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag(INVENTORY, list);
	}
}
