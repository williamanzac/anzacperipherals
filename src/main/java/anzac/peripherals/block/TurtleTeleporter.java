package anzac.peripherals.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import anzac.peripherals.Peripherals;
import anzac.peripherals.creativetab.CreativeTab;
import anzac.peripherals.reference.Names;
import anzac.peripherals.reference.Reference.GuiIds;
import anzac.peripherals.tile.DiamondTurtleTeleporterTileEntity;
import anzac.peripherals.tile.GoldTurtleTeleporterTileEntity;
import anzac.peripherals.tile.IronTurtleTeleporterTileEntity;
import anzac.peripherals.tile.TurtleTeleporterTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TurtleTeleporter extends BlockContainer {

	private final int type;

	public TurtleTeleporter(Material material, final int type) {
		super(material);
		this.type = type;
		setCreativeTab(CreativeTab.peripheralTab);
		setBlockName(Names.Blocks.turtleteleporter);
	}

	public TurtleTeleporter(final int type) {
		this(Material.rock, type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		blockIcon = iconRegister.registerIcon(baseName);
	}

	@Override
	public String getUnlocalizedName() {
		return Names.getBlockKey(super.getUnlocalizedName() + type);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		switch (type) {
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
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		super.onBlockActivated(world, i, j, k, entityplayer, par6, par7, par8, par9);

		// Drop through if the player is sneaking
		if (entityplayer.isSneaking()) {
			return false;
		}

		if (!world.isRemote) {
			entityplayer.openGui(Peripherals.instance, GuiIds.TURTLETELEPORTER, world, i, j, k);
		}

		return true;
	}

	@Override
	public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random random) {
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null && (tileEntity instanceof TurtleTeleporterTileEntity)) {
			for (int l = 0; l < type; ++l) {
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
}
