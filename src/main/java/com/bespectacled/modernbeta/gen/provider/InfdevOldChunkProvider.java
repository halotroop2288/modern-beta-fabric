package com.bespectacled.modernbeta.gen.provider;

import java.util.Random;

import com.bespectacled.modernbeta.biome.OldBiomeSource;
import com.bespectacled.modernbeta.gen.BlockStructureWeightSampler;
import com.bespectacled.modernbeta.gen.OldGenUtil;
import com.bespectacled.modernbeta.gen.OldGeneratorSettings;
import com.bespectacled.modernbeta.noise.PerlinOctaveNoise;
import com.bespectacled.modernbeta.util.BlockStates;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;

public class InfdevOldChunkProvider extends AbstractChunkProvider {
    private boolean generateInfdevPyramid = true;
    private boolean generateInfdevWall = true;

    private final PerlinOctaveNoise noiseOctavesA;
    private final PerlinOctaveNoise noiseOctavesB;
    private final PerlinOctaveNoise noiseOctavesC;
    private final PerlinOctaveNoise noiseOctavesD;
    private final PerlinOctaveNoise noiseOctavesE;
    private final PerlinOctaveNoise noiseOctavesF;
    private final PerlinOctaveNoise forestNoiseOctaves;

    private final Block blockArr[][][];
    
    public InfdevOldChunkProvider(long seed, OldGeneratorSettings settings) {
        //super(seed, settings);
        super(seed, 0, 128, 64, 0, -10, 2, 1, 1.0, 1.0, 80, 160, true, true, BlockStates.STONE, BlockStates.WATER, settings);
        
        this.blockArr = new Block[16][this.worldHeight][16];
        
        // Noise Generators
        noiseOctavesA = new PerlinOctaveNoise(RAND, 16, true); 
        noiseOctavesB = new PerlinOctaveNoise(RAND, 16, true);
        noiseOctavesC = new PerlinOctaveNoise(RAND, 8, true);
        noiseOctavesD = new PerlinOctaveNoise(RAND, 4, true);
        noiseOctavesE = new PerlinOctaveNoise(RAND, 4, true);
        noiseOctavesF = new PerlinOctaveNoise(RAND, 5, true);
        
        new PerlinOctaveNoise(RAND, 3, true);
        new PerlinOctaveNoise(RAND, 3, true);
        new PerlinOctaveNoise(RAND, 3, true);
        
        forestNoiseOctaves = new PerlinOctaveNoise(RAND, 8, true);
        
        if (this.providerSettings.contains("generateInfdevPyramid")) 
            this.generateInfdevPyramid = this.providerSettings.getBoolean("generateInfdevPyramid");
        if (this.providerSettings.contains("generateInfdevWall")) 
            this.generateInfdevWall = this.providerSettings.getBoolean("generateInfdevWall");
        
        setForestOctaves(forestNoiseOctaves);
    }

    @Override
    public Chunk provideChunk(WorldAccess worldAccess, StructureAccessor structureAccessor, Chunk chunk, OldBiomeSource biomeSource) {
        generateTerrain(chunk.getPos().getStartX(), chunk.getPos().getStartZ(), biomeSource);  
        setTerrain((ChunkRegion)worldAccess, chunk, structureAccessor, biomeSource);
        return chunk;
    }

    @Override
    public void provideSurface(ChunkRegion region, Chunk chunk, OldBiomeSource biomeSource) {}

    @Override
    public int getHeight(int x, int z, Type type) {
        BlockPos structPos = new BlockPos(x, 0, z);
        int height = this.worldHeight - 1;
        
        if (HEIGHTMAP_CACHE.get(structPos) == null) {            
            height = this.sampleHeightMap(x, z);
            
            HEIGHTMAP_CACHE.put(structPos, height);
        } 
        
        height = HEIGHTMAP_CACHE.get(structPos);
        
        // Not ideal
        if (type == Heightmap.Type.WORLD_SURFACE_WG && height < this.seaLevel)
            height = this.seaLevel + 1;
         
        return height;
    }
    
    @Override
    public PerlinOctaveNoise getBeachNoiseOctaves() {
        return null;
    }
    
    private void setTerrain(ChunkRegion region, Chunk chunk, StructureAccessor structureAccessor, OldBiomeSource biomeSource) {
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();
        
        Heightmap heightmapOCEAN = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmapSURFACE = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        
        BlockStructureWeightSampler structureWeightSampler = new BlockStructureWeightSampler(structureAccessor, chunk);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        
        Biome biome;
        
        for (int x = 0; x < 16; ++x) {
            int absX = x + startX;
            
            for (int z = 0; z < 16; ++z) {
                int absZ = z + startZ;
                
                for (int y = 0; y < this.worldHeight; ++y) {
                    Block blockToSet = blockArr[x][y][z];
                    
                    // Second check is a hack to stop weird chunk borders generating from surface blocks for ocean biomes
                    // being picked up and replacing topsoil blocks, somehow before biome reassignment.  Why?!
                    if ((biomeSource.isBeta() || biomeSource.isVanilla()) && OldGenUtil.getSolidHeight(chunk, this.worldHeight, absX, absZ) >= this.seaLevel - 4) {
                        biome = getBiomeForSurfaceGen(mutable.set(absX, 0, absZ), region, biomeSource);
                        
                        if (blockToSet == Blocks.GRASS_BLOCK) 
                            blockToSet = biome.getGenerationSettings().getSurfaceConfig().getTopMaterial().getBlock();
                        if (blockToSet == Blocks.DIRT) 
                            blockToSet = biome.getGenerationSettings().getSurfaceConfig().getUnderMaterial().getBlock();
                    }
                    
                    blockToSet = structureWeightSampler.getBlockWeight(absX, y, absZ, blockToSet);

                    if (blockToSet != Blocks.AIR) {
                        chunk.setBlockState(mutable.set(x, y, z), BlockStates.getBlockState(blockToSet), false);
                    }
                    
                    heightmapOCEAN.trackUpdate(x, y, z, BlockStates.getBlockState(blockToSet));
                    heightmapSURFACE.trackUpdate(x, y, z, BlockStates.getBlockState(blockToSet));
                        
                }
            }
        }
        
    }
  
