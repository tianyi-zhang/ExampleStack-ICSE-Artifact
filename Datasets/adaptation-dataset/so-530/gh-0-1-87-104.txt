package com.BombingGames.Game;

import com.BombingGames.Game.Gameobjects.AbstractEntity;
import com.BombingGames.Game.Gameobjects.Block;
import com.BombingGames.MainMenu.MainMenuState;
import java.util.ArrayList;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

/**
 *A map stores nine chunks as part of a bigger map.
 * @author Benedikt Vogler
 */
public class Map {
    /**
     * The gravity constant in m/s^2
     */
    public static final float GRAVITY = 9.81f;
    
    private static int blocksX, blocksY, blocksZ;    
    private Block data[][][];
    private ArrayList<AbstractEntity> entitylist = new ArrayList<AbstractEntity>();
        
    //A list which has all current nine chunk coordinates in it.
    private int[][] coordlist = new int[9][2];
    private boolean ENABLECHUNKSWITCH = true;
   
    
      
    /**
     * Creates a map.
     * @param load Should the map be generated or loaded from disk?
     */
    public Map(boolean load) {
        Log.debug("Creating the map...");
        Log.debug("Should the Engine load a map: "+load);
        if (load) Chunk.readMapInfo();
        //save chunk size, which are now loaded
        blocksX = Chunk.getBlocksX()*3;
        blocksY = Chunk.getBlocksY()*3;
        blocksZ = Chunk.getBlocksZ();
        data = new Block[blocksX][blocksY][blocksZ];
        
        //Fill the nine chunks
        Chunk tempchunk;
        int pos = 0;
        
        for (int y=-1; y < 2; y++)
            for (int x=-1; x < 2; x++){
                coordlist[pos][0] = x;
                coordlist[pos][1] = y;
                tempchunk = new Chunk(x, y, load);
                setChunk(pos, tempchunk);
                pos++;               
            }
        

       
        Log.debug("...Finished creating the map");
    }
    
     /**
     * Returns the amount of Blocks inside the map in x-direction.
     * @return
     */
    public static int getBlocksX() {
        return blocksX;
    }

    /**
     * Returns the amount of Blocks inside the map in y-direction.
     * @return
     */
    public static int getBlocksY() {
        return blocksY;
    }

    /**
     * Returns the amount of Blocks inside the map in z-direction.
     * @return 
     */
    public static int getBlocksZ() {
        return blocksZ;
    }
    
    
    /**
     * Copies an array with three dimensions. Code by Kevin Brock from http://stackoverflow.com/questions/2068370/efficient-system-arraycopy-on-multidimensional-arrays
     * @param array
     * @return The copy of the array-
     */
    private Block[][][] copyOf3Dim(Block[][][] array) {
        Block[][][] copy;
        copy = new Block[array.length][][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = new Block[array[i].length][];
            for (int j = 0; j < array[i].length; j++) {
                copy[i][j] = new Block[array[i][j].length];
                System.arraycopy(array[i][j], 0, copy[i][j], 0, 
                    array[i][j].length);
            }
        }
        return copy;
    } 
    
    /**
     * Get the data of the map
     * @return
     */
    public Block[][][] getData() {
        return data;
    }
    
    /**
     * Reorgnanises the map and sets the new middle chunk to param newmiddle.
     * Move all chunks when loading or creating a new piece of the map
     *    |0|1|2|
     *     -------------
     *    |3|4|5|
     *     -------------
     *    |6|7|8|
     * @param newmiddle newmiddle is 1, 3, 5 or 7
     */
    public void setCenter(int newmiddle){
        if (ENABLECHUNKSWITCH){
            Log.debug("ChunkSwitch:"+newmiddle);
            if (newmiddle==1 || newmiddle==3 || newmiddle==5 || newmiddle==7) {

            //make a copy of the data
            Block data_copy[][][] = copyOf3Dim(data);

            for (int pos=0; pos<9; pos++){
                //refresh coordinates
                coordlist[pos][0] += (newmiddle == 3 ? -1 : (newmiddle == 5 ? 1 : 0));
                coordlist[pos][1] += (newmiddle == 1 ? -1 : (newmiddle == 7 ? 1 : 0));

                if (isMovingChunkPossible(pos, newmiddle)){
                    setChunk(pos, getChunk(data_copy, pos - 4 + newmiddle));
                } else {

                    setChunk(
                            pos,
                            new Chunk(
                                coordlist[pos][0],
                                coordlist[pos][1],
                                MainMenuState.shouldLoadMap()
                            )
                    );

                }
            }

            Controller.requestRecalc();
            } else {
                Log.error("setCenter was called with center:"+newmiddle);
            }
        }
    }
    
