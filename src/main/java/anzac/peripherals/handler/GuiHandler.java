package anzac.peripherals.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import anzac.peripherals.client.gui.AnvilGUI;
import anzac.peripherals.client.gui.BrewingStationGUI;
import anzac.peripherals.client.gui.ChargeStationGUI;
import anzac.peripherals.client.gui.CraftingRouterGUI;
import anzac.peripherals.client.gui.EnchanterGUI;
import anzac.peripherals.client.gui.FluidRouterGUI;
import anzac.peripherals.client.gui.FluidStorageGUI;
import anzac.peripherals.client.gui.ItemRouterGUI;
import anzac.peripherals.client.gui.ItemStorageGUI;
import anzac.peripherals.client.gui.RecipeStorageGUI;
import anzac.peripherals.client.gui.RemoteProxyGUI;
import anzac.peripherals.client.gui.TurtleTeleporterGUI;
import anzac.peripherals.client.gui.WorkbenchGUI;
import anzac.peripherals.inventory.AnvilContainer;
import anzac.peripherals.inventory.BrewingStationContainer;
import anzac.peripherals.inventory.ChargeStationContainer;
import anzac.peripherals.inventory.CraftingRouterContainer;
import anzac.peripherals.inventory.EnchanterContainer;
import anzac.peripherals.inventory.FluidRouterContainer;
import anzac.peripherals.inventory.FluidStorageContainer;
import anzac.peripherals.inventory.ItemRouterContainer;
import anzac.peripherals.inventory.ItemStorageContainer;
import anzac.peripherals.inventory.RecipeStorageContainer;
import anzac.peripherals.inventory.RemoteProxyContainer;
import anzac.peripherals.inventory.TurtleTeleporterContainer;
import anzac.peripherals.inventory.WorkbenchContainer;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.AnvilTileEntity;
import anzac.peripherals.tile.BrewingStationTileEntity;
import anzac.peripherals.tile.ChargingStationTileEntity;
import anzac.peripherals.tile.CraftingRouterTileEntity;
import anzac.peripherals.tile.EnchanterTileEntity;
import anzac.peripherals.tile.FluidRouterTileEntity;
import anzac.peripherals.tile.FluidStorageTileEntity;
import anzac.peripherals.tile.ItemRouterTileEntity;
import anzac.peripherals.tile.ItemStorageTileEntity;
import anzac.peripherals.tile.RecipeStorageTileEntity;
import anzac.peripherals.tile.RemoteProxyTileEntity;
import anzac.peripherals.tile.TurtleTeleporterTileEntity;
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
			if (!(tile instanceof ChargingStationTileEntity)) {
				return null;
			}
			return new ChargeStationContainer(player.inventory, (ChargingStationTileEntity) tile);
		case GuiIds.TURTLETELEPORTER:
			if (!(tile instanceof TurtleTeleporterTileEntity)) {
				return null;
			}
			return new TurtleTeleporterContainer(player.inventory, (TurtleTeleporterTileEntity) tile);
		case GuiIds.FLUIDROUTER:
			if (!(tile instanceof FluidRouterTileEntity)) {
				return null;
			}
			return new FluidRouterContainer(player.inventory, (FluidRouterTileEntity) tile);
		case GuiIds.FLUIDSTORAGE:
			if (!(tile instanceof FluidStorageTileEntity)) {
				return null;
			}
			return new FluidStorageContainer(player.inventory, (FluidStorageTileEntity) tile);
		case GuiIds.RECIPESTORAGE:
			if (!(tile instanceof RecipeStorageTileEntity)) {
				return null;
			}
			return new RecipeStorageContainer(player.inventory, (RecipeStorageTileEntity) tile);
		case GuiIds.REMOTEPROXY:
			if (!(tile instanceof RemoteProxyTileEntity)) {
				return null;
			}
			return new RemoteProxyContainer(player.inventory, (RemoteProxyTileEntity) tile);
		case GuiIds.BREWINGSTATION:
			if (!(tile instanceof BrewingStationTileEntity)) {
				return null;
			}
			return new BrewingStationContainer(player.inventory, (BrewingStationTileEntity) tile);
		case GuiIds.ENCHANTER:
			if (!(tile instanceof EnchanterTileEntity)) {
				return null;
			}
			return new EnchanterContainer(player.inventory, (EnchanterTileEntity) tile);
		case GuiIds.ANVIL:
			if (!(tile instanceof AnvilTileEntity)) {
				return null;
			}
			return new AnvilContainer(player.inventory, (AnvilTileEntity) tile);
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
			if (!(tile instanceof ChargingStationTileEntity)) {
				return null;
			}
			return new ChargeStationGUI(player.inventory, (ChargingStationTileEntity) tile);
		case GuiIds.TURTLETELEPORTER:
			if (!(tile instanceof TurtleTeleporterTileEntity)) {
				return null;
			}
			return new TurtleTeleporterGUI(player.inventory, (TurtleTeleporterTileEntity) tile);
		case GuiIds.FLUIDROUTER:
			if (!(tile instanceof FluidRouterTileEntity)) {
				return null;
			}
			return new FluidRouterGUI(player.inventory, (FluidRouterTileEntity) tile);
		case GuiIds.FLUIDSTORAGE:
			if (!(tile instanceof FluidStorageTileEntity)) {
				return null;
			}
			return new FluidStorageGUI(player.inventory, (FluidStorageTileEntity) tile);
		case GuiIds.RECIPESTORAGE:
			if (!(tile instanceof RecipeStorageTileEntity)) {
				return null;
			}
			return new RecipeStorageGUI(player.inventory, (RecipeStorageTileEntity) tile);
		case GuiIds.REMOTEPROXY:
			if (!(tile instanceof RemoteProxyTileEntity)) {
				return null;
			}
			return new RemoteProxyGUI(player.inventory, (RemoteProxyTileEntity) tile);
		case GuiIds.BREWINGSTATION:
			if (!(tile instanceof BrewingStationTileEntity)) {
				return null;
			}
			return new BrewingStationGUI(player.inventory, (BrewingStationTileEntity) tile);
		case GuiIds.ENCHANTER:
			if (!(tile instanceof EnchanterTileEntity)) {
				return null;
			}
			return new EnchanterGUI(player.inventory, (EnchanterTileEntity) tile);
		case GuiIds.ANVIL:
			if (!(tile instanceof AnvilTileEntity)) {
				return null;
			}
			return new AnvilGUI(player.inventory, (AnvilTileEntity) tile);
		default:
			return null;
		}
	}
}
