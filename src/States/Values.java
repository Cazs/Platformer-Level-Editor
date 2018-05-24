package States;
import java.awt.Color;

public class Values 
{
	public static boolean shoot					= false;
	public static int dir_x						= 0;
	public static int dir_y						= 0;
	public static boolean canvas_focused		= true;
	public static int STEP 						= 10;
	public static int SCR_WIDTH 				= 1000;
	public static int SCR_HEIGHT 				= 700;
	public static int x 						= 10;
	public static int y 						= 10;
	public static boolean erase			 		= false;
	public static int CURSOR_SIZE				= 10;
	public static double SCALE					= 1;
	public static int GAP						= 0;
	public static int GRAVITY					= 11;//the higher the gravity the slower the object will fall
	public static boolean colour				= false;
	public static int TOP_H						= 100;
	public static int LEFT_W					= 200;
	public static Color block_colour			= Color.DARK_GRAY.brighter();
	public static Color bg_colour				= Color.BLACK;
	public static Color hl_colour				= Color.MAGENTA;
	public static Color curs_colour				= Color.GREEN;
	public static int pan						= 5;
	public static boolean physics				= false;
	public static int PROP_W					= 200;
	public static int ACTOR_SPEED				= 10;
	public static boolean run					= false;
	public static String direction				= "RIGHT";
	public static final double MAX_SPEED		= 8;
	public static int canvas_width				= 0;
	public static int canvas_height				= 0;
	public static int ACTOR_SIZE				= 15;
	public static final double MAX_JUMP			= 120;  
	public static boolean jump					= false;
	public static double JUMP_INC				= 3.0;
	public static boolean show_coll_bounds		= false;
	public static final int BULLT_SPD			= 2;
	public static final int BULLET_W			= 2;
	public static final int BULLET_H			= 2;
	public static final double BULLET_WEIGHT	= 10;
	public static  int zoomWidth				= 0;
	public static  int zoomHeight				= 0;
	public static boolean FRONT_END				= false;
	public static long MAX_TIME					= 5000*60;
	public static boolean showlobby				= true;
	public static boolean showgameover			= false;
	public static boolean showcanvas			= false;
	public static boolean updated				= false;
	
}
