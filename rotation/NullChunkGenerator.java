package cyclegame.rotation;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Created by luke on 5/30/15.
 */
public class NullChunkGenerator extends ChunkGenerator {

    public byte[] generate(World world, Random random, int x, int z) {
        return new byte[65536];
    }

}
