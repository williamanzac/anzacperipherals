package anzac.peripherals.tile;

import static net.minecraftforge.fluids.FluidContainerRegistry.isEmptyContainer;
import net.minecraft.item.ItemStack;

public class SimpleFluidInventory extends SimpleInventory {

	public SimpleFluidInventory(final int size, final String name) {
		super(size, name, 1);
	}

	@Override
	public boolean isItemValidForSlot(final int i, final ItemStack itemstack) {
		return isEmptyContainer(itemstack);
	}
}
