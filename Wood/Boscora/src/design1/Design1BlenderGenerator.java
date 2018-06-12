/**
 * 
 */
package design1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Fede
 *
 */
public class Design1BlenderGenerator {

	public static final Boolean USE_BLENDER_GPU = false;
	
	public static final String ROOT_PATH = "E:/fede/WoodArt/BlenderJava/";
	public static final String BASIC_PYTHON_PATH = "E:/fede/WoodArt/Blender/1/base.py";
	public static final String IMAGES_OUTPUT_PATH = "E:/fede/WoodArt/Blender/1/output/";
	
	public static final String BLENDER_RUNNABLE_PATH = "E:/SteamSSDFolder/SteamApps/common/Blender/blender.exe";
	//public static final String BLENDER_RUNNABLE_PATH = "E:/fede/WoodArt/Blender/blender-2.78c/blender.exe";
	public static final String BLENDER_RUNNABLE_PATH_CONFIG = "E:/fede/WoodArt/Blender/blender-2.78c/openCLSet.bat";
	
	private static final Float LINES_DISTANCE = 0.5f;	// mm
	
	private static Integer objCounter = 0;
	
	public static void generateRender(DesignerProperties designer, List<Line> lowLines, List<Line> highLines, Long seed) {
		String pythonFile = getFileText(BASIC_PYTHON_PATH) + System.getProperty("line.separator");
		
		pythonFile += configCpuGpu();
		pythonFile += updatePositions(designer, lowLines, highLines);
		pythonFile += updateBase(designer, lowLines, highLines);
		
		//pythonFile += "bpy.ops.wm.save_as_mainfile(filepath='E:/fede/WoodArt/Blender/1/Basic2.blend')" + System.getProperty("line.separator");

		pythonFile += setRenderSamples(600);		
		pythonFile += setRenderResolution(316, 316);
		pythonFile += renderImage(-0.222166f, -1.04651f, 0.340473f, 1.570796f, 0f, -0.420909f, null, 100f, "thumbs", seed.toString());
		
		//pythonFile += setRenderSamples(600);	
		//pythonFile += setRenderResolution(700, 700);
		//pythonFile += renderImage(-0.307913f, -1.034f, 0.34047f, 1.570796f, 0f, -0.488692f, null, 100f, "thumbs", seed.toString());
		
		pythonFile += setRenderSamples(400);
		pythonFile += setRenderResolution(960, 720);
		pythonFile += renderImage(0.340894f, -1.40777f, 0.340473f, 1.570796f, 0f, 0f, null, 100f, seed.toString(), "1");
		pythonFile += renderImage(-0.324698f, -1.38534f, 0.351688f, 1.570796f, 0f, -0.420909f, null, 100f, seed.toString(), "2");
		pythonFile += renderImage(-0.397961f, -0.523892f, 0.318069f, 1.570796f, 0f, -0.872665f, "Cube.021", 2.5f, seed.toString(), "3");
		pythonFile += renderImage(-0.369574f, -0.500073f, 0.650284f, 1.221730f, 0f, -0.872665f, "Cube.021", 3.5f, seed.toString(), "4");
		pythonFile += renderImage(-0.361445f, -0.453754f, 0.134214f, 1.8f, 0f, -0.874068f, "Cube.021", 3.5f, seed.toString(), "5");
		
		saveFileTo(pythonFile, ROOT_PATH + "javaGenerated.py");
		runBlender();
	}
	
	public static String updateBase(DesignerProperties designer, List<Line> lowLines, List<Line> highLines) {
		String retval = "";
		Float height = (lowLines.get(0).length + highLines.get(0).length)/1000f;
		Float width = lowLines.size()*(LINES_DISTANCE + designer.lineWidth)/1000f;
		
		Float posX = width/2 - (LINES_DISTANCE + designer.lineWidth)/1000f/2;
		
		retval += "bpy.data.objects[\"Base\"].location = [" + posX + ", 0.011, " + height/2 + "];"; 
		retval += "bpy.data.objects[\"Base\"].dimensions = [" + width + ", 0.02, " + height + "];";
		
		return retval;
	}
	
	public static String updatePositions(DesignerProperties designer, List<Line> lowLines, List<Line> highLines) {
		String retval = "";
		
		Line firstLowLine = lowLines.get(0);
		Line firstHighLine = highLines.get(0);
		
		retval += updateLinesTransform(designer, "bpy.data.objects[\"Cube.001\"]", "bpy.data.objects[\"Cube\"]", firstLowLine, firstHighLine, 0);
		
		for (Integer i = 1; i < lowLines.size(); i++) {
			Line lowLine = lowLines.get(i);
			Line highLine = highLines.get(i);
			
			retval += createLine(designer, lowLine, highLine, i);
		}
		
		return retval;
	}
	
	private static String createLine(DesignerProperties designer, Line lowLine, Line highLine, Integer index) {
		String retval = "";
		String lowLineName = getNextObjectName();
		String highLineName = getNextObjectName();
		
		retval += lowLineName + " = copyObj(\"Cube.001\")" + System.getProperty("line.separator");
		retval += highLineName + " = copyObj(\"Cube\")" + System.getProperty("line.separator");
		
		retval += updateLinesTransform(designer, lowLineName, highLineName, lowLine, highLine, index);
		
		return retval;
	}
	
