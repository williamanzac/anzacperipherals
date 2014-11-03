package anzac.peripherals.tile;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.utility.InvUtils;

@Peripheral(type = "itemstorage")
public class ItemStorageTileEntity extends BaseTileEntity implements ISidedInventory {
	private final SimpleInventory inv = new SimpleInventory(7 * 9, "Item Storage", 64);
	private final int[] SLOTS = InvUtils.createSlotArray(inv);

	public ItemStorageTileEntity() {
		inv.addListner(this);
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@PeripheralMethod
	public Map<Integer, ItemStack> contents() throws Exception {
		return InvUtils.contents(this);
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
		return true;
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
		// TODO fire event add stack
	}

	@Override
	public String getInventoryName() {
		return inv.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inv.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer player) {
		return isConnected() && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
		inv.openInventory();
	}

	@Override
	public void closeInventory() {
		inv.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return inv.isItemValidForSlot(i, itemstack);
	}

	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		inv.readFromNBT(tagCompound);
	}

	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		inv.writeToNBT(tagCompound);
	}
}
