package anzac.peripherals.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
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

	@SideOnly(Side.CLIENT)
	private final IIcon[] icons = new IIcon[4];

	public ChargingStation(final Material material) {
		super(material);
		setCreativeTab(CreativeTab.peripheralTab);
		setBlockName(Names.Blocks.chargestation);
	}

	public ChargingStation() {
		this(Material.rock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(final IIconRegister iconRegister) {
		final String baseName = Names.unwrapUnlocalizedName(getUnlocalizedName());
		icons[1] = iconRegister.registerIcon(baseName + 1);
		icons[2] = iconRegister.registerIcon(baseName + 2);
		icons[3] = iconRegister.registerIcon(baseName + 3);
	}

	@Override
	public String getUnlocalizedName() {
		return Names.getBlockKey(super.getUnlocalizedName());
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		switch (meta) {
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
	public boolean onBlockActivated(final World world, final int i, final int j, final int k,
			final EntityPlayer entityplayer, final int par6, final float par7, final float par8, final float par9) {
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
