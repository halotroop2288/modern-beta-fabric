package mod.bespectacled.modernbeta.world.biome.biomes.beta;

import mod.bespectacled.modernbeta.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbeta.world.biome.ModernBetaBiomeFeatures;
import mod.bespectacled.modernbeta.world.biome.ModernBetaBiomeMobs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class BiomeBetaSeasonalForest {
    public static Biome create(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        ModernBetaBiomeMobs.addCommonMobs(spawnSettings);
        ModernBetaBiomeMobs.addSquid(spawnSettings);
        ModernBetaBiomeMobs.addWolves(spawnSettings);
        ModernBetaBiomeMobs.addTurtles(spawnSettings);
        
        GenerationSettings.LookupBackedBuilder genSettings = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
        ModernBetaBiomeFeatures.addSeasonalForestFeatures(genSettings, false);
        
        return (new Biome.Builder())
            .precipitation(true)
            .temperature(1.0F)
            .downfall(0.7F)
            .effects((new BiomeEffects.Builder())
                .skyColor(ModernBetaBiomeColors.BETA_WARM_SKY_COLOR)
                .fogColor(ModernBetaBiomeColors.BETA_FOG_COLOR)
                .waterColor(ModernBetaBiomeColors.VANILLA_LUKEWARM_WATER_COLOR)
                .waterFogColor(ModernBetaBiomeColors.VANILLA_LUKEWARM_WATER_FOG_COLOR)
                .build())
            .spawnSettings(spawnSettings.build())
            .generationSettings(genSettings.build())
            .build();
    }
}
