package anzac.peripherals.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot {

	public SlotOutput(final IInventory inventory, final int slot, final int x, final int y) {
		super(inventory, slot, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack itemStack) {
		return false;
	}
}
