package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class CrawlingParticle extends SimpleParticle {
    private final float speed;
    private final float wobble;
    private final float wobbleSpeed;
    private final int walkingTime;
    private final float randomizerRange;
    private final float initialRange;
    private final int roadLength;

    private int sleep;
    private int walking;

    private boolean alive = true;

    private Vector3d target;
    private final List<Vector3d> targets;
    private int targetIndex;
    private int targetIndexDirection = 1;

    public CrawlingParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);

        speed = JsonHelper.getFloat(type.behavior, "speed", 1.0f) * (random.nextFloat() * 0.2f + 0.9f);
        wobble = JsonHelper.getFloat(type.behavior, "wobble", 0.0f);
        wobbleSpeed = JsonHelper.getFloat(type.behavior, "wobbleSpeed", 0.0f);
        walkingTime = JsonHelper.getInt(type.behavior, "walkingTime", 100);
        randomizerRange = JsonHelper.getFloat(type.behavior, "randomizerRange", 1.0f);
        initialRange = JsonHelper.getFloat(type.behavior, "initialRange", 10.0f);
        roadLength = JsonHelper.getInt(type.behavior, "roadLength", 0);

        if (isRoadWalker()) {
            if (leader == null) {
                targets = new ArrayList<>();
                findTarget();
            } else {
                targets = ((CrawlingParticle)leader).targets;
                target = new Vector3d(x, y, z);
                sleep = random.nextInt(100);
            }
        } else {
            targets = null;
            findTarget();
        }
    }

    private boolean isRoadWalker() {
        return roadLength > 0;
    }

    private void findTarget() {
        addTarget();
        target = new Vector3d(
                x + (random.nextDouble() - 0.5) * initialRange,
                y + (random.nextDouble() - 0.5) * initialRange,
                z + (random.nextDouble() - 0.5) * initialRange
        );
    }

    private void addTarget() {
        if (isRoadWalker()) {
            if (targets.isEmpty()) {
                targets.add(new Vector3d(x, y, z));
            } else {
                Vector3d d = targets.get(targets.size() - 1);
                if (d.distance(x, y, z) > 0.05) {
                    targets.add(new Vector3d(x, y, z));
                }
            }
        }
    }

    private void mutateTarget() {
        addTarget();
        target.add(
                (random.nextDouble()) * randomizerRange,
                (random.nextDouble()) * randomizerRange,
                (random.nextDouble()) * randomizerRange
        );
    }

    @Override
    double getGravity() {
        return collided ? 0.0 : 1.0;
    }

    @Override
    void hitGround(double fallen) {
        if (fallen > 3) {
            kill();
        }
    }

    @Override
    public boolean tick() {
        if (shouldUpdate()) {
            if (alive) {
                // Move to the target
                if (sleep > 0) {
                    if (!isRoadWalker() || targets.size() > 1) {
                        sleep--;
                    }
                } else {
                    if (collided) {
                        moveTo(target, speed);
                    }

                    double dx = x - prevPosX;
                    double dy = y - prevPosY;
                    double dz = z - prevPosZ;

                    // Find new target
                    if (target.distance(x, y, z) < 0.1 || (dx * dx + dy * dy + dz * dz) < Math.pow(0.01, 2.0)) {
                        if (hasLeader() && isRoadWalker() && targets.size() > 1) {
                            // Follow the path the leader has chosen
                            targetIndex += targetIndexDirection;
                            if (targetIndex >= targets.size()) {
                                targetIndexDirection = -1;
                                targetIndex += targetIndexDirection;
                            }
                            if (targetIndex < 1) {
                                targetIndexDirection = 1;
                                targetIndex += targetIndexDirection;
                            }
                            target = targets.get(targetIndex);
                        } else {
                            // Randomize the target
                            findTarget();
                        }
                    }
                }

                // Mutate the position to walk more randomly
                if (!hasLeader()) {
                    walking--;
                    if (walking < 0) {
                        mutateTarget();
                        walking = (int)(walkingTime * (random.nextFloat() + 0.75f));
                    }
                }

                // Look to target
                rotateTowards(getVelocity(), 0.3f);

                // Die
                if (isTouchingPlayer()) {
                    kill();
                }

                // Path has been explored, join the swarm
                if (isRoadWalker() && !hasLeader() && targets.size() > roadLength) {
                    leader = this;
                }

                // Wobble
                if (wobble > 0) {
                    setRoll(Math.cos(age * wobbleSpeed) * wobble);
                }
            } else {
                velocityX = 0.0;
                velocityZ = 0.0;
                setYaw(getYaw());
                setPitch(0.0);
                setRoll(0.0);
            }
        }

        return super.tick();
    }

    private void kill() {
        alive = false;
        setCurrentMesh("dead");
    }
}