	private static String updateLinesTransform(DesignerProperties designer, String lowLineName, String highLineName, Line lowLine, Line highLine, Integer index) {
		String retval = "";
		
		Float xpos = index*(LINES_DISTANCE + designer.lineWidth)/1000f;
		
		Float lowLineDimensionY = lowLine.thinckness/1000f;
		Float lowLineDimensionZ = lowLine.length/1000f;
		Float lowLinePosY = -lowLineDimensionY/2;
		Float lowLinePosZ = lowLine.length/1000f/2;
		
		Float highLineDimensionY = highLine.thinckness/1000f;
		Float highLineDimensionZ = highLine.length/1000f;
		Float highLinePosY = -highLineDimensionY/2;
		Float highLinePosZ = highLine.length/1000f/2 + lowLineDimensionZ;
		
		retval += lowLineName + ".location = [" + xpos + ", " + lowLinePosY + ", " + lowLinePosZ + "];" + System.getProperty("line.separator");
		retval += lowLineName + ".dimensions = [" + designer.lineWidth/1000f + ", " + lowLineDimensionY + ", " + lowLineDimensionZ + "];" + System.getProperty("line.separator");
		
		retval += highLineName + ".location = [" + xpos + ", " + highLinePosY + ", " + highLinePosZ + "];" + System.getProperty("line.separator");
		retval += highLineName + ".dimensions = [" + designer.lineWidth/1000f + ", " + highLineDimensionY + "," + highLineDimensionZ + "];" + System.getProperty("line.separator");
		
		return retval;
	}
	
	private static String getNextObjectName() {
		return "newObj" + (objCounter++).toString();
	}
	
	private static String getFileText(String fileName) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			
		    StringBuilder sb = new StringBuilder();
		    String line;
			line = br.readLine();
			
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static void saveFileTo(String content, String fileName) {
		try (PrintWriter out = new PrintWriter(fileName)) {
		    out.println(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static String setRenderSamples(Integer renderSamples) {
		return "bpy.data.scenes['Scene'].cycles.samples = " + renderSamples.toString() + System.getProperty("line.separator");
	}
	
	private static String configCpuGpu() {
		String retval = "";
		if (USE_BLENDER_GPU) {
			retval += "bpy.data.scenes['Scene'].cycles.device = 'GPU';" + System.getProperty("line.separator"); 
			retval += "bpy.data.scenes['Scene'].render.tile_x = 256;" + System.getProperty("line.separator"); 
			retval += "bpy.data.scenes['Scene'].render.tile_y = 256;" + System.getProperty("line.separator"); 
		} else {
			retval += "bpy.data.scenes['Scene'].cycles.device = 'CPU';" + System.getProperty("line.separator"); 
			retval += "bpy.data.scenes['Scene'].render.tile_x = 16;" + System.getProperty("line.separator"); 
			retval += "bpy.data.scenes['Scene'].render.tile_y = 16;" + System.getProperty("line.separator"); 
		}
		
		return retval;
	}
	
	private static String setRenderResolution(Integer x, Integer y) {
		return "setRenderResolution(" + x + ", " + y + ")" + System.getProperty("line.separator");
	}
	
	private static String renderImage(Float x, Float y, Float z, Float rx, Float ry, Float rz, String focus, Float aperture, String folder, String fileName) {
		String retval = ""; 
		
		String focusToUse;
		if (focus == null) {
			focusToUse = "None";
		} else {
			focusToUse = "'" + focus + "'";
		}
		
		if (x != null && y != null && z != null) {
			retval += "moveCameraTo(" + x + ", " + y + ", " + z + ");" + System.getProperty("line.separator");
		}
		if (rx != null && ry != null && rz != null) {
			retval += "rotateCameraTo(" + rx + ", " + ry + ", " + rz + ");" + System.getProperty("line.separator");
		}
		if (aperture != null) {
			retval += "setFocusProperties(" + focusToUse + ", " + aperture + ");" + System.getProperty("line.separator");
		}
		
		String fileFinalPath = IMAGES_OUTPUT_PATH;
		if (folder != null) {
			fileFinalPath += folder + "/" + fileName + ".png";
		} else {
			fileFinalPath += fileName + ".png";
		}
		
		retval += "renderImage('" + fileFinalPath + "')" + System.getProperty("line.separator");
		
		return retval;
	}
	
	private static void runBlender() {
		try {
			ProcessBuilder builder = new ProcessBuilder(BLENDER_RUNNABLE_PATH_CONFIG);
			builder.start();
			
			builder = new ProcessBuilder(BLENDER_RUNNABLE_PATH, "--background", "--python", ROOT_PATH + "javaGenerated.py");
	        builder.redirectErrorStream(true);
	        Process p = builder.start();
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        while (true) {
	            line = r.readLine();
	            if (line == null) { break; }
	            System.out.println(line);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
