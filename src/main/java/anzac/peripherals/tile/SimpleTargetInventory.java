package anzac.peripherals.tile;

import net.minecraft.item.ItemStack;
import anzac.peripherals.item.MemoryCard;

public class SimpleTargetInventory extends SimpleInventory {

	public SimpleTargetInventory(final int size, final String name) {
		super(size, name, 1);
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return itemstack.getItem() instanceof MemoryCard && itemstack.hasTagCompound();
	}
}