    /**
     * checks if the number can be reached by moving the net in a newmiddle
     * @param pos the position you want to check
     * @param newmiddle the newmiddle the chunkswitch is made to
     * @return 
     */
     private boolean isMovingChunkPossible(int pos, int newmiddle){
        boolean result = true; 
        switch (newmiddle){
            case 1: if ((pos==0) || (pos==1) || (pos==2)) result = false;
            break;
            
            case 3: if ((pos==0) || (pos==3) || (pos==6)) result = false;
            break;  
                
            case 5: if ((pos==2) || (pos==5) || (pos==8)) result = false;
            break;
                
            case 7: if ((pos==6) || (pos==7) || (pos==8)) result = false;
            break;
        } 
        return result;
    }
     
    /**
     * Get a chunk out of a map (should be a copy of Map.data)
     * @param src The map
     * @param pos The chunk number
     */ 
    private Chunk getChunk(Block[][][] src, int pos) {
        Chunk tmpChunk = new Chunk();
        //copy the data in two loops and arraycopy
        for (int x = Chunk.getBlocksX()*(pos % 3);
                x < Chunk.getBlocksX()*(pos % 3+1);
                x++
            )
                for (int y = Chunk.getBlocksY()*Math.abs(pos / 3);
                        y < Chunk.getBlocksY()*Math.abs(pos / 3+1);
                        y++
                    ) {
                    System.arraycopy(
                        src[x][y],                
                        0,
                        tmpChunk.getData()[x-Chunk.getBlocksX()*(pos % 3)][y - Chunk.getBlocksY()*(pos / 3)],
                        0,
                        Chunk.getBlocksZ()
                    );
                }
        return tmpChunk;
    }

    /**
     * Inserts a chunk in the map.
     * @param pos The position in the grid
     * @param newchunk The chunk you want to insert
     */
    private void setChunk(int pos, Chunk newchunk) {
        for (int x=0;x < Chunk.getBlocksX(); x++)
            for (int y=0;y < Chunk.getBlocksY();y++) {
                System.arraycopy(
                    newchunk.getData()[x][y],
                    0,
                    data[x+ Chunk.getBlocksX()*(pos%3)][y+ Chunk.getBlocksY()*Math.abs(pos/3)],
                    0,
                    Chunk.getBlocksZ()
                );
            }
    }
    
    
    /**
     * Draws the map
     * @param camera 
     */
    public void render(Graphics g, Camera camera) {
        //if (Gameplay.getController().hasGoodGraphics()) Block.getSpritesheet().bind();
        //if (Gameplay.getView().hasGoodGraphics()) GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD);
        
        Block.getSpritesheet().getFullImage().startUse();
        //render vom bottom to top
        for (int i=0; i < camera.depthsortlistSize() ;i++) {
            int[] coords = camera.getDepthsortCoord(i);//get the coords of the current renderobject
            int entitynumber = camera.getEntityIndex(i); //get the entityindex to check if it is an entity
            
            if (entitynumber == -1) //if a block then  get it and draw it
                data[coords[0]][coords[1]][coords[2]].render(g, coords);
            else //if it's an entity get it and draw it
                entitylist.get(entitynumber).render(g);        
        }
            
       Block.getSpritesheet().getFullImage().endUse(); 
      // if (Gameplay.getView().hasGoodGraphics()) GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
    }

   /**
     *Get the coordinates of a chunk. 
     * @param pos the position of the chunk
     * @return the coordinates of the chunk
     */
    public int[] getChunkCoords(int pos) {
        return coordlist[pos];
    }
    
    /**
     * Returns a renderobject of the map.
     * @param x If too high or too low, it takes the highest/deepest value possible
     * @param y If too high or too low, it takes the highest/deepest value possible
     * @param z If too high or too low, it takes the highest/deepest value possible
     * @return A single renderobject at the wanted coordinates.
     * @see com.BombingGames.Game.Map#getData(int, int, int) 
     */
    public Block getDataSafe(int x, int y, int z){
        if (x >= blocksX){
            x = blocksX-1;
        } else if( x<0 ){
            x = 0;
        }
        
        if (y >= blocksY){
            y = blocksY-1;
        } else if( y < 0 ){
            y = 0;
        }
        
        if (z >= blocksZ){
            z = blocksZ-1;
        } else if( z < 0 ){
            z = 0;
        }
        
        return data[x][y][z];    
    }
    
    /**
     * 
     * @param coords
     * @return
     */
    public Block getDataSafe(int[] coords) {
        return getDataSafe(coords[0], coords[1], coords[2]);
    }
    
