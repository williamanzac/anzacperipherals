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

	public static ItemStack[] contents(final IInventory inventory) throws Exception {
		LogHelper.info("contents for " + inventory);
		final Map<Integer, ItemStack> table = new HashMap<Integer, ItemStack>();
		final int sizeInventory = inventory.getSizeInventory();
		LogHelper.info("sizeInventory " + sizeInventory);
		for (int slot = 0; slot < sizeInventory; slot++) {
			final ItemStack stackInSlot = inventory.getStackInSlot(slot);
			if (stackInSlot != null) {
				final int uuid = UUIDUtils.getUUID(stackInSlot);
				final int amount = stackInSlot.stackSize;
				if (table.containsKey(uuid)) {
					final ItemStack ItemStack = table.get(uuid);
					ItemStack.stackSize += amount;
				} else {
					final ItemStack ItemStack = UUIDUtils.getItemStack(uuid, amount);
					table.put(uuid, ItemStack);
				}
			}
		}
		// AnzacPeripheralsCore.logger.info("table:" + table);
		final ItemStack[] array = table.values().toArray(new ItemStack[table.size()]);
		LogHelper.info("table " + array);
		return array;
	}

	public static boolean stacksMatch(final ItemStack targetStack, final ItemStack sourceStack) {
		return OreDictionary.itemMatches(targetStack, sourceStack, false);
	}

	public static boolean canMergeItemStack(final ItemStack sourceStack, final ItemStack targetStack) {
		boolean merged = false;

		if (sourceStack.isStackable()) {
			if (stacksMatch(targetStack, sourceStack)) {
				final int l = targetStack.stackSize + sourceStack.stackSize;

				if (l <= sourceStack.getMaxStackSize()) {
					sourceStack.stackSize = 0;
					merged = true;
				} else if (targetStack.stackSize < sourceStack.getMaxStackSize()) {
					sourceStack.stackSize -= sourceStack.getMaxStackSize() - targetStack.stackSize;
					merged = true;
				}
			}
		}

		return merged;
	}

	/**
	 * merges provided ItemStack with the first avaliable one in the container/player inventory
	 */
	public static boolean mergeItemStack(final ItemStack sourceStack, final ItemStack targetStack) {
		boolean merged = false;

		if (sourceStack.isStackable()) {
			if (stacksMatch(targetStack, sourceStack)) {
				final int l = targetStack.stackSize + sourceStack.stackSize;

				if (l <= sourceStack.getMaxStackSize()) {
					sourceStack.stackSize = 0;
					targetStack.stackSize = l;
					merged = true;
				} else if (targetStack.stackSize < sourceStack.getMaxStackSize()) {
					sourceStack.stackSize -= sourceStack.getMaxStackSize() - targetStack.stackSize;
					targetStack.stackSize = sourceStack.getMaxStackSize();
					merged = true;
				}
			}
		}

		return merged;
	}
}
