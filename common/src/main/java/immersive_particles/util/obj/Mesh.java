package immersive_particles.util.obj;

import java.util.ArrayList;

public class Mesh {
    public final ArrayList<Face> faces = new ArrayList<>();

    public Mesh() {
        // nop
    }

    public void add(Face face) {
        faces.add(face);
    }

    public String toString() {
        StringBuilder result = new StringBuilder("\tfaces: " + faces.size() + " :\n");
        for (Face f : faces) {
            result.append(" \t\t( ").append(f.toString()).append(" )\n");
        }
        return result.toString();
    }
}