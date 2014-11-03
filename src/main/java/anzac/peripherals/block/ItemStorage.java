package anzac.peripherals.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import anzac.peripherals.Peripherals;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.ItemStorageTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStorage extends BaseBlock {
	@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon side;

	public ItemStorage() {
		setBlockName(Names.Blocks.itemstorage);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		front = iconRegister.registerIcon(baseName + "_front");
		side = iconRegister.registerIcon(baseName + "_side");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta) {
		switch (side) {
		case 2:
			return front;
		case 3:
		case 4:
		case 5:
			return this.side;
		}
		return super.getIcon(side, meta);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new ItemStorageTileEntity();
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z,
			final EntityPlayer entityplayer, final int par6, final float par7, final float par8, final float par9) {
		super.onBlockActivated(world, x, y, z, entityplayer, par6, par7, par8, par9);

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking()) {
			return false;
		}

		if (!world.isRemote) {
			entityplayer.openGui(Peripherals.instance, GuiIds.ITEMSTORAGE, world, x, y, z);
		}

		return true;
	}
}
