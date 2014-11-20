package anzac.peripherals.block;

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
import anzac.peripherals.tile.DiamondChargeStationTileEntity;
import anzac.peripherals.tile.GoldChargeStationTileEntity;
import anzac.peripherals.tile.IronChargeStationTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChargingStation extends BlockContainer {

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

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		switch (type) {
		case 1:
			return new IronChargeStationTileEntity();
		case 2:
			return new GoldChargeStationTileEntity();
		case 3:
			return new DiamondChargeStationTileEntity();
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
			entityplayer.openGui(Peripherals.instance, GuiIds.CHARGESTATION, world, i, j, k);
		}

		return true;
	}
}
