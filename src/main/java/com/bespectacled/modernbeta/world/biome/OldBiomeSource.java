package com.bespectacled.modernbeta.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bespectacled.modernbeta.ModernBeta;
import com.bespectacled.modernbeta.api.registry.Registries;
import com.bespectacled.modernbeta.api.world.biome.BiomeBlockResolver;
import com.bespectacled.modernbeta.api.world.biome.BiomeProvider;
import com.bespectacled.modernbeta.api.world.biome.OceanBiomeResolver;
import com.bespectacled.modernbeta.api.world.cavebiome.CaveBiomeProvider;
import com.bespectacled.modernbeta.util.NbtTags;
import com.bespectacled.modernbeta.util.NbtUtil;
import com.bespectacled.modernbeta.util.settings.ImmutableSettings;
import com.bespectacled.modernbeta.util.settings.Settings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class OldBiomeSource extends BiomeSource {
    public static final Codec<OldBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(
            Codec.LONG.fieldOf("seed").stable().forGetter(biomeSource -> biomeSource.seed),
            RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(biomeSource -> biomeSource.biomeRegistry),
            NbtCompound.CODEC.fieldOf("provider_settings").forGetter(biomeSource -> biomeSource.biomeSettings.getNbt()),
            NbtCompound.CODEC.fieldOf("cave_provider_settings").forGetter(biomeSource -> biomeSource.caveBiomeSettings.getNbt()),
            Codec.INT.optionalFieldOf("version").forGetter(generator -> generator.version)
        ).apply(instance, (instance).stable(OldBiomeSource::new)));
    
    private final long seed;
    private final Registry<Biome> biomeRegistry;
    private final Settings biomeSettings;
    private final Settings caveBiomeSettings;
    private final Optional<Integer> version;
    
    private final BiomeProvider biomeProvider;
    private final CaveBiomeProvider caveBiomeProvider;

    private static List<Biome> getBiomesForRegistry(
        long seed,
        Registry<Biome> biomeRegistry, 
        NbtCompound biomeSettings,
        NbtCompound caveBiomeSettings
    ) {
        List<Biome> mainBiomes = Registries.BIOME.get(NbtUtil.readStringOrThrow(NbtTags.BIOME_TYPE, biomeSettings))
            .apply(seed, biomeSettings, biomeRegistry)
            .getBiomesForRegistry();
        
        List<Biome> caveBiomes = Registries.CAVE_BIOME.get(NbtUtil.readStringOrThrow(NbtTags.CAVE_BIOME_TYPE, caveBiomeSettings))
            .apply(seed, caveBiomeSettings, biomeRegistry)
            .getBiomesForRegistry();
        
        List<Biome> biomes = new ArrayList<>();
        biomes.addAll(mainBiomes);
        biomes.addAll(caveBiomes);
        
        return biomes;
    }
    
    public OldBiomeSource(
        long seed,
        Registry<Biome> biomeRegistry,
        NbtCompound biomeSettings,
        NbtCompound caveBiomeSettings,
        Optional<Integer> version
    ) {
        super(getBiomesForRegistry(seed, biomeRegistry, biomeSettings, caveBiomeSettings));
        
        // Validate mod version
        ModernBeta.validateVersion(version);
        
        this.seed = seed;
        this.biomeRegistry = biomeRegistry;
        this.biomeSettings = new ImmutableSettings(biomeSettings);
        this.caveBiomeSettings = new ImmutableSettings(caveBiomeSettings);
        this.version = version;
        
        this.biomeProvider = Registries.BIOME
            .get(NbtUtil.readStringOrThrow(NbtTags.BIOME_TYPE, biomeSettings))
            .apply(seed, biomeSettings, biomeRegistry);
        this.caveBiomeProvider = Registries.CAVE_BIOME
            .get(NbtUtil.readStringOrThrow(NbtTags.CAVE_BIOME_TYPE,caveBiomeSettings))
            .apply(seed, caveBiomeSettings, biomeRegistry);
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public BiomeSource withSeed(long seed) {
        return new OldBiomeSource(
            seed,
            this.biomeRegistry,
            this.biomeSettings.getNbt(),
            this.caveBiomeSettings.getNbt(),
            this.version
        );
    }
    
    public Biome getBiome(int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {    
        return this.biomeProvider.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    public Biome getOceanBiome(int biomeX, int biomeY, int biomeZ) {
        if (this.biomeProvider instanceof OceanBiomeResolver oceanBiomeResolver)
            return oceanBiomeResolver.getOceanBiomeForNoiseGen(biomeX, biomeY, biomeZ);
        
        return this.biomeProvider.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
    
    public Biome getDeepOceanBiome(int biomeX, int biomeY, int biomeZ) {
        if (this.biomeProvider instanceof OceanBiomeResolver oceanBiomeResolver)
            return oceanBiomeResolver.getDeepOceanBiomeForNoiseGen(biomeX, biomeY, biomeZ);
        
        return this.biomeProvider.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }
    
    public Biome getCaveBiome(int biomeX, int biomeY, int biomeZ) {
        return this.caveBiomeProvider.getBiome(biomeX, biomeY, biomeZ);
    }
    
    public Biome getBiomeForSurfaceGen(int x, int y, int z) {
        if (this.biomeProvider instanceof BiomeBlockResolver biomeResolver) {
            return biomeResolver.getBiomeAtBlock(x, y, z);
        }
        
        return this.biomeProvider.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2);
    }
    
    public Biome getBiomeForSurfaceGen(ChunkRegion region, BlockPos pos) {
        if (this.biomeProvider instanceof BiomeBlockResolver biomeResolver)
            return biomeResolver.getBiomeAtBlock(pos.getX(), pos.getY(), pos.getZ());
        
        return region.getBiome(pos);
    }
    
    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }
    
    public BiomeProvider getBiomeProvider() {
        return this.biomeProvider;
    }
    
    public CaveBiomeProvider getCaveBiomeProvider() {
        return this.caveBiomeProvider;
    }
    
    public Settings getBiomeSettings() {
        return this.biomeSettings;
    }
    
    public Settings getCaveBiomeSettings() {
        return this.caveBiomeSettings;
    }

    public static void register() {
        Registry.register(Registry.BIOME_SOURCE, ModernBeta.createId("old"), CODEC);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return OldBiomeSource.CODEC;
    }
}