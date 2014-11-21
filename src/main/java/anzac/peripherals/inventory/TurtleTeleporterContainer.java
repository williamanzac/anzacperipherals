package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import anzac.peripherals.tile.TurtleTeleporterTileEntity;

public class TurtleTeleporterContainer extends BaseContainer<TurtleTeleporterTileEntity> {

	public TurtleTeleporterContainer(final InventoryPlayer inventoryPlayer, final TurtleTeleporterTileEntity te) {
		super(te);

		int row;
		int col;

		// inventory
		for (col = 0; col < 4; ++col) {
			if (col < te.getSizeInventory()) {
				addSlotToContainer(new Slot(te, col, 53 + col * 18, 35) {
					@Override
					public boolean isItemValid(final ItemStack stack) {
						return inventory.isItemValidForSlot(getSlotIndex(), stack);
					}
				});
			}
		}

		// player inventory
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(inventoryPlayer, 9 + col + row * 9, 8 + col * 18, 84 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 142));
		}
	}
}
