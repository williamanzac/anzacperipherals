package anzac.peripherals.init;

import static dan200.computercraft.api.ComputerCraftAPI.registerPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import anzac.peripherals.tile.BaseTileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class AnzacPeripheralProvider implements IPeripheralProvider {

	public static void init() {
		registerPeripheralProvider(new AnzacPeripheralProvider());
	}

	@Override
	public IPeripheral getPeripheral(final World world, final int x, final int y, final int z, final int side) {
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof BaseTileEntity) {
			return (IPeripheral) tileEntity;
		}
		return null;
	}
}
