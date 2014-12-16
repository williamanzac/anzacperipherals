package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import anzac.peripherals.tile.RemoteProxyTileEntity;

public class RemoteProxyContainer extends BaseContainer<RemoteProxyTileEntity> {

	public RemoteProxyContainer(final InventoryPlayer inventoryPlayer, final RemoteProxyTileEntity te) {
		super(te);

		// inventory
		addSlotToContainer(new Slot(te, 0, 80, 35) {
			@Override
			public boolean isItemValid(final ItemStack stack) {
				return inventory.isItemValidForSlot(getSlotIndex(), stack);
			}
		});

		int row;
		int col;
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
