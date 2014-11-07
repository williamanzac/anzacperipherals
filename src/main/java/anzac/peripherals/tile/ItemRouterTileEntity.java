package anzac.peripherals.tile;

import static cpw.mods.fml.common.registry.GameRegistry.findUniqueIdentifierFor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.annotations.Event;
import anzac.peripherals.annotations.Peripheral;
import anzac.peripherals.annotations.PeripheralMethod;
import anzac.peripherals.peripherals.PeripheralEvent;
import anzac.peripherals.peripherals.Trigger;
import anzac.peripherals.utility.InvUtils;
import anzac.peripherals.utility.LogHelper;
import anzac.peripherals.utility.Position;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.transport.IPipeTile;

@Peripheral(type = "itemrouter")
public class ItemRouterTileEntity extends BaseTileEntity implements ISidedInventory {
	private final SimpleInventory inv = new SimpleInventory(1, "Item Router", 64);
	private final int[] SLOTS = InvUtils.createSlotArray(inv);

	protected List<Trigger> triggers = new ArrayList<Trigger>();

	public ItemRouterTileEntity() {
		inv.addListner(this);
	}

	/**
	 * @param side
	 * @return table
	 */
	@PeripheralMethod
	public List<ITrigger> getAvailableTriggers(final ForgeDirection side) {
		final Position p = new Position(xCoord, yCoord, zCoord, side);
		p.moveForwards(1);
		final Block block = worldObj.getBlock(p.x, p.y, p.z);
		final TileEntity entity = worldObj.getTileEntity(p.x, p.y, p.z);
		if (block == null || entity == null) {
			return null;
		}
		final List<ITrigger> triggers = ActionManager.getNeighborTriggers(block, entity);
		if (entity instanceof IPipeTile) {
			triggers.addAll(ActionManager.getPipeTriggers((IPipeTile) entity));
		}
		LogHelper.info("returning " + triggers);
		return triggers;
	}

	/**
	 * @param name
	 * @param itemstack
	 * @param side
	 * @throws Exception
	 */
	@PeripheralMethod
	public void addTrigger(final String name, final ItemStack itemstack, final ForgeDirection side) throws Exception {
		// if (getMount() == null) {
		// throw new Exception("No disk loaded");
		// }
		final ITrigger iTrigger = ActionManager.triggers.get(name);
		if (iTrigger == null) {
			throw new Exception(name + " is not a valid trigger");
		}
		if (iTrigger.requiresParameter() && itemstack == null) {
			throw new Exception(name + " requires a parameter");
		}
		final Trigger trigger = new Trigger(iTrigger, itemstack, side);
		triggers.add(trigger);
	}

	/**
	 * @param name
	 * @param itemstack
	 * @param side
	 * @throws Exception
	 */
	@PeripheralMethod
	public void removeTrigger(final String name, final ItemStack itemstack, final ForgeDirection side) throws Exception {
		// if (getMount() == null) {
		// throw new Exception("No disk loaded");
		// }
		for (final Iterator<Trigger> it = triggers.iterator(); it.hasNext();) {
			final Trigger trigger = it.next();
			if (trigger.getUniqueTag().equals(name) && trigger.getParameter() == itemstack && trigger.getSide() == side) {
				it.remove();
			}
		}
	}

	/**
	 * Will return a table containing the uuid and count of each item in the internal cache.
	 * 
	 * @return A table of the internal contents.
	 * @throws Exception
	 */
	@PeripheralMethod
	public Map<Integer, ItemStack> contents() throws Exception {
		return contents(ForgeDirection.UNKNOWN);
	}

	/**
	 * Will return a table containing the uuid and count of each item in the inventory connected to {@code direction}
	 * side of this block.
	 * 
	 * @param direction
	 *            which side of this block to examine the inventory of.
	 * @return A table of the contents of the connected inventory.
	 * @throws Exception
	 */
	@PeripheralMethod
	public Map<Integer, ItemStack> contents(final ForgeDirection direction) throws Exception {
		return contents(direction, direction.getOpposite());
	}

	/**
	 * Will return a table containing the uuid and count of each item in the inventory connected to {@code direction}
	 * side of this block and limited the examined slot to those accessible from {@code side} side.
	 * 
	 * @param direction
	 *            which side of this block to examine the inventory of.
	 * @param dir
	 *            which side of the inventory to examine.
	 * @return A table of the contents of the connected inventory.
	 * @throws Exception
	 */
	@PeripheralMethod
	public Map<Integer, ItemStack> contents(final ForgeDirection direction, final ForgeDirection dir) throws Exception {
		final TileEntity te;
		if (direction == ForgeDirection.UNKNOWN) {
			te = this;
		} else {
			final Position pos = new Position(xCoord, yCoord, zCoord, direction);
			pos.moveForwards(1);
			te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
			if (te == null || !(te instanceof IInventory)) {
				throw new Exception("Inventory not found");
			}
		}
		final IInventory handler = (IInventory) te;
		return InvUtils.contents(handler);
	}

