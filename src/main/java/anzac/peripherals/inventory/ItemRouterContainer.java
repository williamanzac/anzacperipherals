package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.ItemRouterTileEntity;

public class ItemRouterContainer extends BaseContainer<ItemRouterTileEntity> {

	public ItemRouterContainer(final InventoryPlayer inventoryPlayer, final ItemRouterTileEntity te) {
		super(te);

		// inventory
		addSlotToContainer(new Slot(te, 0, 80, 35));

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
