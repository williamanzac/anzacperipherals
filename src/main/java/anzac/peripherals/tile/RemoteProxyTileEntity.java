package anzac.peripherals.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import anzac.peripherals.peripherals.Target;
import anzac.peripherals.utility.Position;

public class RemoteProxyTileEntity extends TileEntity implements IInventory {

	private SimpleTargetInventory inv = new SimpleTargetInventory(1, "Remote Proxy");

	public Target getTarget() {
		final List<Target> targets = new ArrayList<Target>();
		for (int i = 0; i < getSizeInventory(); i++) {
			final ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.hasTagCompound()) {
				final NBTTagCompound tagCompound = stack.getTagCompound();
				final Target target = new Target();
				final int x = tagCompound.getInteger("linkx");
				final int y = tagCompound.getInteger("linky");
				final int z = tagCompound.getInteger("linkz");
				final int d = tagCompound.getInteger("linkd");
				target.position = new Position(x, y, z);
				target.dimension = d;
				targets.add(target);
			}
		}
		if (!targets.isEmpty()) {
			return targets.get(0);
		}
		return null;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inv.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv.setInventorySlotContents(slot, stack);
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
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inv.isUseableByPlayer(player);
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inv.isItemValidForSlot(slot, stack);
	}

	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		inv.readFromNBT(tagCompound);
	}

	@Override
	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		inv.writeToNBT(tagCompound);
	}
}
