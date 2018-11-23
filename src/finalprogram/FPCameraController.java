/***************************************************************
* file: FPCameraController.java
* author: No Preference
* class: CS 445 - Computer Graphics
*
* assignment: Check Point #3
* date last modified: 5/30/2018
*
* purpose: Creates a camera that will be used to navigate the randomly generated
* 30x30 terrain
****************************************************************/ 
package finalprogram;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

public class FPCameraController{
    //3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lPosition = null;
    
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;
    private int chunksize = 40;
    private int negintensity = 40;
    private double towers = 0.3;
    private Chunk chunk = new Chunk(0, 0, 0, chunksize, negintensity, towers);

    
    // Constructor: FPCameraController
    // purpose: intializes variables 
    public FPCameraController(float x, float y, float z){
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
        lPosition= new Vector3f(x,y,z);
        lPosition.x= 0f;
        lPosition.y= 15f;
        lPosition.z= 0f;
    }

    // method: yaw
    // purpose: increment the camera's current yaw rotation
    public void yaw(float amount)
    {
        //increment the yaw by the amount param
        yaw += amount;
    }

    // method: pitch
    // purpose: increment the camera's current yaw rotation
    public void pitch(float amount)
    {
        //increment the pitch by the amount param
        pitch -= amount;
    }

    // method: walkForward
    // purpose: 
    //moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance)
    {
        float xOffset= distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset= distance * (float)Math.cos(Math.toRadians(yaw));
        position.x-= xOffset;
        position.z+= zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: walkBackwards
    // purpose: 
    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance)
    {
        float xOffset= distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset= distance * (float)Math.cos(Math.toRadians(yaw));
        position.x+= xOffset;
        position.z-= zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x+=xOffset).put(lPosition.y).put(lPosition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: strafeLeft
    // purpose: 
    //strafes the camera left relative to its current rotation (yaw)
    public void strafeLeft(float distance)
    {
        float xOffset= distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset= distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x-= xOffset;
        position.z+= zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: strafeRight
    // purpose: 
    //strafes the camera right relative to its current rotation (yaw)
    public void strafeRight(float distance)
    {
        float xOffset= distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset= distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x-= xOffset;
        position.z+= zOffset;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: moveUp
    // purpose: 
    //moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance)
    {
        position.y-= distance;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: moveDown
    // purpose: 
    //moves the camera down
    public void moveDown(float distance)
    {
        position.y+= distance;
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: lookThrough
    // purpose: 
    //translates and rotate the matrix so that it looks through the camera
    public void lookThrough()
    {
        //roatatethe pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatatethe yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    // method: gameLoop
    // purpose: Initializes and updates the camera as new input arrives from the user
    public void gameLoop()
    {
        FPCameraController camera = new FPCameraController(0, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //length of frame
        float lastTime= 0.0f; // when the last frame was
        long time = 0;
        float mouseSensitivity= 0.09f;
        float movementSpeed= 2f;
        //hide the mouse
        Mouse.setGrabbed(true);

        // keep looping till the display window is closed the ESC key is down
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            time = Sys.getTime();
            lastTime= time;
            if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            	this.changeChunk();
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
            	chunksize++;
            	this.changeChunk();
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            	if (chunksize > 40) {
            		chunksize--;
            		this.changeChunk();
            	}
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_L)) {

            		negintensity+=10;
            		this.changeChunk();
            	
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            		if (negintensity >10) {
            		negintensity-=10;
            		this.changeChunk();
            		}
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            	
            		towers+=0.1;
            		this.changeChunk();
            	
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
            		if (towers > 0.1) {
            		towers-=0.1;
            		this.changeChunk();
            		}
            }
            //distance in mouse movement //from the last getDX() call.
            dx = Mouse.getDX();
            
            //distance in mouse movement //from the last getDY() call.
            dy = Mouse.getDY();

            //controllcamera yaw from x movement fromtthe mouse
            camera.yaw(dx * mouseSensitivity);
            
            //controllcamera pitch from y movement fromtthe mouse
            camera.pitch(dy* mouseSensitivity);

            //when passing in the distance to move
            //we times the movementSpeedwith dtthis is a time scale
            //so if its a slow frame u move more then a fast frame
            //so on a slow computer you move just as fast as on a fast computer
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))//move forward
            {
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))//move backwards
            {
                camera.walkBackwards(movementSpeed);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))//strafe left 
            {
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))//strafe right
            {
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))//move up 
            {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) //move down
            {
                camera.moveDown(movementSpeed);
            }

            //set the model view matrix back to the identity
            glLoadIdentity();
            
            //look through the camera before you draw anything
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            //you would draw your scene here.
            chunk.render();
            
            //draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }

        Display.destroy(); //Exit window when user clicks x or presses ESC key
    }

    // method: render
    // purpose: Creates all the six different colored sides of the cube
    private void render() {
        try{
            glBegin(GL_QUADS);
            //Top
            glColor3f(1.0f,0.0f,0.0f); //red
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);

            //Bottom
            glColor3f(1,0.3f,0.0f); //orange
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);

            //Front
            glColor3f(255f,255f,255f); //white
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);

            //Back
            glColor3f(1f,1.0f,0.0f); //yellow
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);

            //Left
            glColor3f(0.0f,0.0f,1.0f); //blue
            glVertex3f(-1.0f, 1.0f,1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);

            //Right
            glColor3f(0.0f,0.5f,0.0f); //green
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();

        }catch(Exception e){
        }
    }
    
    public void changeChunk() {
    	chunk = new Chunk(0, 0, 0, chunksize, negintensity, towers);
    }
}