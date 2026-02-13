package me.cortex.voxy.commonImpl.mixin.minecraft;

import me.cortex.voxy.common.Logger;
import me.cortex.voxy.commonImpl.IWorldGetIdentifier;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(Level.class)
public class MixinWorld implements IWorldGetIdentifier {
    @Unique
    private WorldIdentifier identifier;
    @Unique
    private final String[] overworldNames = {"main_sw", "main_nw", "main_no"};

    @Inject(method = "<init>", at = @At("RETURN"))
    private void voxy$injectIdentifier(WritableLevelData properties,
                                       ResourceKey<Level> key,
                                       RegistryAccess registryManager,
                                       Holder<DimensionType> dimensionEntry,
                                       boolean isClient,
                                       boolean debugWorld,
                                       long seed,
                                       int maxChainedNeighborUpdates,
                                       CallbackInfo ci) {
        if (key != null) {
            // Path will be "overworld", "the_nether", or our desired names we want to overwrite!
            String path = key.identifier().getPath();
            Logger.info("Entered dimension with path " + path);
            if (Arrays.asList(overworldNames).contains(path)) {
                Logger.info("Dimension has matched, replacing with overworld");
                key = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("minecraft", "overworld"));
            }
            this.identifier = new WorldIdentifier(key, seed, dimensionEntry == null?null:dimensionEntry.unwrapKey().orElse(null));
        } else {
            this.identifier = null;
        }
    }

    @Override
    public WorldIdentifier voxy$getIdentifier() {
        return this.identifier;
    }
}
