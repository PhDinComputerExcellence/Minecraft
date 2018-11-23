/***************************************************************
* file: Chunk.java
* author: No Preference
* class: CS 445 - Computer Graphics
*
* assignment: Check Point #3
* date last modified: 5/30/2018
*
* purpose: Gathers the information for all the blocks that will make up the chunk
* such as the block's position and corresponding texture and stores them within
* vertex buffer objects so that the blocks can be drawn with the render method
****************************************************************/ 
package finalprogram;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    
    static int CHUNK_SIZE;
    static final int CUBE_LENGTH = 2;
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startY, startZ;
    private Random r;
    int negFlat;
    double intensity;

    private int VBOTextureHandle;
    private Texture texture;
    
    private SimplexNoise noiseGen;

    // method: render
    // purpose: render chunk + mesh
    public void render(){
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            
            glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
            glColorPointer(3,GL_FLOAT, 0, 0L);
            
            ////////////////////////////////////////////////////////////////////
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            ////////////////////////////////////////////////////////////////////
            
            glDrawArrays(GL_QUADS, 0,CHUNK_SIZE *CHUNK_SIZE*CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    // method: rebuildMesh
    // purpose: re render chunk + mesh
    public void rebuildMesh(float startX, float startY, float startZ){
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        FloatBuffer vertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer vertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer vertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        
        for(float x = 0; x < CHUNK_SIZE; x++)
                for(float z = 0; z < CHUNK_SIZE; z++)
                        for(float y = 0; y< CHUNK_SIZE; y++){
                                if(blocks[(int)x][(int)y][(int)z].isActive()){
                                        vertexPositionData.put(createCube((float) (startX+ x * CUBE_LENGTH), (float)(y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)),(float) (startZ+ z * CUBE_LENGTH)));
                                        vertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int) x][(int) y][(int) z])));
                                        vertexTextureData.put(createTexCube((float) 0, (float) 0, blocks[(int)(x)][(int) (y)][(int) (z)]));
                                }
                        }
        
        vertexColorData.flip();
        vertexPositionData.flip();
        vertexTextureData.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    // method: createCubeVertexCol
    // purpose: return an array with contents of "CubeColorArray"
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors= new float[CubeColorArray.length* 4 * 6];
        
        for (int i = 0; i< cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i%CubeColorArray.length];
        }
        
        return cubeColors;
    }
    
    // method: createCube
    // purpose: return an array with cube vertex data
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset,y - offset, z,
        
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH, 
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH, 
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }
    
    // method: getCubeColor
    // purpose: returns {1,1,1} as default
    private float [] getCubeColor(Block block){
            return new float[]{1,1,1};
    }
    
    // method: createTexCube
    // purpose: return an array with correct texture mapping for block type
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f/16)/1024f;
        
            switch (block.getId()) {
                case 1: //SAND
                    return new float[] {
                        // BOTTOM QUAD(DOWN=+Y) //Sets texture for block using given png file
                        x + offset*3, y + offset*2,
                        x + offset*2, y + offset*2,
                        x + offset*2, y + offset*1,
                        x + offset*3, y + offset*1,
                        // TOP!
                        x + offset*3, y + offset*2,
                        x + offset*2, y + offset*2,
                        x + offset*2, y + offset*1,
                        x + offset*3, y + offset*1,
                        // FRONT QUAD
                        x + offset*2, y + offset*1,
                        x + offset*3, y + offset*1,
                        x + offset*3, y + offset*2,
                        x + offset*2, y + offset*2,
                        // BACK QUAD
                        x + offset*3, y + offset*2,
                        x + offset*2, y + offset*2,
                        x + offset*2, y + offset*1,
                        x + offset*3, y + offset*1,
                        // LEFT QUAD
                        x + offset*2, y + offset*1,
                        x + offset*3, y + offset*1,
                        x + offset*3, y + offset*2,
                        x + offset*2, y + offset*2,
                        // RIGHT QUAD
                        x + offset*2, y + offset*1,
                        x + offset*3, y + offset*1,
                        x + offset*3, y + offset*2,
                        x + offset*2, y + offset*2};
                    
                case 2: //WATER
                    return new float[] {
                        // BOTTOM QUAD(DOWN=+Y) //Sets texture for block using given png file
                        x + offset*0, y + offset*9,
                        x + offset*1, y + offset*9,
                        x + offset*1, y + offset*10,
                        x + offset*0, y + offset*10,
                        // TOP!
                        x + offset*1, y + offset*10,
                        x + offset*0, y + offset*10,
                        x + offset*0, y + offset*9,
                        x + offset*1, y + offset*9,
                        // FRONT QUAD
                        x + offset*0, y + offset*9,
                        x + offset*1, y + offset*9,
                        x + offset*1, y + offset*10,
                        x + offset*0, y + offset*10,
                        // BACK QUAD
                        x + offset*1, y + offset*10,
                        x + offset*0, y + offset*10,
                        x + offset*0, y + offset*9,
                        x + offset*1, y + offset*9,
                        // LEFT QUAD
                        x + offset*0, y + offset*9,
                        x + offset*1, y + offset*9,
                        x + offset*1, y + offset*10,
                        x + offset*0, y + offset*10,
                        // RIGHT QUAD
                        x + offset*0, y + offset*9,
                        x + offset*1, y + offset*9,
                        x + offset*1, y + offset*10,
                        x + offset*0, y + offset*10};
                    
                case 3: //DIRT
                    return new float[] {
                        // BOTTOM QUAD(DOWN=+Y) //Sets texture for block using given png file
                         x + offset*3, y + offset*1,
                         x + offset*2, y + offset*1,
                         x + offset*2, y + offset*0,
                         x + offset*3, y + offset*0,
                        // TOP!
                        x + offset*3, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*0,
                        x + offset*3, y + offset*0,
                        // FRONT QUAD
                        x + offset*3, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*0,
                        x + offset*3, y + offset*0,
                        // BACK QUAD
                        x + offset*3, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*0,
                        x + offset*3, y + offset*0,
                        // LEFT QUAD
                        x + offset*3, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*0,
                        x + offset*3, y + offset*0,
                        // RIGHT QUAD
                        x + offset*3, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*0,
                        x + offset*3, y + offset*0};
                    
                case 4: //STONE
                    return new float[] {
                        // BOTTOM QUAD(DOWN=+Y) //Sets texture for block using given png file
                        x + offset*2, y + offset*1,
                        x + offset*1, y + offset*1,
                        x + offset*1, y + offset*0,
                        x + offset*2, y + offset*0,
                        // TOP!
                        x + offset*2, y + offset*1,
                        x + offset*1, y + offset*1,
                        x + offset*1, y + offset*0,
                        x + offset*2, y + offset*0,
                        // FRONT QUAD
                        x + offset*1, y + offset*0,
                        x + offset*2, y + offset*0,
                        x + offset*2, y + offset*1,
                        x + offset*1, y + offset*1,
                        // BACK QUAD
                        x + offset*2, y + offset*1,
                        x + offset*1, y + offset*1,
                        x + offset*1, y + offset*0,
                        x + offset*2, y + offset*0,
                        // LEFT QUAD
                        x + offset*1, y + offset*0,
                        x + offset*2, y + offset*0,
                        x + offset*2, y + offset*1,
                        x + offset*1, y + offset*1,
                        // RIGHT QUAD
                        x + offset*1, y + offset*0,
                        x + offset*2, y + offset*0,
                        x + offset*2, y + offset*1,
                        x + offset*1, y + offset*1};
                    
                case 5: //BEDROCK
                    return new float[] {
                        // BOTTOM QUAD(DOWN=+Y) //Sets texture for block using given png file
                        x + offset*2, y + offset*2,
                        x + offset*1, y + offset*2,
                        x + offset*1, y + offset*1,
                        x + offset*2, y + offset*1,
                        // TOP!
                        x + offset*2, y + offset*2,
                        x + offset*1, y + offset*2,
                        x + offset*1, y + offset*1,
                        x + offset*2, y + offset*1,
                        // FRONT QUAD
                        x + offset*1, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*2,
                        x + offset*1, y + offset*2,
                        // BACK QUAD
                        x + offset*2, y + offset*2,
                        x + offset*1, y + offset*2,
                        x + offset*1, y + offset*1,
                        x + offset*2, y + offset*1,
                        // LEFT QUAD
                        x + offset*1, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*2,
                        x + offset*1, y + offset*2,
                        // RIGHT QUAD
                        x + offset*1, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*2,
                        x + offset*1, y + offset*2};

                default: //GRASS
                    return new float[] {
                        // BOTTOM QUAD(DOWN=+Y) //Sets texture for block using given png file
                        x + offset*3, y + offset*10,
                        x + offset*2, y + offset*10,
                        x + offset*2, y + offset*9,
                        x + offset*3, y + offset*9,
                        // TOP!
                        x + offset*3, y + offset*1,
                        x + offset*2, y + offset*1,
                        x + offset*2, y + offset*0,
                        x + offset*3, y + offset*0,
                        // FRONT QUAD
                        x + offset*3, y + offset*0,
                        x + offset*4, y + offset*0,
                        x + offset*4, y + offset*1,
                        x + offset*3, y + offset*1,
                        // BACK QUAD
                        x + offset*4, y + offset*1,
                        x + offset*3, y + offset*1,
                        x + offset*3, y + offset*0,
                        x + offset*4, y + offset*0,
                        // LEFT QUAD
                        x + offset*3, y + offset*0,
                        x + offset*4, y + offset*0,
                        x + offset*4, y + offset*1,
                        x + offset*3, y + offset*1,
                        // RIGHT QUAD
                        x + offset*3, y + offset*0,
                        x + offset*4, y + offset*0,
                        x + offset*4, y + offset*1,
                        x + offset*3, y + offset*1};
            }
    }
    
    // method: Chunk
    // purpose: constructor
    //        : creates chunk at startX, startY, startZ 
    //        : using simplexNoise randomly generate properties of chunk
    public Chunk(int startX, int startY, int startZ, int chunksize, int neg, double towers){
            try{
                    texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
            }catch(Exception e){
                    System.out.println("terrain.png not found");
            }

            CHUNK_SIZE = chunksize;
            negFlat = neg;
            intensity = towers;
            r = new Random();
            noiseGen = new SimplexNoise(negFlat, intensity, r.nextInt());
            blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
            
            blockRandomizer(blocks);
     
            VBOColorHandle = glGenBuffers();
            VBOVertexHandle = glGenBuffers();
            VBOVertexHandle = glGenBuffers();
            
            this.startX = startX;
            this.startY = startY;
            this.startZ = startZ;
            
            rebuildMesh(startX, startY, startZ);
    }    
    
    private void blockRandomizer(Block[][][] blocks)
    {
    	for (int x = 0; x < CHUNK_SIZE; x++)
            for (int z = 0; z < CHUNK_SIZE; z++)
                for (int y = 0; y < CHUNK_SIZE; y++)
                    blocks[x][y][z] = randomBlock(y, noiseGen.getNoise(x, z)*10);
    }
    
    public void changeNoise() {
    	System.out.println("Kappa");
    	r = new Random();
    	noiseGen = new SimplexNoise(negFlat, intensity, r.nextInt());
    }
    

    private Block randomBlock(int y, double f)
    {
        //Returning variable
        Block b = null;

        //Bottom layer = "Bedrock"
        if(y == 0) b = new Block(Block.BlockType.BlockType_Bedrock);

        //Middle body = "Dirt" | "Stone" 
        else if(y < 24+f)
        {
            int randomNum = r.nextInt(2);
            if(randomNum == 0)
                b = new Block(Block.BlockType.BlockType_Dirt);
            else
                b = new Block(Block.BlockType.BlockType_Stone);
        }

        //Top layer = "Grass" | "Sand" | "Water"
        else if(y >= 29)
        {
//            int randomNum = r.nextInt(3);
            if(y >=29)
                b = new Block(Block.BlockType.BlockType_Grass);
            else if(y > 25)
                b = new Block(Block.BlockType.BlockType_Sand);
            else
            {
                    b = new Block(Block.BlockType.BlockType_Water);
            }
        } else if (y >= 28) {
        	b = new Block(Block.BlockType.BlockType_Sand);
        } else {
        	b = new Block(Block.BlockType.BlockType_Water);
        }

        //Set rest as false
        if(y >= 30+f)
        {
                b.setActive(false);
        }

        return b;
    }
}