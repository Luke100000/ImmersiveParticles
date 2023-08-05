package immersive_particles.core.tasks;

import immersive_particles.core.ImmersiveParticle;

public abstract class Task {
    private final ImmersiveParticle particle;

    protected Task(ImmersiveParticle particle) {
        this.particle = particle;
    }

    public abstract void tick(ImmersiveParticle particle);

    public ImmersiveParticle getParticle() {
        return particle;
    }

    public abstract static class Settings {
        public abstract Task createTask(ImmersiveParticle particle);
    }
}