    private void generateTerrain(int startX, int startZ, OldBiomeSource biomeSource) {
        Random rand = new Random();
      
        for (int relX = 0; relX < 16; ++relX) {
            int x = startX + relX;
            int rX = x / 1024;
            
            for (int relZ = 0; relZ < 16; ++relZ) {    
                int z = startZ + relZ;
                int rZ = z / 1024;
                
                float noiseA = (float)(
                    this.noiseOctavesA.sample(x / 0.03125f, 0.0, z / 0.03125f) - 
                    this.noiseOctavesB.sample(x / 0.015625f, 0.0, z / 0.015625f)) / 512.0f / 4.0f;
                float noiseB = (float)this.noiseOctavesE.sample(x / 4.0f, z / 4.0f);
                float noiseC = (float)this.noiseOctavesF.sample(x / 8.0f, z / 8.0f) / 8.0f;
                
                noiseB = noiseB > 0.0f ? 
                    ((float)(this.noiseOctavesC.sample(x * 0.25714284f * 2.0f, z * 0.25714284f * 2.0f) * noiseC / 4.0)) :
                    ((float)(this.noiseOctavesD.sample(x * 0.25714284f, z * 0.25714284f) * noiseC));
                    
                int heightVal = (int)(noiseA + this.seaLevel + noiseB);
                if ((float)this.noiseOctavesE.sample(x, z) < 0.0f) {
                    heightVal = heightVal / 2 << 1;
                    if ((float)this.noiseOctavesE.sample(x / 5, z / 5) < 0.0f) {
                        ++heightVal;
                    }
                }
                
                for (int y = 0; y < this.worldHeight; ++y) {
                    Block blockToSet = Blocks.AIR;
                    
                    if (this.generateInfdevWall && (x == 0 || z == 0) && y <= heightVal + 2) {
                        blockToSet = Blocks.OBSIDIAN;
                    }
                    else if (!biomeSource.isVanilla() && !biomeSource.isBeta() && y == heightVal + 1 && heightVal >= this.seaLevel && Math.random() < 0.02) {
                        blockToSet = Blocks.DANDELION;
                    }
                    else if (y == heightVal && heightVal >= this.seaLevel) {
                        blockToSet = Blocks.GRASS_BLOCK;
                    }
                    else if (y <= heightVal - 2) {
                        blockToSet = Blocks.STONE;
                    }
                    else if (y <= heightVal) {
                        blockToSet = Blocks.DIRT;
                    }
                    else if (y <= this.seaLevel) {
                        blockToSet = Blocks.WATER;
                    }
                    
                    if (this.generateInfdevPyramid) {
                        rand.setSeed(rX + rZ * 13871);
                        int bX = (rX << 10) + 128 + rand.nextInt(512);
                        int bZ = (rZ << 10) + 128 + rand.nextInt(512);
                        
                        bX = x - bX;
                        bZ = z - bZ;
                        
                        if (bX < 0) bX = -bX;
                        if (bZ < 0) bZ = -bZ;
                        
                        if (bZ > bX) bX = bZ;
                        if ((bX = 127 - bX) == 255) bX = 1;
                        if (bX < heightVal) bX = heightVal;
                        
                        if (y <= bX && (blockToSet == Blocks.AIR || blockToSet == Blocks.WATER))
                            blockToSet = Blocks.BRICKS;     
                    }
                    
                    if (y <= 0 + rand.nextInt(5)) {
                        blockToSet = Blocks.BEDROCK;
                    }
                              
                    
                    blockArr[relX][y][relZ] = blockToSet;
                }
                    
            }
        }
    }
    
    private int sampleHeightMap(int sampleX, int sampleZ) {
        
        int startX = (sampleX >> 4) << 4;
        int startZ = (sampleZ >> 4) << 4;
        
        int x = startX + Math.abs(sampleX) % 16;
        int z = startZ + Math.abs(sampleZ) % 16;
        
        float noiseA = (float)(
            this.noiseOctavesA.sample(x / 0.03125f, 0.0, z / 0.03125f) - 
            this.noiseOctavesB.sample(x / 0.015625f, 0.0, z / 0.015625f)) / 512.0f / 4.0f;
        float noiseB = (float)this.noiseOctavesE.sample(x / 4.0f, z / 4.0f);
        float noiseC = (float)this.noiseOctavesF.sample(x / 8.0f, z / 8.0f) / 8.0f;
        
        noiseB = noiseB > 0.0f ? 
            ((float)(this.noiseOctavesC.sample(x * 0.25714284f * 2.0f, z * 0.25714284f * 2.0f) * noiseC / 4.0)) :
            ((float)(this.noiseOctavesD.sample(x * 0.25714284f, z * 0.25714284f) * noiseC));
            
        int heightVal = (int)(noiseA + this.seaLevel + noiseB);
        if ((float)this.noiseOctavesE.sample(x, z) < 0.0f) {
            heightVal = heightVal / 2 << 1;
            if ((float)this.noiseOctavesE.sample(x / 5, z / 5) < 0.0f) {
                ++heightVal;
            }
        }
        
        return heightVal;
    }
}
