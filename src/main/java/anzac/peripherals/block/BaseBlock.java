package anzac.peripherals.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
		return Names.getBlockKey(super.getUnlocalizedName());
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return null;
	}
}
