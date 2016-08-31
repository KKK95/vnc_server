package vnc_mouse;

import java.awt.AWTException;				//awt class 的警告, 表示awt 出現異常
import java.awt.Robot;						//robot 用來控制滑鼠和鍵盤
import java.awt.event.InputEvent;			//InputEvent.getModifiersEx() 會獲得所有按鍵和修改鍵的狀態
import java.awt.event.KeyEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
 
public class Mouse {
	
	private static Robot robot;								//robot 用來控制滑鼠和鍵盤
	
	public Mouse()
	{
		boolean leftpressed=false;
		boolean rightpressed=false;
		try
		{
			robot = new Robot();
		}catch (AWTException e) {
			System.out.println("Error in creating robot instance");
			System.exit(-1);
		}
	}
	
	public static void mouse_control
	(Socket client, String line) 
	{
//			System.out.println(line); //print whatever we get from client
			
			//if user clicks on next
			if(line.equalsIgnoreCase("next"))
			{
				//Simulate press and release of key 'n'
				robot.keyPress(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_N);
			}
			//if user clicks on previous
			else if(line.equalsIgnoreCase("previous"))
			{
				//Simulate press and release of key 'p'
				robot.keyPress(KeyEvent.VK_P);
				robot.keyRelease(KeyEvent.VK_P);		        	
			}
			//if user clicks on play/pause
			else if(line.equalsIgnoreCase("play"))
			{
				//Simulate press and release of spacebar
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
			}
			//input will come in x,y format if user moves mouse on mousepad
			else if(line.contains(","))
			{
				float movex=Float.parseFloat(line.split(",")[0]);//extract movement in x direction
				float movey=Float.parseFloat(line.split(",")[1]);//extract movement in y direction
				Point point = MouseInfo.getPointerInfo().getLocation(); //Get current mouse position
				float nowx=point.x;
				float nowy=point.y;
				robot.mouseMove((int)(nowx+movex),(int)(nowy+movey));//Move mouse pointer to new location
			}
			//if user taps on mousepad to simulate a left click
			else if(line.contains("left_click"))
			{
				//Simulate press and release of mouse button 1(makes sure correct button is pressed based on user's dexterity)
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			}
			//Exit if user ends the connection
	}
}
