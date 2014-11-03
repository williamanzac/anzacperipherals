package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.ItemStorageTileEntity;

public class ItemStorageContainer extends BaseContainer<ItemStorageTileEntity> {

	public ItemStorageContainer(final InventoryPlayer inventoryPlayer, final ItemStorageTileEntity te) {
		super(te);

		int row;
		int col;
		// inventory
		for (row = 0; row < 7; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(te, col + row * 9, 8 + col * 18, 18 + row * 18));
			}
		}

		// player inventory
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(inventoryPlayer, 9 + col + row * 9, 8 + col * 18, 158 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 216));
		}
	}
}
