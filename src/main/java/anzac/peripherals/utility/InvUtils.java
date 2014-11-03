package anzac.peripherals.utility;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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

	public static void transferToSlot(final IInventory inv, final int slot, final ItemStack stack) {
		final ItemStack stackInSlot = inv.getStackInSlot(slot);
		if (stackInSlot == null) {
			final ItemStack targetStack = stack.copy();
			inv.setInventorySlotContents(slot, targetStack);
			stack.stackSize -= targetStack.stackSize;
		} else {
			final boolean merged = mergeItemStack(stack, stackInSlot);
			if (merged) {
				inv.setInventorySlotContents(slot, stackInSlot);
			}
		}
	}
}
