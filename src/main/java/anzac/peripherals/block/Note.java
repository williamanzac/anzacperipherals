package anzac.peripherals.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import anzac.peripherals.reference.Names;
import anzac.peripherals.tile.NoteTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Note extends BaseBlock {

	public Note() {
		setBlockName(Names.Blocks.note);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		blockIcon = iconRegister.registerIcon(baseName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta) {
		return super.getIcon(side, meta);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new NoteTileEntity();
	}

	// public boolean onBlockEventReceived(final World world, final int x, final int y, final int z, final int eventId,
	// final int parameter) {
	// super.onBlockEventReceived(world, x, y, z, eventId, parameter);
	// final TileEntity tileentity = world.getTileEntity(x, y, z);
	// return tileentity != null ? tileentity.receiveClientEvent(eventId, parameter) : false;
	// }
}
