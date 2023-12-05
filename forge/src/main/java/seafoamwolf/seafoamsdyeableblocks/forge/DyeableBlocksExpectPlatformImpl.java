package seafoamwolf.seafoamsdyeableblocks.forge;

import seafoamwolf.seafoamsdyeableblocks.DyeableBlocksExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class DyeableBlocksExpectPlatformImpl {
    /**
     * This is our actual method to {@link DyeableBlocksExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}