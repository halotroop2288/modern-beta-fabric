package com.bespectacled.modernbeta.gen;

import java.util.Optional;

import com.google.common.collect.Maps;

import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import net.minecraft.world.gen.chunk.StrongholdConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class OldGeneratorConfig {
    public static final StructuresConfig STRUCTURES;
    public static final Optional<StrongholdConfig> INDEV_STRONGHOLD;
    public static final StructuresConfig INDEV_STRUCTURES;
    
    public static final NoiseSamplingConfig BETA_SAMPLING_CONFIG;
    public static final NoiseSamplingConfig ALPHA_SAMPLING_CONFIG;
    public static final NoiseSamplingConfig SKYLANDS_SAMPLING_CONFIG;
    public static final NoiseSamplingConfig INFDEV_SAMPLING_CONFIG;
    public static final NoiseSamplingConfig NETHER_SAMPLING_CONFIG;
    
    public static final GenerationShapeConfig BETA_SHAPE_CONFIG;
    public static final GenerationShapeConfig ALPHA_SHAPE_CONFIG;
    public static final GenerationShapeConfig SKYLANDS_SHAPE_CONFIG;
    public static final GenerationShapeConfig INFDEV_SHAPE_CONFIG;
    public static final GenerationShapeConfig INDEV_SHAPE_CONFIG;
    public static final GenerationShapeConfig NETHER_SHAPE_CONFIG;
    
    
    static {
        STRUCTURES = new StructuresConfig(true);
        INDEV_STRONGHOLD = Optional.of(new StrongholdConfig(0, 0, 1));
        INDEV_STRUCTURES = new StructuresConfig(INDEV_STRONGHOLD, Maps.newHashMap(StructuresConfig.DEFAULT_STRUCTURES));

        BETA_SAMPLING_CONFIG = new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0);
        ALPHA_SAMPLING_CONFIG = new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0);
        SKYLANDS_SAMPLING_CONFIG = new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0);
        INFDEV_SAMPLING_CONFIG = new NoiseSamplingConfig(1.0, 1.0, 80.0, 160.0);
        NETHER_SAMPLING_CONFIG = new NoiseSamplingConfig(1.0, 3.0, 80.0, 60.0);
        
        BETA_SHAPE_CONFIG = GenerationShapeConfig.create(
            -64, 
            192, 
            BETA_SAMPLING_CONFIG, 
            new SlideConfig(-10, 3, 0), 
            new SlideConfig(10, 3, 0),
            1, 
            2, 
            1, 
            -0.46875, 
            true, 
            true, 
            false, 
            false
        );
        
        ALPHA_SHAPE_CONFIG = GenerationShapeConfig.create(
            0, 
            128, 
            ALPHA_SAMPLING_CONFIG, 
            new SlideConfig(-10, 3, 0), 
            new SlideConfig(10, 3, 0),
            1, 
            2, 
            1, 
            -0.46875, 
            true, 
            true, 
            false, 
            false
        );
        
        SKYLANDS_SHAPE_CONFIG = GenerationShapeConfig.create(
            0, 
            128, 
            SKYLANDS_SAMPLING_CONFIG, 
            new SlideConfig(-10, 3, 0), 
            new SlideConfig(-30, 0, 0),
            2, 
            1, 
            1, 
            -0.46875, 
            true, 
            true, 
            false, 
            false
        );
        
        INFDEV_SHAPE_CONFIG = GenerationShapeConfig.create(
            0, 
            128, 
            INFDEV_SAMPLING_CONFIG, 
            new SlideConfig(-10, 3, 0), 
            new SlideConfig(10, 3, 0),
            1, 
            1, 
            1, 
            -0.46875, 
            true, 
            true, 
            false, 
            false
        );
        
        INDEV_SHAPE_CONFIG = GenerationShapeConfig.create(
            0, 
            256, 
            INFDEV_SAMPLING_CONFIG, 
            new SlideConfig(-10, 3, 0), 
            new SlideConfig(10, 3, 0),
            1, 
            1, 
            1, 
            -0.46875, 
            true, 
            true, 
            false, 
            false
        );
        
        NETHER_SHAPE_CONFIG = GenerationShapeConfig.create(
            0, 
            128, 
            NETHER_SAMPLING_CONFIG, 
            new SlideConfig(-10, 3, 0), 
            new SlideConfig(-30, 0, 0),
            1, 
            2, 
            1, 
            -0.46875, 
            true, 
            true, 
            false, 
            false
        );
    }
}
