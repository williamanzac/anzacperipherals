package anzac.peripherals.init;

import static dan200.computercraft.api.ComputerCraftAPI.registerPeripheralProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import anzac.peripherals.peripherals.Target;
import anzac.peripherals.tile.BaseTileEntity;
import anzac.peripherals.tile.RemoteProxyTileEntity;
import anzac.peripherals.utility.ClassUtils;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class AnzacPeripheralProvider implements IPeripheralProvider {

	public static void init() {
		registerPeripheralProvider(new AnzacPeripheralProvider());
	}

	@Override
	public IPeripheral getPeripheral(final World world, final int x, final int y, final int z, final int side) {
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof RemoteProxyTileEntity) {
			final Target target = ((RemoteProxyTileEntity) tileEntity).getTarget();
			if (target != null) {
				final World destWorld = MinecraftServer.getServer().worldServerForDimension(target.dimension);
				IPeripheral callMethod = ClassUtils.callMethod("dan200.computercraft.ComputerCraft", "getPeripheralAt",
						new Object[] { destWorld, target.position.x, target.position.y, target.position.z, side },
						new Class[] { World.class, int.class, int.class, int.class, int.class });
				return callMethod;
			}
			return null;
		}
		if (tileEntity instanceof BaseTileEntity) {
			return (IPeripheral) tileEntity;
		}
		return null;
	}
}
