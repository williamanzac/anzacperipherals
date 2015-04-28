package anzac.peripherals.utility;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

	public static Map<Integer, ItemStack> contents(final IInventory inventory, final ForgeDirection from)
			throws Exception {
		final Map<Integer, ItemStack> table = new HashMap<Integer, ItemStack>();
		final int[] accessibleSlots = accessibleSlots(from, inventory);
		for (final int slot : accessibleSlots) {
			final ItemStack stackInSlot = inventory.getStackInSlot(slot);
			if (stackInSlot != null) {
				table.put(slot, stackInSlot);
			}
		}
		return table;
	}

	public static boolean itemMatched(final ItemStack target, final ItemStack source, final boolean matchMeta,
			final boolean matchNBT, final boolean useOreDict) {
		if (source == null) {
			return false;
		}
		boolean matched = false;
		if (target != null && Item.getIdFromItem(source.getItem()) == Item.getIdFromItem(target.getItem())) {
			matched = true;
			if (matchMeta && source.getItemDamage() != target.getItemDamage()) {
				matched = false;
			} else if (matchNBT && !isNBTMatch(source, target)) {
				matched = false;
			}
		}
		if (!matched && useOreDict && isOreDicMatch(target, source)) {
			matched = true;
		}
		return matched;
	}

	private static boolean isOreDicMatch(final ItemStack target, final ItemStack source) {
		if (source == null) {
			return false;
		}
		final int[] ids1 = OreDictionary.getOreIDs(target);
		if (ids1 == null || ids1.length == 0) {
			return false;
		}
		final int[] ids2 = OreDictionary.getOreIDs(source);
		if (ids2 == null || ids2.length == 0) {
			return false;
		}
		for (final int id1 : ids1) {
			for (final int id2 : ids2) {
				if (id1 == id2) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isNBTMatch(final ItemStack target, final ItemStack source) {
		if (target.stackTagCompound == null && source.stackTagCompound == null) {
			return true;
		}
		if (target.stackTagCompound == null || source.stackTagCompound == null) {
			return false;
		}
		if (!target.getTagCompound().hasKey("GEN")) {
			return target.stackTagCompound.equals(source.stackTagCompound);
		}
		final NBTTagCompound targetTag = (NBTTagCompound) target.getTagCompound().copy();
		final NBTTagCompound sourceTag = (NBTTagCompound) source.getTagCompound().copy();
		targetTag.removeTag("GEN");
		sourceTag.removeTag("GEN");
		return targetTag.equals(sourceTag);
	}

	public static boolean canMergeItemStack(final ItemStack sourceStack, final ItemStack targetStack) {
		if (sourceStack.isStackable()) {
			if (itemMatched(targetStack, sourceStack, true, true, false)) {
				final int l = targetStack.stackSize + sourceStack.stackSize;
				final int maxStackSize = sourceStack.getMaxStackSize();
				if (l <= maxStackSize) {
					// sourceStack.stackSize = 0;
					return true;
				} else if (targetStack.stackSize < maxStackSize) {
					// sourceStack.stackSize -= maxStackSize - targetStack.stackSize;
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
			if (itemMatched(targetStack, sourceStack, true, true, false)) {
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

	public static int addItem(final Object into, final ItemStack item, final ForgeDirection side) {
		if (into == null || item == null) {
			return 0;
		}
		if (into instanceof ISidedInventory) {
			return InvUtils.addItem((ISidedInventory) into, item, side);
		} else if (into instanceof IInventory) {
			return InvUtils.addItem((IInventory) into, item);
		}
		return 0;
	}

	// public static int addItem(IItemDuct con, ItemStack item, ForgeDirection inventorySide) {
	// int startedWith = item.stackSize;
	// ItemStack remaining = con.insertItem(inventorySide, item);
	// if (remaining == null) {
	// return startedWith;
	// }
	// return startedWith - remaining.stackSize;
	// }

	public static int addItem(final ISidedInventory sidedInv, final ItemStack item, ForgeDirection inventorySide) {
		if (inventorySide == null) {
			inventorySide = ForgeDirection.UNKNOWN;
		}
		final int[] slots = sidedInv.getAccessibleSlotsFromSide(inventorySide.ordinal());
		if (slots == null) {
			return 0;
		}
		int numInserted = 0;
		int numToInsert = item.stackSize;
		for (int i = 0; i < slots.length && numToInsert > 0; i++) {
			final int slot = slots[i];
			if (sidedInv.canInsertItem(slot, item, inventorySide.ordinal())) {
				final ItemStack contents = sidedInv.getStackInSlot(slot);
				final ItemStack toInsert = item.copy();
				toInsert.stackSize = Math.min(toInsert.stackSize, sidedInv.getInventoryStackLimit());
				toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
				int inserted = 0;
				if (contents == null) {
					inserted = toInsert.stackSize;
				} else {
					if (contents.isItemEqual(item) && ItemStack.areItemStackTagsEqual(contents, item)) {
						int space = sidedInv.getInventoryStackLimit() - contents.stackSize;
						space = Math.min(space, contents.getMaxStackSize() - contents.stackSize);
						inserted += Math.min(space, toInsert.stackSize);
						toInsert.stackSize = contents.stackSize + inserted;
					} else {
						toInsert.stackSize = 0;
					}
				}
				if (inserted > 0) {
					numInserted += inserted;
					numToInsert -= inserted;
					sidedInv.setInventorySlotContents(slot, toInsert);
				}
			}
		}
		if (numInserted > 0) {
			sidedInv.markDirty();
		}
		return numInserted;
	}

	public static int addItem(final IInventory inv, final ItemStack item) {
		int numInserted = 0;
		int numToInsert = item.stackSize;
		for (int slot = 0; slot < inv.getSizeInventory() && numToInsert > 0; slot++) {
			final ItemStack contents = inv.getStackInSlot(slot);
			if (!isStackFull(contents)) {
				final ItemStack toInsert = item.copy();
				toInsert.stackSize = Math.min(toInsert.stackSize, inv.getInventoryStackLimit());
				toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
				int inserted = 0;
				if (contents == null) {
					inserted = toInsert.stackSize;
				} else {
					if (contents.isItemEqual(item) && ItemStack.areItemStackTagsEqual(contents, item)) {
						int space = inv.getInventoryStackLimit() - contents.stackSize;
						space = Math.min(space, contents.getMaxStackSize() - contents.stackSize);
						inserted += Math.min(space, toInsert.stackSize);
						toInsert.stackSize = contents.stackSize + inserted;
					} else {
						toInsert.stackSize = 0;
					}
				}
				if (!inv.isItemValidForSlot(slot, toInsert)) {
					inserted = 0;
				}
				if (inserted > 0) {
					numInserted += inserted;
					numToInsert -= inserted;
					inv.setInventorySlotContents(slot, toInsert);
				}
			}
		}
		if (numInserted > 0) {
			inv.markDirty();
		}
		return numInserted;
	}

	public static boolean isStackFull(final ItemStack contents) {
		if (contents == null) {
			return false;
		}
		return contents.stackSize >= contents.getMaxStackSize();
	}

	public static boolean areStackMergable(final ItemStack s1, final ItemStack s2) {
		if (s1 == null || s2 == null || !s1.isStackable() || !s2.isStackable()) {
			return false;
		}
		if (!s1.isItemEqual(s2)) {
			return false;
		}
		return ItemStack.areItemStackTagsEqual(s1, s2);
	}

	public static boolean areStacksEqual(final ItemStack s1, final ItemStack s2) {
		if (s1 == null || s2 == null) {
			return false;
		}
		if (!s1.isItemEqual(s2)) {
			return false;
		}
		return ItemStack.areItemStackTagsEqual(s1, s2);
	}

	public static int routeTo(final World world, final int x, final int y, final int z, final ForgeDirection side,
			final ForgeDirection insertSide, final ItemStack stack) throws Exception {
		final Position pos = new Position(x, y, z, side);
		pos.moveForwards(1);

		final TileEntity tile = world.getTileEntity(pos.x, pos.y, pos.z);
		if (tile instanceof IInventory) {
			final IInventory inv = (IInventory) tile;
			return addItem(inv, stack, insertSide);
		} else if (tile instanceof IPipeTile) {
			final IPipeTile pipe = (IPipeTile) tile;
			if (pipe.getPipeType() != PipeType.ITEM) {
				throw new Exception("The pipe dos not accept Items.");
			}
			if (!pipe.isPipeConnected(insertSide)) {
				throw new Exception("The pipe is not connected.");
			}
			final ItemStack copy = stack.copy();
			final int injected = pipe.injectItem(copy, true, insertSide, null);
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

	public static ItemStack consumeItem(final ItemStack stack) {
		if (stack.stackSize == 1) {
			if (stack.getItem().hasContainerItem(stack)) {
				return stack.getItem().getContainerItem(stack);
			} else {
				return null;
			}
		} else {
			stack.splitStack(1);

			return stack;
		}
	}
}
