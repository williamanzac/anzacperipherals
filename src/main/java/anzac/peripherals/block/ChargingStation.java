package anzac.peripherals.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import anzac.peripherals.creativetab.CreativeTab;
import anzac.peripherals.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChargingStation extends Block {

	private final int type;

	public ChargingStation(Material material, final int type) {
		super(material);
		this.type = type;
		setCreativeTab(CreativeTab.peripheralTab);
		setBlockName(Names.Blocks.chargestation);
	}

	public ChargingStation(final int type) {
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
}
