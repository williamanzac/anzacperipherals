package anzac.peripherals.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import anzac.peripherals.annotations.BlockInfo;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.BrewingTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BlockInfo(name = Names.Blocks.brewingstation, guiId = GuiIds.BREWINGSTATION)
public class BrewingStation extends BaseBlock {
	@SideOnly(Side.CLIENT)
	private IIcon side;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		side = iconRegister.registerIcon(baseName + "_side");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, final int meta) {
		switch (side) {
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
		return new BrewingTileEntity();
	}

}
