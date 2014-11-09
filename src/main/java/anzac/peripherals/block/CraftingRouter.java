package anzac.peripherals.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import anzac.peripherals.Peripherals;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.CraftingRouterTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CraftingRouter extends BaseBlock {
	@SideOnly(Side.CLIENT)
	private IIcon top;
	@SideOnly(Side.CLIENT)
	private IIcon side;

	public CraftingRouter() {
		setBlockName(Names.Blocks.craftingrouter);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		top = iconRegister.registerIcon(baseName + "_top");
		side = iconRegister.registerIcon(baseName + "_side");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta) {
		switch (side) {
		case 1:
			return top;
		case 2:
		case 3:
		case 4:
		case 5:
			return this.side;
		}
		return super.getIcon(side, meta);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new CraftingRouterTileEntity();
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		super.onBlockActivated(world, i, j, k, entityplayer, par6, par7, par8, par9);

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking()) {
			return false;
		}

		if (!world.isRemote) {
			entityplayer.openGui(Peripherals.instance, GuiIds.CRAFTINGROUTER, world, i, j, k);
		}

		return true;
	}
}
