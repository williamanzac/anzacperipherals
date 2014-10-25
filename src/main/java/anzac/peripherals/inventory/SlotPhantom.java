package anzac.peripherals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotPhantom extends Slot implements ISpecialSlot {

	public SlotPhantom(final IInventory inventory, final int slot, final int x, final int y) {
		super(inventory, slot, x, y);
	}

	@Override
	public boolean canTakeStack(final EntityPlayer entityPlayer) {
		return false;
	}

	@Override
	public boolean isAdjustable() {
		return true;
	}
}
