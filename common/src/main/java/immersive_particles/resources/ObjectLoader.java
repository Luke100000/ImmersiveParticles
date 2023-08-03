package immersive_particles.resources;

import immersive_particles.Main;
import immersive_particles.util.obj.Builder;
import immersive_particles.util.obj.Mesh;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectLoader extends SinglePreparationResourceReloader<List<Identifier>> {
    protected static final Identifier ID = Main.locate("objects");

    public static final Map<Identifier, Map<String, Mesh>> objects = new HashMap<>();

    @Override
    protected List<Identifier> prepare(ResourceManager manager, Profiler profiler) {
        return new ArrayList<>(manager.findResources("objects", n -> n.endsWith(".obj")));
    }

    @Override
    protected void apply(List<Identifier> o, ResourceManager manager, Profiler profiler) {
        objects.clear();
        o.forEach(id -> {
            try {
                InputStream stream = manager.getResource(id).getInputStream();
                Map<String, Mesh> faces = new Builder(new BufferedReader(new InputStreamReader(stream))).objects;
                Identifier newId = new Identifier(id.getNamespace(), id.getPath().substring(8, id.getPath().length() - 4));
                objects.put(newId, faces);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