	/**
	 * Extract {@code amount} number of items with {@code uuid} from the inventory connected to {@code fromDir} side.
	 * 
	 * @param fromDir
	 *            which side of this block to extract from.
	 * @param itemstack
	 *            the uuid of the items to extract.
	 * @param amount
	 *            the number of items to extract.
	 * @return The actual number of items extracted.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int extractFrom(final ForgeDirection fromDir, final ItemStack itemstack, final int amount) throws Exception {
		return extractFrom(fromDir, itemstack, amount, fromDir.getOpposite());
	}

	/**
	 * Extract {@code amount} number of items with {@code uuid} from the {@code side} side of the inventory connected to
	 * {@code fromDir} side.
	 * 
	 * @param fromDir
	 *            which side of this block to extract from.
	 * @param itemstack
	 *            the uuid of the items to extract.
	 * @param amount
	 *            the number of items to extract.
	 * @param extractSide
	 *            which side of the inventory to extract from.
	 * @return The actual number of items extracted.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int extractFrom(final ForgeDirection fromDir, final ItemStack itemstack, final int amount,
			final ForgeDirection extractSide) throws Exception {
		if (getStackInSlot(0) != null) {
			throw new Exception("Internal cache is not empty");
		}
		final Position pos = new Position(xCoord, yCoord, zCoord, fromDir);
		pos.moveForwards(1);
		final TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
		if (te == null || !(te instanceof IInventory)) {
			throw new Exception("Inventory not found");
		}
		final IInventory inv = (IInventory) te;
		final int[] slots = InvUtils.accessibleSlots(ForgeDirection.UNKNOWN, inv);
		for (final int i : slots) {
			final ItemStack stackInSlot = inv.getStackInSlot(i);
			if (InvUtils.stacksMatch(stackInSlot, itemstack)) {
				return InvUtils.addItem(this, stackInSlot, true, ForgeDirection.UNKNOWN);
			}
		}
		return 0;
	}

	/**
	 * Transfer {@code amount} number of items from the internal cache to the inventory connected on {@code toDir} side.
	 * 
	 * @param toDir
	 *            the side the inventory is connected to.
	 * @param amount
	 *            the number of items to transfer.
	 * @return the actual number of items transferred.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int routeTo(final ForgeDirection toDir, final int amount) throws Exception {
		return routeTo(toDir, toDir.getOpposite(), amount);
	}

	/**
	 * Transfer {@code amount} number of items from the internal cache to the {@code side} side of the inventory
	 * connected on {@code toDir} side.
	 * 
	 * @param toDir
	 *            the side the inventory is connected to.
	 * @param insertDir
	 *            the side the inventory to insert the items from.
	 * @param amount
	 *            the number of items to transfer.
	 * @return the actual number of items transferred.
	 * @throws Exception
	 */
	@PeripheralMethod
	public int routeTo(final ForgeDirection toDir, final ForgeDirection insertDir, final int amount) throws Exception {
		final int[] slots = InvUtils.accessibleSlots(ForgeDirection.UNKNOWN, inv);
		for (final int i : slots) {
			final ItemStack stackInSlot = getStackInSlot(i);
			if (stackInSlot != null) {
				final ItemStack copy = stackInSlot.copy();
				copy.stackSize = amount;
				final int amount1 = copy.stackSize;
				copy.stackSize -= InvUtils.routeTo(worldObj, xCoord, yCoord, zCoord, toDir, insertDir, copy);
				final int toDec = amount1 - copy.stackSize;
				if (toDec > 0) {
					decrStackSize(i, toDec);
				}
				return amount - copy.stackSize;
			}
		}
		return 0;
	}

	//
	// /**
	// * Transfer {@code amount} number of items from the internal cache to another connected peripheral with
	// * {@code label} label. The peripheral must be connected to the same computer.
	// *
	// * @param label
	// * the label of the peripheral.
	// * @param amount
	// * the number of items to transfer.
	// * @return the actual number of items transferred.
	// * @throws Exception
	// */
	// @PeripheralMethod
	// public int sendTo(final String label, final int amount) throws Exception {
	// return getEntity().sendTo(label, amount);
	// }
	//
	// /**
	// * Transfer {@code amount} amount of fluid from another connected peripheral with {@code label} label to the
	// * internal tank. The peripheral must be connected to the same computer.
	// *
	// * @param label
	// * the label of the peripheral.
	// * @param uuid
	// * the uuid of the fluid to transfer.
	// * @param amount
	// * the amount of fluid to transfer.
	// * @return the actual amount transferred.
	// * @throws Exception
	// */
	// @PeripheralMethod
	// public int requestFrom(final String label, final int uuid, final int amount) throws Exception {
	// return getEntity().requestFrom(label, uuid, amount);
	// }

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
		if (stack != null) {
			fireRouteEvent(findUniqueIdentifierFor(stack.getItem()).toString(), stack.stackSize);
		}
	}

	/**
	 * @param name
	 * @param stackSize
	 */
	@Event(PeripheralEvent.item_route)
	private void fireRouteEvent(final String name, final int stackSize) {
		fireEvent(PeripheralEvent.item_route, name, stackSize);
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