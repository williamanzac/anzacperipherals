package anzac.peripherals.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ValidatingSlot extends Slot {

	public ValidatingSlot(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack stack) {
		return inventory.isItemValidForSlot(slotNumber, stack);
	}
}
