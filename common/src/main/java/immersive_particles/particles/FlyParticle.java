package immersive_particles.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlyParticle extends Particle {
    private final Sprite sprite;

    public FlyParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96f;
        this.field_28787 = true;
        this.velocityX *= 0.1f;
        this.velocityY = this.velocityY * (double)0.1f;
        this.velocityZ *= 0.1f;
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.maxAge = 20 * 60;
        this.sprite = spriteProvider.getSprite(world.random);
    }

    private static final double[][][] cubeVertices = new double[][][] {
            { // front
                    {-0.5 / 16.0, 0.5 / 16.0, -0.5 / 16.0},
                    {0.5 / 16.0, 0.5 / 16.0, -0.5 / 16.0},
                    {0.5 / 16.0, -0.5 / 16.0, -0.5 / 16.0},
                    {-0.5 / 16.0, -0.5 / 16.0, -0.5 / 16.0},
            },
            { // back
                    {-0.5 / 16.0, -0.5 / 16.0, 0.5 / 16.0},
                    {0.5 / 16.0, -0.5 / 16.0, 0.5 / 16.0},
                    {0.5 / 16.0, 0.5 / 16.0, 0.5 / 16.0},
                    {-0.5 / 16.0, 0.5 / 16.0, 0.5 / 16.0},
            },
            { // left
                    {-0.5 / 16.0, -0.5 / 16.0, -0.5 / 16.0},
                    {-0.5 / 16.0, -0.5 / 16.0, 0.5 / 16.0},
                    {-0.5 / 16.0, 0.5 / 16.0, 0.5 / 16.0},
                    {-0.5 / 16.0, 0.5 / 16.0, -0.5 / 16.0},
            },
            { // right
                    {0.5 / 16.0, 0.5 / 16.0, -0.5 / 16.0},
                    {0.5 / 16.0, 0.5 / 16.0, 0.5 / 16.0},
                    {0.5 / 16.0, -0.5 / 16.0, 0.5 / 16.0},
                    {0.5 / 16.0, -0.5 / 16.0, -0.5 / 16.0},
            },
            { // top
                    {-0.5 / 16.0, 0.5 / 16.0, 0.5 / 16.0},
                    {0.5 / 16.0, 0.5 / 16.0, 0.5 / 16.0},
                    {0.5 / 16.0, 0.5 / 16.0, -0.5 / 16.0},
                    {-0.5 / 16.0, 0.5 / 16.0, -0.5 / 16.0},
            },
            { // bottom
                    {-0.5 / 16.0, -0.5 / 16.0, -0.5 / 16.0},
                    {0.5 / 16.0, -0.5 / 16.0, -0.5 / 16.0},
                    {0.5 / 16.0, -0.5 / 16.0, 0.5 / 16.0},
                    {-0.5 / 16.0, -0.5 / 16.0, 0.5 / 16.0},
            }
    };

    public void buildCube(VertexConsumer vertexConsumer, double x, double y, double z, float width, float height, float depth, double rotation, double scale, int light) {
        buildCube(vertexConsumer, x, y, z, 0.0, 0.0, 0.0, width, height, depth, rotation, scale, light);
    }

    public void buildCube(VertexConsumer vertexConsumer, double x, double y, double z, double ox, double oy, double oz, float width, float height, float depth, double rotation, double scale, int light) {
        double cos = Math.cos(rotation);
        double sin = Math.sin(rotation);

        float[][] cubeUvs = new float[][] {
                {depth, depth, width, height},         // front
                {depth + width, depth, width, height}, // back
                {0, depth, depth, height},             // left
                {depth + width, depth, depth, height}, // right
                {depth + width, 0, width, depth},      // top
                {depth, 0, width, depth},              // bottom
        };

        for (int side = 0; side < 6; side++) {
            float[] uv = cubeUvs[side];
            double[][] vertices = cubeVertices[side];
            for (int vertex = 0; vertex < 4; vertex++) {
                double px = ox + vertices[vertex][0] * width;
                double py = oy + vertices[vertex][1] * height;
                double pz = oz + vertices[vertex][2] * depth;
                vertexConsumer
                        .vertex(
                                (float)(x + (px * cos - py * sin) * scale),
                                (float)(y + (py * cos + px * sin) * scale),
                                (float)(z + pz * scale))
                        .texture(
                                sprite.getFrameU(uv[0] + (vertex % 2 == 0 ? 0 : 1) * uv[2]),
                                sprite.getFrameV(uv[1] + (vertex >= 2 ? 0 : 1) * uv[3])
                        )
                        .color(red, green, blue, alpha)
                        .light(light)
                        .next();
            }
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cam = camera.getPos();
        float x = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cam.getX());
        float y = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cam.getY());
        float z = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cam.getZ());

        int light = this.getBrightness(tickDelta);
        buildCube(vertexConsumer, x, y, z, 2, 2, 3, 0, 1, light);
    }


    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
            return new FlyParticle(clientWorld, spriteProvider, x, y, z, vx, vy, vz);
        }
    }
}

