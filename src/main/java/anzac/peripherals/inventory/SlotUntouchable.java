package anzac.peripherals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotUntouchable extends SlotPhantom {

	public SlotUntouchable(final IInventory inventory, final int slot, final int x, final int y) {
		super(inventory, slot, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean canTakeStack(final EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public boolean isAdjustable() {
		return false;
	}
}
