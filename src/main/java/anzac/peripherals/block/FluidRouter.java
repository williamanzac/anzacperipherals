package anzac.peripherals.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import anzac.peripherals.annotations.BlockInfo;
import anzac.peripherals.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BlockInfo(name = Names.Blocks.fluidrouter)
public class FluidRouter extends BaseBlock {

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
}
