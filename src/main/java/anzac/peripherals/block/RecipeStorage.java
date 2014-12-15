package anzac.peripherals.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import anzac.peripherals.annotations.BlockInfo;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.RecipeStorageTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BlockInfo(name = Names.Blocks.recipestorage, guiId = GuiIds.RECIPESTORAGE)
public class RecipeStorage extends BaseBlock {
	@SideOnly(Side.CLIENT)
	private IIcon top;
	@SideOnly(Side.CLIENT)
	private IIcon front;
	@SideOnly(Side.CLIENT)
	private IIcon side;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		front = iconRegister.registerIcon(baseName + "_front");
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
			return front;
		case 3:
		case 4:
		case 5:
			return this.side;
		}
		return super.getIcon(side, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new RecipeStorageTileEntity();
	}
}
