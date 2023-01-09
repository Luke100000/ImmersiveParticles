package immersive_particles.core.spawnTypes.descriptors;

import com.google.gson.JsonElement;
import immersive_particles.core.Searcher;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Descriptor {
    protected abstract boolean validate(Searcher searcher, int x, int y, int z);

    boolean validateIdentifiers(Set<Identifier> identifiers, Set<Identifier> tags, Supplier<Identifier> supplier, Predicate<Identifier> checkTag) {
        if (identifiers.isEmpty() && tags.isEmpty()) {
            return true;
        }
        Identifier id = supplier.get();
        for (Identifier i : identifiers) {
            if (id.equals(i)) {
                return true;
            }
        }
        for (Identifier tag : tags) {
            if (checkTag.test(tag)) {
                return true;
            }
        }
        return false;
    }

    static void readIdentifierSet(JsonElement json, Set<Identifier> identifiers, Set<Identifier> tags) {
        if (json != null) {
            for (JsonElement block : json.getAsJsonArray()) {
                String string = block.getAsString();
                if (string.startsWith("#")) {
                    tags.add(new Identifier(string.substring(1)));
                } else {
                    identifiers.add(new Identifier(string));
                }
            }
        }
    }

    public enum Side {
        ALL(List.of(new BlockPos(0, 1, 0), new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1))),
        SIDES(List.of(new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1))),
        TOP(List.of(new BlockPos(0, -1, 0))),
        BOTTOM(List.of(new BlockPos(0, 1, 0)));

        public final List<BlockPos> offsets;

        Side(List<BlockPos> offsets) {
            this.offsets = offsets;
        }
    }
}
