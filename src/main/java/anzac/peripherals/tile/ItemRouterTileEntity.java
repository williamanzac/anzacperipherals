package anzac.peripherals.tile;

import static cpw.mods.fml.common.registry.GameRegistry.findUniqueIdentifierFor;
import static net.minecraft.item.ItemStack.loadItemStackFromNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
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
import buildcraft.api.gates.ITileTrigger;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.IPipeTrigger;
import buildcraft.transport.TileGenericPipe;

@Peripheral(type = "itemrouter")
public class ItemRouterTileEntity extends BaseTileEntity implements ISidedInventory {
	private static final String SIDE = "side";
	private static final String PARAMETER = "parameter";
	private static final String TAG = "tag";
	private static final String TRIGGERS = "triggers";

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
	 * @param trigger
	 * @param itemstack
	 * @param side
	 * @throws Exception
	 */
	@PeripheralMethod
	public void addTrigger(final ITrigger trigger, final ItemStack itemstack, final ForgeDirection side)
			throws Exception {
		if (trigger == null) {
			throw new Exception("A valid trigger is required");
		}
		if (trigger.requiresParameter() && itemstack == null) {
			throw new Exception(trigger.getUniqueTag() + " requires a parameter");
		}
		final Trigger ret = new Trigger(trigger, itemstack, side);
		triggers.add(ret);
	}

	/**
	 * @param trigger
	 * @param itemstack
	 * @param side
	 * @throws Exception
	 */
	@PeripheralMethod
	public void removeTrigger(final ITrigger trigger, final ItemStack itemstack, final ForgeDirection side)
			throws Exception {
		final Trigger ret = new Trigger(trigger, itemstack, side);
		triggers.remove(ret);
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
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) {
			return;
		}

		if (worldObj.getTotalWorldTime() % 10 == 0) {
			resolveEvents();
		}
	}

	protected void resolveEvents() {
		// Computes the events depending on the triggers
		for (final Trigger trigger : triggers) {
			if (trigger != null) {
				if (isTriggerActive(trigger)) {
					fireTriggerEvent(trigger);
				}
			}
		}
	}

	protected boolean isTriggerActive(final Trigger trigger) {
		final ITrigger trigger2 = trigger.getTrigger();
		if (trigger != null && trigger2 != null) {
			final Position p = new Position(xCoord, yCoord, zCoord, trigger.getSide());
			p.moveForwards(1);
			LogHelper.info("position: " + p);
			final TileEntity tile = worldObj.getTileEntity(p.x, p.y, p.z);

			if (tile != null) {
				final ITriggerParameter triggerParameter = trigger.getTriggerParameter();
				final ForgeDirection opposite = trigger.getSide().getOpposite();
				if (trigger2 instanceof IPipeTrigger) {
					if (((IPipeTrigger) trigger2).isTriggerActive(((TileGenericPipe) tile).pipe, triggerParameter)) {
						return true;
					}
				}
				if (trigger2 instanceof ITileTrigger) {
					if (((ITileTrigger) trigger2).isTriggerActive(opposite, tile, triggerParameter)) {
						return true;
					}
				}
			}
		}

		return false;
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

		if (tagCompound.hasKey(TRIGGERS)) {
			triggers.clear();
			final NBTTagList tagList = tagCompound.getTagList(TRIGGERS, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); ++i) {
				final NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
				if (tag.hasKey(TAG)) {
					final ITrigger trigger = ActionManager.triggers.get(tag.getString(TAG));
					final ItemStack parameter = loadItemStackFromNBT(tag.getCompoundTag(PARAMETER));
					final int side = tag.getInteger(SIDE);
					triggers.add(new Trigger(trigger, parameter, ForgeDirection.getOrientation(side)));
				}
			}
		}

		inv.readFromNBT(tagCompound);
	}

	public void writeToNBT(final NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

		final NBTTagList tagList = new NBTTagList();
		for (final Trigger trigger : triggers) {
			final NBTTagCompound tag = new NBTTagCompound();
			if (trigger != null) {
				tag.setString(TAG, trigger.getUniqueTag());
				final NBTTagCompound parameter = new NBTTagCompound();
				trigger.getParameter().writeToNBT(parameter);
				tag.setTag(PARAMETER, parameter);
				tag.setInteger(SIDE, trigger.getSide().ordinal());
			}
			tagList.appendTag(tag);
		}
		tagCompound.setTag(TRIGGERS, tagList);

		inv.writeToNBT(tagCompound);
	}
}
