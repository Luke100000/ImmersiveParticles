package immersive_insects;

public class SpawnLocation {
    public final double chance;
    public final double x;
    public final double y;
    public final double z;
    public final double yaw;
    public final double rot;
    public final double whatever;

    public SpawnLocation(double chance, double x, double y, double z, double yaw, double rot, double whatever) {
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.rot = rot;
        this.whatever = whatever;
    }
}