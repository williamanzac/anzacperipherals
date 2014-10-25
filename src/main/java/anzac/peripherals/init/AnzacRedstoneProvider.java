package anzac.peripherals.init;

import static dan200.computercraft.api.ComputerCraftAPI.registerBundledRedstoneProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.tile.RedstoneTileEntity;
import dan200.computercraft.api.redstone.IBundledRedstoneProvider;

public class AnzacRedstoneProvider implements IBundledRedstoneProvider {

	public static void init() {
		registerBundledRedstoneProvider(new AnzacRedstoneProvider());
	}

	@Override
	public int getBundledRedstoneOutput(final World world, final int x, final int y, final int z, final int side) {
		final TileEntity entity = world.getTileEntity(x, y, z);
		if (entity instanceof RedstoneTileEntity) {
			return ((RedstoneTileEntity) entity).getBundledOutput(ForgeDirection.values()[side]);
		}
		return -1;
	}
}
