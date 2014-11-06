package anzac.peripherals.utility;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;

public class InvUtils {

	public static int[] createSlotArray(final int first, final int count) {
		final int[] slots = new int[count];
		for (int k = first; k < first + count; k++) {
			slots[k - first] = k;
		}
		return slots;
	}

	public static int[] createSlotArray(final IInventory inventory) {
		final int count = inventory.getSizeInventory();
		return createSlotArray(0, count);
	}

	public static int[] createSlotArray(final ItemStack[] inventory) {
		final int count = inventory.length;
		return createSlotArray(0, count);
	}

	public static Map<Integer, ItemStack> contents(final IInventory inventory) throws Exception {
		LogHelper.info("contents for " + inventory);
		final Map<Integer, ItemStack> table = new HashMap<Integer, ItemStack>();
		final int sizeInventory = inventory.getSizeInventory();
		LogHelper.info("sizeInventory " + sizeInventory);
		for (int slot = 0; slot < sizeInventory; slot++) {
			final ItemStack stackInSlot = inventory.getStackInSlot(slot);
			if (stackInSlot != null) {
				table.put(slot, stackInSlot);
			}
		}
		// AnzacPeripheralsCore.logger.info("table:" + table);
		LogHelper.info("table " + table);
		return table;
	}

	public static boolean stacksMatch(final ItemStack targetStack, final ItemStack sourceStack) {
		return OreDictionary.itemMatches(targetStack, sourceStack, false);
	}

	public static boolean canMergeItemStack(final ItemStack sourceStack, final ItemStack targetStack) {
		if (sourceStack.isStackable()) {
			if (stacksMatch(targetStack, sourceStack)) {
				final int l = targetStack.stackSize + sourceStack.stackSize;
				final int maxStackSize = sourceStack.getMaxStackSize();
				if (l <= maxStackSize) {
					sourceStack.stackSize = 0;
					return true;
				} else if (targetStack.stackSize < maxStackSize) {
					sourceStack.stackSize -= maxStackSize - targetStack.stackSize;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * merges provided ItemStack with the first avaliable one in the container/player inventory
	 */
	public static boolean mergeItemStack(final ItemStack sourceStack, final ItemStack targetStack) {
		if (sourceStack.isStackable()) {
			if (stacksMatch(targetStack, sourceStack)) {
				final int l = targetStack.stackSize + sourceStack.stackSize;
				final int maxStackSize = sourceStack.getMaxStackSize();
				if (l <= maxStackSize) {
					sourceStack.stackSize = 0;
					targetStack.stackSize = l;
					return true;
				} else if (targetStack.stackSize < maxStackSize) {
					sourceStack.stackSize -= maxStackSize - targetStack.stackSize;
					targetStack.stackSize = maxStackSize;
					return true;
				}
			}
		}

		return false;
	}

	//
	// public static void transferToSlot(final IInventory inv, final int slot, final ItemStack stack) {
	// final ItemStack stackInSlot = inv.getStackInSlot(slot);
	// if (stackInSlot == null) {
	// final ItemStack targetStack = stack.copy();
	// inv.setInventorySlotContents(slot, targetStack);
	// stack.stackSize -= targetStack.stackSize;
	// } else {
	// final boolean merged = mergeItemStack(stack, stackInSlot);
	// if (merged) {
	// inv.setInventorySlotContents(slot, stackInSlot);
	// }
	// }
	// }

	public static int[] accessibleSlots(final ForgeDirection extractSide, final IInventory inv) {
		final int[] slots;
		if (inv instanceof ISidedInventory) {
			slots = ((ISidedInventory) inv).getAccessibleSlotsFromSide(extractSide.ordinal());
		} else {
			slots = createSlotArray(inv);
		}
		return slots;
	}

	public static int addItem(final IInventory inv, final ItemStack stack, final boolean doAdd,
			final ForgeDirection from) {
		final ItemStack copy;
		final int size = stack.stackSize;
		if (doAdd) {
			copy = stack;
		} else {
			copy = stack.copy();
		}
		final int[] availableSlots = accessibleSlots(from, inv);
		for (final int slot : availableSlots) {
			final ItemStack stackInSlot = inv.getStackInSlot(slot);
			if (stackInSlot != null && stacksMatch(stackInSlot, copy)) {
				final ItemStack target = copy.copy();
				final int l = stackInSlot.stackSize + copy.stackSize;
				target.stackSize = l;
				if (doAdd) {
					inv.setInventorySlotContents(slot, target);
				} else {
					final int inventoryStackLimit = inv.getInventoryStackLimit();
					if (target.stackSize > inventoryStackLimit) {
						target.stackSize = inventoryStackLimit;
					}
				}
				copy.stackSize -= (target.stackSize - stackInSlot.stackSize);
			}
			if (copy.stackSize == 0) {
				break;
			}
		}
		if (copy.stackSize > 0) {
			for (final int slot : availableSlots) {
				if (!inv.isItemValidForSlot(slot, stack)) {
					continue;
				}
				final ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (stackInSlot == null) {
					final ItemStack target = copy.copy();
					if (doAdd) {
						inv.setInventorySlotContents(slot, target);
					} else {
						final int inventoryStackLimit = inv.getInventoryStackLimit();
						if (target.stackSize > inventoryStackLimit) {
							target.stackSize = inventoryStackLimit;
						}
					}
					copy.stackSize -= target.stackSize;
				}
				if (copy.stackSize == 0) {
					break;
				}
			}
		}
		return size - copy.stackSize;
	}

	public static int routeTo(final World world, final int x, final int y, final int z, final ForgeDirection side,
			final ForgeDirection insertSide, final ItemStack stack) throws Exception {
		final Position pos = new Position(x, y, z, side);
		pos.moveForwards(1);

		final TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);
		if (tile instanceof IInventory) {
			final IInventory inv = (IInventory) tile;
			return addItem(inv, stack, true, insertSide);
		} else if (tile instanceof IPipeTile) {
			final IPipeTile pipe = (IPipeTile) tile;
			if (pipe.getPipeType() != PipeType.ITEM) {
				throw new Exception("The pipe dos not accept Items.");
			}
			if (!pipe.isPipeConnected(insertSide)) {
				throw new Exception("The pipe is not connected.");
			}
			final ItemStack copy = stack.copy();
			final int injected = pipe.injectItem(copy, true, insertSide);
			return injected;
			// } else if (tile instanceof IItemConduit) {
			// final ItemStack insertItem = ((IItemConduit) tile).insertItem(insertSide, stack);
			// if (insertItem == null) {
			// return stack.stackSize;
			// }
			// return stack.stackSize - insertItem.stackSize;
		}
		throw new Exception("No inventory or pipe found.");
	}
}
