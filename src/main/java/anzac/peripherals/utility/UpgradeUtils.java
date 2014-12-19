package anzac.peripherals.utility;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Facing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import anzac.peripherals.Peripherals;
import dan200.computercraft.api.turtle.ITurtleAccess;

public class UpgradeUtils {
	private static final int FIRST_UPGRADE_ID = 26714;
	private static int nextTurtleId = FIRST_UPGRADE_ID;

	public static int nextUpgradeId() {
		return nextTurtleId++;
	}

	public static EntityPlayer createPlayer(final World world, final ITurtleAccess turtle, final int direction) {
		final EntityPlayer turtlePlayer = Peripherals.proxy.getInternalFakePlayer((WorldServer) world).get();
		final ChunkCoordinates position = turtle.getPosition();
		turtlePlayer.posX = (position.posX + 0.5D);
		turtlePlayer.posY = (position.posY + 0.5D);
		turtlePlayer.posZ = (position.posZ + 0.5D);

		if (turtle.getPosition().equals(position)) {
			turtlePlayer.posX += 0.48D * Facing.offsetsXForSide[direction];
			turtlePlayer.posY += 0.48D * Facing.offsetsYForSide[direction];
			turtlePlayer.posZ += 0.48D * Facing.offsetsZForSide[direction];
		}

		if (direction > 2) {
			turtlePlayer.rotationYaw = toYawAngle(direction);
			turtlePlayer.rotationPitch = 0.0F;
		} else {
			turtlePlayer.rotationYaw = toYawAngle(turtle.getDirection());
			turtlePlayer.rotationPitch = toPitchAngle(direction);
		}
		turtlePlayer.prevPosX = turtlePlayer.posX;
		turtlePlayer.prevPosY = turtlePlayer.posY;
		turtlePlayer.prevPosZ = turtlePlayer.posZ;
		turtlePlayer.prevRotationPitch = turtlePlayer.rotationPitch;
		turtlePlayer.prevRotationYaw = turtlePlayer.rotationYaw;
		return turtlePlayer;
	}

	private static float toYawAngle(final int dir) {
		switch (dir) {
		case 2:
			return 180.0F;
		case 3:
			return 0.0F;
		case 4:
			return 90.0F;
		case 5:
			return 270.0F;
		}
		return 0.0F;
	}

	private static float toPitchAngle(final int dir) {
		switch (dir) {
		case 0:
			return 90.0F;
		case 1:
			return -90.0F;
		}
		return 0.0F;
	}

	@SuppressWarnings("unchecked")
	public static Entity findEntity(final World world, final Vec3 vecStart, final Vec3 vecDir, double distance) {
		Vec3 vecEnd = vecStart.addVector(vecDir.xCoord * distance, vecDir.yCoord * distance, vecDir.zCoord * distance);

		final MovingObjectPosition result = world.rayTraceBlocks(vecStart.addVector(0.0D, 0.0D, 0.0D),
				vecEnd.addVector(0.0D, 0.0D, 0.0D));
		if (result != null && result.typeOfHit == MovingObjectType.BLOCK) {
			distance = vecStart.distanceTo(result.hitVec);
			vecEnd = vecStart.addVector(vecDir.xCoord * distance, vecDir.yCoord * distance, vecDir.zCoord * distance);
		}

		final float xStretch = Math.abs(vecDir.xCoord) > 0.25D ? 0.0F : 1.0F;
		final float yStretch = Math.abs(vecDir.yCoord) > 0.25D ? 0.0F : 1.0F;
		final float zStretch = Math.abs(vecDir.zCoord) > 0.25D ? 0.0F : 1.0F;
		final AxisAlignedBB bigBox = AxisAlignedBB.getBoundingBox(Math.min(vecStart.xCoord, vecEnd.xCoord) - 0.375F
				* xStretch, Math.min(vecStart.yCoord, vecEnd.yCoord) - 0.375F * yStretch,
				Math.min(vecStart.zCoord, vecEnd.zCoord) - 0.375F * zStretch, Math.max(vecStart.xCoord, vecEnd.xCoord)
						+ 0.375F * xStretch, Math.max(vecStart.yCoord, vecEnd.yCoord) + 0.375F * yStretch,
				Math.max(vecStart.zCoord, vecEnd.zCoord) + 0.375F * zStretch);

		Entity closest = null;
		double closestDist = 99.0D;
		final List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, bigBox);
		for (final Entity entity : list) {
			if (entity.canBeCollidedWith()) {
				final AxisAlignedBB littleBox = entity.boundingBox;
				if (littleBox.isVecInside(vecStart)) {
					closest = entity;
					closestDist = 0.0D;
				} else {
					final MovingObjectPosition littleBoxResult = littleBox.calculateIntercept(vecStart, vecEnd);
					if (littleBoxResult != null) {
						final double dist = vecStart.distanceTo(littleBoxResult.hitVec);
						if (closest == null || dist <= closestDist) {
							closest = entity;
							closestDist = dist;
						}
					} else if (littleBox.intersectsWith(bigBox)) {
						if (closest == null) {
							closest = entity;
							closestDist = distance;
						}
					}
				}
			}
		}
		if (closest != null && closestDist <= distance) {
			return closest;
		}
		return null;
	}
}
