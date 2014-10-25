package anzac.peripherals.block;

import static dan200.computercraft.api.ComputerCraftAPI.getBundledRedstoneOutput;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import anzac.peripherals.reference.Names;
import anzac.peripherals.tile.RedstoneTileEntity;
import anzac.peripherals.utility.LogHelper;
import anzac.peripherals.utility.Position;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Redstone extends BaseBlock {

	public Redstone() {
		setBlockName(Names.Blocks.redstone);
	}

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

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		LogHelper.info("creating RedstoneTileEntity");
		return new RedstoneTileEntity();
	}

	@Override
	public boolean canConnectRedstone(final IBlockAccess world, final int x, final int y, final int z, final int side) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public int isProvidingWeakPower(final IBlockAccess world, final int x, final int y, final int z, final int side) {
		final TileEntity entity = world.getTileEntity(x, y, z);
		if (entity instanceof RedstoneTileEntity) {
			return ((RedstoneTileEntity) entity).getOutput(Facing.oppositeSide[side]);
		}
		return super.isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block neighbor) {
		super.onNeighborBlockChange(world, x, y, z, neighbor);
		final TileEntity entity = world.getTileEntity(x, y, z);
		if (entity instanceof RedstoneTileEntity) {
			for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
				final RedstoneTileEntity redstoneTileEntity = (RedstoneTileEntity) entity;
				redstoneTileEntity.setInput(direction.ordinal(), getInputStrength(world, x, y, z, direction));
				redstoneTileEntity.setBundledInput(direction.ordinal(),
						getBundledRedstoneOutput(world, x, y, z, direction.getOpposite().ordinal()));
			}
		}
	}

	@Override
	public boolean isNormalCube() {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	protected int getInputStrength(final World world, final int x, final int y, final int z, final ForgeDirection side) {
		final Position p = new Position(x, y, z, side);
		p.moveForwards(1);
		final int l1 = world.getIndirectPowerLevelTo(p.x, p.y, p.z, side.getOpposite().ordinal());
		return l1 >= 15 ? l1 : Math.max(l1,
				world.getBlock(p.x, p.y, p.z) == Blocks.redstone_wire ? world.getBlockMetadata(p.x, p.y, p.z) : 0);
	}

	@Override
	public void updateTick(final World world, final int x, final int y, final int z, final Random random) {
		super.updateTick(world, x, y, z, random);
		final TileEntity entity = world.getTileEntity(x, y, z);
		if (entity instanceof RedstoneTileEntity) {
			for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
				((RedstoneTileEntity) entity)
						.setInput(direction.ordinal(), getInputStrength(world, x, y, z, direction));
			}
		}
	}

	@Override
	public int isProvidingStrongPower(final IBlockAccess world, final int x, final int y, final int z, final int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}
}
