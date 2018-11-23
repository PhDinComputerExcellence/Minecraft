/***************************************************************
* file: Basic.java
* author: No Preference
* class: CS 445 - Computer Graphics
*
* assignment: Check Point #3
* date last modified: 5/30/2018
*
* purpose: The program draws a centered 640x480 window that displays a randomly
* generated terrain with up to 7 different textured block types. The program allows
* the user to navigate the environment using certain keyboard keys and by using
* the mouse
****************************************************************/ 
package finalprogram;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

public class Basic implements KeyListener{
    
    private FPCameraController fp;
    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
   
    // method: start
    // purpose: prints instructions, creates window, and initializes OpenGL
    public void start(){
        try{
            printControls();
            createWindow();
            initGL();
            
            //If I don't initialize "fp" here entire program crashes
            fp = new FPCameraController(0f, 0f, 0f);
            fp.gameLoop();
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    // method: createWindow
    // purpose: creates a 640x480 non full screen black background window
    private void createWindow() throws Exception{        
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        
        for (int i= 0; i< d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }

        Display.setDisplayMode(displayMode); 
        Display.setTitle("Check Point #3");
        Display.create();
    }
    
    // method: initGL
    // purpose: initialize OpenGL and set all the settings
    private void initGL(){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)
        displayMode.getHeight(), 0.1f, 300.0f);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light's position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight); //sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight); //sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight); //sets our ambient light
        
        glEnable(GL_LIGHTING); //enables our lighting
        glEnable(GL_LIGHT0); //enables light0
    }
	
    // method: initLightArrays
    // purpose: initialize light arrays to be used with OpenGL
    private void initLightArrays(){
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
    }

	
    // method: printControls
    // purpose: print the controls
    private void printControls(){
        System.out.println("W - move forward");
        System.out.println("S - move backward");
        System.out.println("A - strafe left");
        System.out.println("D - strafe right");
        System.out.println("Space - move up");
        System.out.println("Left Shift - move down");
        System.out.println("Escape - Quit");
        System.out.println("O - Decrease Size");
        System.out.println("P - Increase Size");
        System.out.println("R - Reset");
        System.out.println("L - Less Noise");
        System.out.println("K - More Noise");
        System.out.println("N - Less Rocky");
        System.out.println("M - Can't Stop the Rock");
    }

    // method: main
    // purpose: run "start" method
    public static void main(String[] args){
        Basic basic = new Basic();
        basic.start();
    }

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			fp.changeChunk();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			fp.changeChunk();
		}
		
	}
}