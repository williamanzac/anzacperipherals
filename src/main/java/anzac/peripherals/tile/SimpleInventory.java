package anzac.peripherals.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class SimpleInventory implements IInventory {

	private final ItemStack[] inv;
	private final Set<TileEntity> listeners = new HashSet<TileEntity>();
	private final String invName;
	private final int stackLimit;

	public SimpleInventory(final int size, final String name, final int limit) {
		inv = new ItemStack[size];
		invName = name;
		stackLimit = limit;
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(final int slot) {
		return inv[slot];
	}

	@Override
	public ItemStack decrStackSize(final int slot, final int amt) {
		if (slot < inv.length && inv[slot] != null) {
			if (inv[slot].stackSize > amt) {
				ItemStack result = inv[slot].splitStack(amt);
				markDirty();
				return result;
			}
			ItemStack stack = inv[slot];
			setInventorySlotContents(slot, null);
			return stack;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(final int slot) {
		if (inv[slot] == null) {
			return null;
		}

		ItemStack stackToTake = inv[slot];
		setInventorySlotContents(slot, null);
		return stackToTake;
	}

	@Override
	public void setInventorySlotContents(final int slot, final ItemStack stack) {
		if (slot >= inv.length) {
			return;
		}
		inv[slot] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return stackLimit;
	}

	@Override
	public boolean isUseableByPlayer(final EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return true;
	}

	public void readFromNBT(final NBTTagCompound tagCompound) {
		// read slots
		final NBTTagList list = tagCompound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
		for (byte entry = 0; entry < list.tagCount(); entry++) {
			final NBTTagCompound itemTag = list.getCompoundTagAt(entry);
			final int slot = itemTag.getByte("slot");
			if (slot >= 0 && slot < getSizeInventory()) {
				final ItemStack stack = ItemStack.loadItemStackFromNBT(itemTag);
				setInventorySlotContents(slot, stack);
			}
		}
	}

	public void writeToNBT(final NBTTagCompound tagCompound) {
		// write slots
		final NBTTagList list = new NBTTagList();
		for (byte slot = 0; slot < getSizeInventory(); slot++) {
			final ItemStack stack = getStackInSlot(slot);
			if (stack != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("slot", slot);
				stack.writeToNBT(itemTag);
				list.appendTag(itemTag);
			}
		}
		tagCompound.setTag("inventory", list);
	}

	public void addListner(final TileEntity listener) {
		listeners.add(listener);
	}

	public void removeListner(final TileEntity listener) {
		listeners.remove(listener);
	}

	@Override
	public String getInventoryName() {
		return invName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void markDirty() {
		for (TileEntity tileEntity : listeners) {
			tileEntity.markDirty();
		}
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
}
