package anzac.peripherals.block;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import anzac.peripherals.annotations.BlockInfo;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.DiamondTurtleTeleporterTileEntity;
import anzac.peripherals.tile.GoldTurtleTeleporterTileEntity;
import anzac.peripherals.tile.IronTurtleTeleporterTileEntity;
import anzac.peripherals.tile.TurtleTeleporterTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BlockInfo(name = Names.Blocks.turtleteleporter, guiId = GuiIds.TURTLETELEPORTER)
public class TurtleTeleporter extends BaseBlock {

	@SideOnly(Side.CLIENT)
	private final IIcon[] icons = new IIcon[4];

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		icons[1] = iconRegister.registerIcon(baseName + 1);
		icons[2] = iconRegister.registerIcon(baseName + 2);
		icons[3] = iconRegister.registerIcon(baseName + 3);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		switch (meta) {
		case 1:
			return new IronTurtleTeleporterTileEntity();
		case 2:
			return new GoldTurtleTeleporterTileEntity();
		case 3:
			return new DiamondTurtleTeleporterTileEntity();
		}
		return null;
	}

	@Override
	public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random random) {
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		final int meta = world.getBlockMetadata(x, y, z);
		if (tileEntity != null && (tileEntity instanceof TurtleTeleporterTileEntity)) {
			for (int l = 0; l < meta; ++l) {
				final double d1 = y + random.nextFloat();
				final int i1 = random.nextInt(2) * 2 - 1;
				final int j1 = random.nextInt(2) * 2 - 1;
				final double d3 = (random.nextFloat() - 0.5D) * 0.125D;
				final double d5 = z + 0.5D + 0.25D * j1;
				final double d4 = random.nextFloat() * 1.0F * j1;
				final double d6 = x + 0.5D + 0.25D * i1;
				final double d2 = random.nextFloat() * 1.0F * i1;
				world.spawnParticle("portal", d6, d1, d5, d2, d3, d4);
			}
		}
	}

	@Override
	public int damageDropped(final int meta) {
		return meta;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(final Item item, final CreativeTabs tab, final List list) {
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
	}

	@Override
	public IIcon getIcon(final int side, final int meta) {
		return icons[meta];
	}
}
