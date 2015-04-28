package anzac.peripherals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import anzac.peripherals.tile.CraftingRouterTileEntity;

public class CraftingRouterContainer extends BaseContainer<CraftingRouterTileEntity> {

	public CraftingRouterContainer(final InventoryPlayer inventoryPlayer, final CraftingRouterTileEntity te) {
		super(te);

		int row;
		int col;

		// result
		for (row = 0; row < 3; ++row) {
			addSlotToContainer(new SlotPhantom(te.craftResult, row, 68, 17 + row * 18));
		}
		// recipe
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 3; ++col) {
				addSlotToContainer(new SlotPhantom(te.craftMatrix, col + row * 3, 8 + col * 18, 17 + row * 18));
			}
		}

		// input
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 3; ++col) {
				addSlotToContainer(new Slot(te, col + row * 3, 92 + col * 18, 17 + row * 18));
			}
		}

		// player inventory
		for (row = 0; row < 3; ++row) {
			for (col = 0; col < 9; ++col) {
				addSlotToContainer(new Slot(inventoryPlayer, 9 + col + row * 9, 8 + col * 18, 88 + row * 18));
			}
		}
		for (row = 0; row < 9; ++row) {
			addSlotToContainer(new Slot(inventoryPlayer, row, 8 + row * 18, 146));
		}
	}
}
