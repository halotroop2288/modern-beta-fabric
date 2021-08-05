package com.bespectacled.modernbeta.api.world.biome.climate;

/**
 * Implemented by a climate sampler to provide temperatures and rainfall values,
 * for use by a biome provider or chunk provider.
 *
 */
public interface ClimateSampler {
    /**
     * Sample temperature value in range [0.0, 1.0] from given coordinates, 
     * to use to provide sky color.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * 
     * @return A temperature value in range [0.0, 1.0] sampled at position.
     */
    double sampleTemp(int x, int z);
    
    /**
     * Sample rainfall value in range [0.0, 1.0] from given coordinates, 
     * to use to provide sky color.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * 
     * @return A rainfall value in range [0.0, 1.0] sampled at position.
     */
    double sampleRain(int x, int z);
}
