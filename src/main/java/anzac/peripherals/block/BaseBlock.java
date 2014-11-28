package anzac.peripherals.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import anzac.peripherals.Peripherals;
import anzac.peripherals.annotations.BlockInfo;
import anzac.peripherals.creativetab.CreativeTab;
import anzac.peripherals.reference.Names;

public abstract class BaseBlock extends BlockContainer {
	public BaseBlock(final Material material) {
		super(material);
		setCreativeTab(CreativeTab.peripheralTab);
		setBlockTextureName(Names.Blocks.generic);
	}

	public BaseBlock() {
		this(Material.rock);
	}

	@Override
	public String getUnlocalizedName() {
		final BlockInfo blockInfo = getClass().getAnnotation(BlockInfo.class);
		final String key = blockInfo.name();
		return Names.getBlockKey(key);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7,
			float par8, float par9) {
		// Drop through if the player is sneaking
		if (entityplayer.isSneaking()) {
			return false;
		}

		if (!world.isRemote) {
			final BlockInfo blockInfo = getClass().getAnnotation(BlockInfo.class);
			final int guiId = blockInfo.guiId();
			if (guiId != -1) {
				entityplayer.openGui(Peripherals.instance, guiId, world, i, j, k);
			}
		}

		return true;
	}
}