    /**
     * Returns  a Block without checking the parameters first. Good for debugging and also faster.
     * @param x position
     * @param y position
     * @param z position
     * @return the single renderobject you wanted
     */
    public Block getData(int x, int y, int z){
        return data[x][y][z];  
    }
    
    /**
     * Set a renderobject at a specific coordinate
     * @param x position
     * @param y position
     * @param z position
     * @param block  
     */
    public void setData(int x, int y, int z, Block block){
        data[x][y][z] = block;
    }
    
    /**
     * 
     * @param coords
     * @param block
     */
    public void setData(int[] coords, Block block) {
        data[coords[0]][coords[1]][coords[2]] = block;
    }
    
    /**
     * Set a renderobject at a specific coordinate
     * @param x position
     * @param y position
     * @param z position
     * @param block 
     * @deprecated 
     * @see com.BombingGames.Game.Map#setData(int x, int y, int z, Block renderobject)
     */
    public void setDataSafe(int x, int y, int z, Block block){
        if (x >= blocksX){
            x = blocksX-1;
        } else if( x<0 ){
            x = 0;
        }
        
        if (y >= blocksY){
            y = blocksY-1;
        } else if( y < 0 ){
            y = 0;
        }
        
        if (z >= blocksZ){
            z = blocksZ-1;
        } else if( z < 0 ){
            z = 0;
        }
        
        data[x][y][z] = block;
    }
    
    /**
     * 
     * @param coords
     * @param block
     */
    public void setDataSafe(int[] coords, Block block) {
        if (coords[0] >= blocksX){
            coords[0] = blocksX-1;
        } else if( coords[0]<0 ){
            coords[0] = 0;
        }
        
        if (coords[1] >= blocksY){
            coords[1] = blocksY-1;
        } else if( coords[1] < 0 ){
            coords[1] = 0;
        }
        
        if (coords[2] >= blocksZ){
            coords[2] = blocksZ-1;
        } else if( coords[2] < 0 ){
            coords[2] = 0;
        }
        
        data[coords[0]][coords[1]][coords[2]] = block;
    }
    
    
    /**
     * a method who gives random blocks offset
     * @param numberofblocks the amount of moved blocks
     */
    public void earthquake(int numberofblocks){
        int[] x = new int[numberofblocks];
        int[] y = new int[numberofblocks];
        int[] z = new int[numberofblocks];
        
        //pick random blocks 
        for (int i=0;i<numberofblocks;i++){
            x[i] = (int) (Math.random()*blocksX-1);
            y[i] = (int) (Math.random()*blocksY-1);
            z[i] = (int) (Math.random()*blocksZ-1);
        }
        
        for (int i=0;i < numberofblocks; i++){
            float[] pos = {
                (float) (Math.random()*Block.DIM2),
                (float) (Math.random()*Block.DIM2),
                (float) (Math.random()*Block.GAMEDIMENSION)
            };
            data[x[i]][y[i]][z[i]].setPos(pos);
        }
        Controller.requestRecalc();
    }
    
    /**
     * Calculates the light level based on the sun shining straight from the top
     */
    public void calc_light(){
        for (int x=0; x < blocksX; x++){
            for (int y=0; y < blocksY; y++) {
                
                //find top most renderobject
                int topmost = Chunk.getBlocksZ()-1;
                while (data[x][y][topmost].isTransparent() == true && topmost > 0 ){
                    topmost--;
                }
                
                if (topmost>0) {
                    //start at topmost renderobject and go down. Every step make it a bit darker
                    for (int level = topmost; level > -1; level--){
                        data[x][y][level].setLightlevel((50 * level) / topmost);
                    }
                }
            }
        }         
    }
    
    /**
     * Returns the entitylist
     * @return
     */
    public ArrayList<AbstractEntity> getEntitylist() {
        return entitylist;
    }
    
    /**
     * Transforms absolute coordinates into realtive coordinates.
     * @param absCoords the absolute absCoords
     * @return  the relative absCoords
     */
    public int[] absToRelCoords(int[] absCoords){
        int[] tmp = absCoords.clone();
        tmp[0] -= getChunkCoords(0)[0] * Chunk.getBlocksX();
        tmp[1] -= getChunkCoords(0)[1] * Chunk.getBlocksY();        
        return tmp;
    }
    
     /**
     * Transforms absolute coordinates into relative coordinates. This only affects index 0 and 1.
     * @param relCoords the relative coords
     * @return the absolute coords
     */
    public int[] relToAbsCoords(int[] relCoords){
        int[] tmp = relCoords.clone();
        tmp[0] += getChunkCoords(0)[0] * Chunk.getBlocksX();
        tmp[1] += getChunkCoords(0)[1] * Chunk.getBlocksY();
        return tmp;

    }
}