package anzac.peripherals.utility;

public class UpgradeUtils {
	private static final int FIRST_UPGRADE_ID = 26714;
	private static int nextTurtleId = FIRST_UPGRADE_ID;

	public static int nextUpgradeId() {
		return nextTurtleId++;
	}
}
