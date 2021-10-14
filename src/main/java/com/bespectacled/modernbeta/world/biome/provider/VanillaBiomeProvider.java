package com.bespectacled.modernbeta.world.biome.provider;

import java.util.List;
import java.util.stream.Collectors;

import com.bespectacled.modernbeta.ModernBeta;
import com.bespectacled.modernbeta.api.world.biome.ClimateBiomeProvider;
import com.bespectacled.modernbeta.util.NbtTags;
import com.bespectacled.modernbeta.util.NbtUtil;
import com.bespectacled.modernbeta.world.biome.provider.climate.VanillaClimateSampler;
import com.bespectacled.modernbeta.world.biome.vanilla.VanillaBiomeLayer;
import com.bespectacled.modernbeta.world.biome.vanilla.VanillaOceanLayer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class VanillaBiomeProvider extends ClimateBiomeProvider {
    private final BiomeLayerSampler biomeSampler;
    private final BiomeLayerSampler oceanSampler;
    
    public VanillaBiomeProvider(long seed, NbtCompound settings, Registry<Biome> biomeRegistry) {
        super(seed, settings, biomeRegistry, new VanillaClimateSampler(seed, buildBiomeLayers(seed, settings), biomeRegistry));

        // Pull biome sampler back out of VanillaClimateSampler
        this.biomeSampler = ((VanillaClimateSampler)this.getClimateSampler()).getBiomeSampler();
        this.oceanSampler = buildOceanLayers(seed, settings);
    }

    @Override
    public Biome getBiomeForNoiseGen(Registry<Biome> biomeRegistry, int biomeX, int biomeY, int biomeZ) {
        return this.biomeSampler.sample(biomeRegistry, biomeX, biomeZ);
    }

    @Override
    public Biome getOceanBiomeForNoiseGen(Registry<Biome> biomeRegistry, int biomeX, int biomeY, int biomeZ) {
        return this.oceanSampler.sample(biomeRegistry, biomeX, biomeZ); 
    }

    @Override
    public List<RegistryKey<Biome>> getBiomesForRegistry() {
        return BuiltinRegistries.BIOME.getEntries()
            .stream()
            .filter(e -> this.isValidCategory(e.getValue().getCategory()))
            .map(e -> e.getKey())
            .collect(Collectors.toList());
    }
    
    private boolean isValidCategory(Category category) {
        boolean isValid = 
            category != Category.NONE &&
            //category != Category.BEACH &&
            //category != Category.OCEAN &&
            category != Category.NETHER &&
            category != Category.THEEND;
        
        return isValid;
    }
    
    private static BiomeLayerSampler buildBiomeLayers(long seed, NbtCompound settings) {
        int vanillaBiomeSize = NbtUtil.readInt(
            NbtTags.VANILLA_BIOME_SIZE, 
            settings, 
            ModernBeta.BIOME_CONFIG.vanillaBiomeConfig.vanillaBiomeSize
        );
        
        return VanillaBiomeLayer.build(seed, false, vanillaBiomeSize, -1);
    }
    
    private static BiomeLayerSampler buildOceanLayers(long seed, NbtCompound settings) {
        int vanillaOceanBiomeSize = NbtUtil.readInt(
            NbtTags.VANILLA_OCEAN_BIOME_SIZE, 
            settings, 
            ModernBeta.BIOME_CONFIG.vanillaBiomeConfig.vanillaOceanBiomeSize
        );
     
        return VanillaOceanLayer.build(seed, false, vanillaOceanBiomeSize, -1);
    }
}
