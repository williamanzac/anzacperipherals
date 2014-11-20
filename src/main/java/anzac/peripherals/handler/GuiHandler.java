package anzac.peripherals.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import anzac.peripherals.client.gui.ChargeStationGUI;
import anzac.peripherals.client.gui.CraftingRouterGUI;
import anzac.peripherals.client.gui.ItemRouterGUI;
import anzac.peripherals.client.gui.ItemStorageGUI;
import anzac.peripherals.client.gui.WorkbenchGUI;
import anzac.peripherals.inventory.ChargeStationContainer;
import anzac.peripherals.inventory.CraftingRouterContainer;
import anzac.peripherals.inventory.ItemRouterContainer;
import anzac.peripherals.inventory.ItemStorageContainer;
import anzac.peripherals.inventory.WorkbenchContainer;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.ChargeStationTileEntity;
import anzac.peripherals.tile.CraftingRouterTileEntity;
import anzac.peripherals.tile.ItemRouterTileEntity;
import anzac.peripherals.tile.ItemStorageTileEntity;
import anzac.peripherals.tile.WorkbenchTileEntity;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x,
			final int y, final int z) {
		if (!world.blockExists(x, y, z)) {
			return null;
		}

		final TileEntity tile = world.getTileEntity(x, y, z);

		switch (id) {
		case GuiIds.WORKBENCH:
			if (!(tile instanceof WorkbenchTileEntity)) {
				return null;
			}
			return new WorkbenchContainer(player.inventory, (WorkbenchTileEntity) tile);
		case GuiIds.ITEMSTORAGE:
			if (!(tile instanceof ItemStorageTileEntity)) {
				return null;
			}
			return new ItemStorageContainer(player.inventory, (ItemStorageTileEntity) tile);
		case GuiIds.ITEMROUTER:
			if (!(tile instanceof ItemRouterTileEntity)) {
				return null;
			}
			return new ItemRouterContainer(player.inventory, (ItemRouterTileEntity) tile);
		case GuiIds.CRAFTINGROUTER:
			if (!(tile instanceof CraftingRouterTileEntity)) {
				return null;
			}
			return new CraftingRouterContainer(player.inventory, (CraftingRouterTileEntity) tile);
		case GuiIds.CHARGESTATION:
			if (!(tile instanceof ChargeStationTileEntity)) {
				return null;
			}
			return new ChargeStationContainer(player.inventory, (ChargeStationTileEntity) tile);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x,
			final int y, final int z) {
		if (!world.blockExists(x, y, z)) {
			return null;
		}

		final TileEntity tile = world.getTileEntity(x, y, z);

		switch (id) {
		case GuiIds.WORKBENCH:
			if (!(tile instanceof WorkbenchTileEntity)) {
				return null;
			}
			return new WorkbenchGUI(player.inventory, (WorkbenchTileEntity) tile);
		case GuiIds.ITEMSTORAGE:
			if (!(tile instanceof ItemStorageTileEntity)) {
				return null;
			}
			return new ItemStorageGUI(player.inventory, (ItemStorageTileEntity) tile);
		case GuiIds.ITEMROUTER:
			if (!(tile instanceof ItemRouterTileEntity)) {
				return null;
			}
			return new ItemRouterGUI(player.inventory, (ItemRouterTileEntity) tile);
		case GuiIds.CRAFTINGROUTER:
			if (!(tile instanceof CraftingRouterTileEntity)) {
				return null;
			}
			return new CraftingRouterGUI(player.inventory, (CraftingRouterTileEntity) tile);
		case GuiIds.CHARGESTATION:
			if (!(tile instanceof ChargeStationTileEntity)) {
				return null;
			}
			return new ChargeStationGUI(player.inventory, (ChargeStationTileEntity) tile);
		default:
			return null;
		}
	}
}
