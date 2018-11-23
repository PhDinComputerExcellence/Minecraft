/***************************************************************
* file: Basic.java
* author: No Preference
* class: CS 445 - Computer Graphics
*
* assignment: Check Point #3
* date last modified: 5/30/2018
*
* purpose: Used to store required information for the blocks that will construct 
* our terrain such as its type, active status, and position within the world
****************************************************************/ 
package finalprogram;

public class Block {
    
    private boolean isActive;
    private BlockType type;
    private float x,y,z;
    
    public enum BlockType{

        
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);

        private int blockId;
        
        BlockType(int i) {
            blockId = i;
        }
        
        public int getId(){
            return blockId;
        }
        
        public void setId(int i){
            blockId = i;
        }
    }
    
    // method: Block
    // purpose: constructor
    public Block(BlockType type){
            this.type = type;
            isActive = true;
    }
    
    // method: setCoords
    // purpose: set block coordinates
    public void setCoords(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
    }
    
    // method: isActive
    // purpose: returns status if the block is "active"
    public boolean isActive(){
            return isActive;
    }
    
    // method: setActive
    // purpose: sets the block to "active"
    public void setActive(boolean active){
            isActive = active;
    }
    
    // method: getId
    // purpose: return block's ID
    public int getId(){
            return type.getId();
    }
}