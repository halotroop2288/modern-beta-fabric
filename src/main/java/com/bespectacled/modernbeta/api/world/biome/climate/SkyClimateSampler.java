package com.bespectacled.modernbeta.api.world.biome.climate;

public interface SkyClimateSampler {
    /**
     * Sample temperature value in range [0.0, 1.0] from given coordinates, 
     * to use to provide sky color.
     * 
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * 
     * @return A temperature value in range [0.0, 1.0] sampled at position.
     */
    double sampleSkyTemp(int x, int z);
}
