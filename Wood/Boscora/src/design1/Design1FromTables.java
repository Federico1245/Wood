/**
 * 
 */
package design1;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import main.Main;
import design1.shapeFunc.IdentityShapeFunction;
import design1.shapeFunc.MixerShapeFunction;
import design1.shapeFunc.PolynomialShapeFunction;
import design1.shapeFunc.ShapeFunction;
import design1.shapeFunc.TrigonometricShapeFunction;
import domain.DesignEntry;

/**
 * @author Fede
 *
 */
public class Design1FromTables {

	private static final Boolean alwaysComputeTables = true;
	
	public static final Integer FINAL_WIDTH = 700;
	public static final Integer FINAL_HEIGHT = 700;
	
	private static final Integer TABLE_HEIGHT = 1000;
	private static final Integer TABLE_WIDTH = 200;
	
	private List<Line> highLines;
	private List<Line> lowLines;
	
	private List<Table> tables;

	private Long seed;
	private Integer runNumber = 0;
	private Integer lastLineId = 0;
	private Integer lastTableId = 0;
	
	private class Table {
		public Integer id;
		public Integer section;
		public Integer width;
		public Integer length;
		public List<Line> lines = new ArrayList<Line>();
		
		public Table(Integer id, Integer section, Integer width, Integer length) {
			this.id = id;
			this.section = section;
			this.width = width;
			this.length = length;
		}
		
		public Integer getLinesWidth() {
			Integer retval = 0;
			for (Line line : lines) {
				retval += line.thinckness;
			}
			return retval;
		}
		
		public void addLines(List<Line> lines) {
			this.lines.addAll(lines);
			for (Line line : lines) {
				line.tableId = id;
				line.section = section;
			}
		}
	}
	
	public void runMultipleTimes() throws Exception {
		Integer timesToRun = 200;
		Float totalWastePercentage = 0f;
		Float totalAvgSurfaceRequired = 0f;
		
		for (int i = 0; i < timesToRun; i++) {
			System.out.println("Rendering [" + i + "]");
			runNumber = i;
			build(true, null, true);
		}
		
		Float avgWaste = totalWastePercentage/timesToRun;
		Float avgSurfaceRequired = totalAvgSurfaceRequired/timesToRun;
		
		System.out.println("AvgWaste " + avgWaste);
		System.out.println("AvgSurfaceRequired " + avgSurfaceRequired);
	}
	
	public void build(Boolean saveFiles, Long seedVal, Boolean generateRender) throws Exception {
		if (seedVal != null) {
			seed = seedVal;
			runNumber = 9999;
		} else {
			seed = new Random().nextLong();
		}
		Random generator = new Random(seed);
		DesignerProperties designer = getRandomParams();
		tables = new ArrayList<Table>();
		lastLineId = 0;
		lastTableId = 0;
		
		highLines = new ArrayList<Line>();
		lowLines = new ArrayList<Line>();
		
		Integer totalLines = Double.valueOf(Math.ceil(FINAL_WIDTH/designer.lineWidth)).intValue();
		for (int i = 0; i < totalLines; i++) {
			Integer lineLength = Long.valueOf(Math.round(generator.nextFloat()*(designer.getHighLineMaxLength(i) - designer.getHighLineMinLength(i)))).intValue() + designer.getHighLineMinLength(i);
			
			Integer cutCounter = 0;
			while (i > 0 && Math.abs(lineLength - highLines.get(i - 1).length) < designer.getShapeFunction().getHighLineAcceptableSimilarLength() && cutCounter < 3000) {
				lineLength = Long.valueOf(Math.round(generator.nextFloat()*(designer.getHighLineMaxLength(i) - designer.getHighLineMinLength(i)))).intValue() + designer.getHighLineMinLength(i);
				cutCounter++;
			}
			
			Integer highLineThickness = Long.valueOf(Math.round(generator.nextFloat()*(designer.highLineMaxThickness - designer.highLineMinThickness))).intValue() + designer.highLineMinThickness;
			Integer lowLineThickness = Long.valueOf(Math.round(generator.nextFloat()*(designer.lowLineMaxThickness - designer.lowLineMinThickness))).intValue() + designer.lowLineMinThickness;
			
			Line highLine = new Line(lastLineId++, highLineThickness, lineLength, true);
			Line lowLine = new Line(lastLineId++, lowLineThickness, FINAL_HEIGHT - lineLength, false);
			
			highLines.add(highLine);
			lowLines.add(lowLine);
		}
		
		Boolean result = updateTableLines(designer, seedVal != null || alwaysComputeTables);
		
		if (result && saveFiles && isValidBuild()) {
			paintResult(designer);
			if (seedVal != null || alwaysComputeTables) {
				//paintTables();
			}
			saveResultTechnicalSheet(designer);
		}
		
		if (generateRender) {
			DesignEntry de = new DesignEntry();
			de.setSeed(seed);
			de.setAddedDate(Calendar.getInstance().getTime());
			de.save();
			
			Design1BlenderGenerator.generateRender(designer, lowLines, highLines, seed);
		}
	}
	
	private void paintResult(DesignerProperties designer) throws IOException {
		BufferedImage newImg = new BufferedImage(FINAL_WIDTH, FINAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Boolean[][] pixels = new Boolean[FINAL_WIDTH][FINAL_HEIGHT];
		
		for (int i = 0; i < highLines.size(); i++) {
			Line line = highLines.get(i);
			
			for (int row = Math.round(i*designer.lineWidth); row < (i+1)*designer.lineWidth; row++) {
				if (row >= FINAL_HEIGHT) {
					break;
				}
				for (int col = 0; col < line.length; col++) {
					int current = 0;
		    		int color = 0xFF000000 + (current << 16) + (current << 8) + current;
		    		newImg.setRGB(row, col, color);
		    		pixels[row][col] = true;
				}
			}
		}
		
		for (int row = 0; row < FINAL_WIDTH; row++) {
	    	for (int col = 0; col < FINAL_HEIGHT; col++) {
	    		if (pixels[row][col] == null) {
					int current = 255;
		    		int color = 0xFF000000 + (current << 16) + (current << 8) + current;
		    		newImg.setRGB(row, col, color);
	    		}
	    	}
	    }
		
		File f = new File(Main.PATH + runNumber.toString() + ".png");
	    ImageIO.write(newImg, "png", f);
	}
	
	private void paintTables() throws IOException {
		Integer tablesSeparation = 100;
		Integer imgWidth = (tables.get(tables.size() - 1).id + 1)*(TABLE_WIDTH + tablesSeparation) - tablesSeparation;
		BufferedImage newImg = new BufferedImage(imgWidth, TABLE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Boolean[][] pixels = new Boolean[imgWidth][TABLE_HEIGHT];
		
		Integer marginLeftTable = 0;
		Integer marginTop = 0;
		Integer lastTableId = 0;
		for (int i = 0; i < tables.size(); i++) {
			Table table = tables.get(i);
			
			if (lastTableId != table.id) {
				lastTableId = table.id;
				marginTop = 0;
				
				// Paint gray gap separating tables
				for (int row = marginLeftTable + TABLE_WIDTH; row < marginLeftTable + TABLE_WIDTH + tablesSeparation; row++) {
			    	for (int col = 0; col < TABLE_HEIGHT; col++) {
			    		if (pixels[row][col] == null) {
							int current = 127;
				    		int color = 0xFF000000 + (current << 16) + (current << 8) + current;
				    		newImg.setRGB(row, col, color);
				    		pixels[row][col] = true;
			    		}
			    	}
			    }
				
				marginLeftTable += TABLE_WIDTH + tablesSeparation;
			}
			
			Integer marginLeft = marginLeftTable;
			for (int j = 0; j < table.lines.size(); j++) {
				Line line = table.lines.get(j);
				
				for (int row = marginLeft; row < marginLeft + line.thinckness; row++) {
					for (int col = marginTop; col < marginTop + line.length; col++) {
			    		int color = line.dark ? 0xff3a595c : 0xff7ba9a9;
			    		newImg.setRGB(row, col, color);
			    		pixels[row][col] = true;
					}
				}
				
				marginLeft += line.thinckness + Main.SAW_DISK_THICKNESS;
			}
			
			if (table.lines.size() > 0) {
				marginTop += table.lines.get(0).length + Main.SAW_DISK_THICKNESS;
			}
		}
		
		for (int row = 0; row < imgWidth; row++) {
	    	for (int col = 0; col < TABLE_HEIGHT; col++) {
	    		if (pixels[row][col] == null) {
					int current = 255;
		    		int color = 0xFF000000 + (current << 16) + (current << 8) + current;
		    		newImg.setRGB(row, col, color);
	    		}
	    	}
	    }
		
		File f = new File(Main.PATH + runNumber.toString() + "_tables.png");
	    ImageIO.write(newImg, "png", f);
	}
	
	private void saveResultTechnicalSheet(DesignerProperties designer) throws IOException {
		BufferedWriter writer = null;
		File file = new File(Main.PATH + runNumber.toString() + ".txt");
		
		writer = new BufferedWriter(new FileWriter(file));
		writer.write("Seed [" + seed + "]");
		writer.newLine(); writer.newLine();
		
		writer.write("Designer Shape Function [" + designer.getShapeFunction().getDescription() + "]");
		writer.newLine(); writer.newLine();
		
		writer.write("/***BUILDING INSTRUCTIONS***/");
		writer.newLine(); writer.newLine();
		
		writer.write("Tables");
		writer.newLine();
		for (Table table : tables) {
			writer.write("Table N° " + table.id + " section ID [" + table.section + "] length [" + table.length + "]");
			writer.newLine();
			for (Line line : table.lines) {
				writer.write("\tLine ID [" + line.id + "] " + (line.dark ? "High Line" : "Low Line"));
				writer.newLine();
			}
		}
		
		writer.write("High Lines");
		writer.newLine();
		for (Integer i = 0; i < highLines.size(); i++) {
			Line line = highLines.get(i);
			writer.write("Line N° " + i.toString() + " cut ID [" + line.id + "] Table ID [" + line.tableId + "] Section [" + line.section + "] length [" + line.length + "] Thickness [" + line.thinckness + "]");
			writer.newLine();
		}
		
		writer.write("Low Lines");
		writer.newLine();
		for (Integer i = 0; i < lowLines.size(); i++) {
			Line line = lowLines.get(i);
			writer.write("Line N° " + i.toString() + " cut ID [" + line.id + "] Table ID [" + line.tableId + "] Section [" + line.section + "] length [" + line.length + "] Thickness [" + line.thinckness + "]");
			writer.newLine();
		}
		
        writer.close();
	}
	
	private Boolean updateTableLines(DesignerProperties designer, Boolean computeBestTablesUse) {
		List<List<Line>> linesAgrouped = new ArrayList<List<Line>>();
		List<Line> allLines = new ArrayList<Line>();
		allLines.addAll(highLines);
		allLines.addAll(lowLines);
		
		Collections.sort(allLines, new LineComparator()); // Big to Small
	
		while (allLines.size() > 0) {
			List<Line> currentLines = new ArrayList<Line>();
			Integer widthSum = 0;
			while (widthSum + designer.lowLineMinThickness < TABLE_WIDTH && allLines.size() > 0) {
				Line line = allLines.get(0);
				currentLines.add(line);
				allLines.remove(line);
				
				widthSum += line.thinckness;
			}
			linesAgrouped.add(currentLines);
		}
		
		for (List<Line> lines : linesAgrouped) {
			Boolean result = adjustLinesThickness(designer, lines);
			if (result == false) {
				return false;
			}
			Collections.sort(lines, new LineComparator());
		}
		
		if (computeBestTablesUse) {
			List<List<Line>> bestOrder = getBestTablesUse(linesAgrouped);
			Table currentTable = null;
			for (List<Line> lines : bestOrder) {
				if (currentTable == null || lines.get(0).length > currentTable.length) {
					currentTable = new Table(lastTableId++, 0, TABLE_WIDTH, TABLE_HEIGHT);
					tables.add(currentTable);
				}
				
				currentTable.addLines(lines);
				
				currentTable = new Table(currentTable.id, currentTable.section + 1, TABLE_WIDTH, currentTable.length - lines.get(0).length - Main.SAW_DISK_THICKNESS);
				tables.get(tables.size() - 1).length = lines.get(0).length;
				if (currentTable.length > 0) {
					tables.add(currentTable);
				}
			}
		}
		
		return true;
	}
	
	private List<List<Line>> getBestTablesUse(List<List<Line>> linesAgrouped) {
		List<Integer> linesLength = getLinesLength(linesAgrouped);
		List<Integer> order = new ArrayList<Integer>();
		for (int i = 0; i < linesAgrouped.size(); i++) {
			order.add(i);
		}
		
		List<List<Integer>> orderPermutations = getAllOrderPermutations(order);
		
		Integer lowestCost = Integer.MAX_VALUE;
		Integer orderIndex = null;
		
		for (int i = 0; i < orderPermutations.size(); i++) {
			List<Integer> currentOrder = orderPermutations.get(i);
			List<Integer> currentLinesLength = new ArrayList<Integer>();
			for (Integer elem : currentOrder) {
				currentLinesLength.add(linesLength.get(elem.intValue()));
			}
			Integer cost = getOrderCost(currentLinesLength);
			
			if (cost < lowestCost) {
				lowestCost = cost;
				orderIndex = i;
			}
		}
		
		List<Integer> bestPermutation = orderPermutations.get(orderIndex);
		List<List<Line>> retval = new ArrayList<List<Line>>();
		for (Integer elem : bestPermutation) {
			retval.add(linesAgrouped.get(elem));
		}
		
		return retval;
	}
	
	private List<Integer> getLinesLength(List<List<Line>> linesAgrouped) {
		List<Integer> retval = new ArrayList<Integer>();
		for (List<Line> lines : linesAgrouped) {
			retval.add(lines.get(0).length);
		}
		
		return retval;
	}
	
	private Integer getOrderCost(List<Integer> lengths) {
		Integer tablesUsed = 0;
		List<Integer> tables = new ArrayList<Integer>();
		
		for (Integer length : lengths) {
			Integer tableToUse = null;
			if (tables.size() > 0 && tables.get(tables.size() - 1) >= length) {
				tableToUse = tables.size() - 1;
			}
			
			if (tableToUse == null) {
				tables.add(TABLE_HEIGHT);
				tablesUsed++;
				tableToUse = tables.size() - 1;
			}
			
			Integer newLength = tables.get(tableToUse) - length - Main.SAW_DISK_THICKNESS;
			tables.remove(tableToUse.intValue());
			if (newLength > 0) {
				tables.add(tableToUse, newLength);
			}
		}
		
		Integer longestLength = 0;
		for (Integer length : tables) {
			if (length > longestLength) {
				longestLength = length;
			}
		}
		
		return tablesUsed*TABLE_HEIGHT - longestLength;
	}
	
	private static List<List<Integer>> getAllOrderPermutations(List<Integer> order) {
		List<List<Integer>> retval = new ArrayList<List<Integer>>();
		
		if (order.size() == 1) {
			retval.add(new ArrayList<Integer>(order));
			return retval;
		}
		
		for (int i = 0; i < order.size(); i++) {
			List<Integer> remaining = new ArrayList<Integer>(order);
			remaining.remove(i);
			
			List<List<Integer>> currentPermutations = getAllOrderPermutations(remaining);
			for (List<Integer> permutation : currentPermutations) {
				List<Integer> current = new ArrayList<Integer>();
				current.add(order.get(i));
				current.addAll(permutation);
				
				retval.add(current);
			}
		}
		
		return retval;
	}
	
	private Boolean adjustLinesThickness(DesignerProperties designer, List<Line> lines) {
		Integer totalWidth = 0;
		for (Line line : lines) {
			totalWidth += line.thinckness + Main.SAW_DISK_THICKNESS;
		}
		totalWidth -= Main.SAW_DISK_THICKNESS;
		
		if (totalWidth + designer.lowLineMinThickness < TABLE_WIDTH) {
			return true;
		}
		
		Integer toDiscount = totalWidth - TABLE_WIDTH;
		Integer toDiscountByLine = Math.round(Float.valueOf(toDiscount)/lines.size());
		if (toDiscountByLine.equals(0) && !toDiscount.equals(0)) {
			toDiscountByLine = toDiscount/Math.abs(toDiscount);
		}
		
		Integer safeCounter = 0;
		while (!toDiscount.equals(0)) {
			safeCounter++;
			if (Math.abs(toDiscount) < Math.abs(toDiscountByLine)) {
				toDiscountByLine = toDiscount;
			}
			Collections.shuffle(lines);
			Line line = lines.get(0);
			
			if (line.dark) {
				if (line.thinckness - toDiscountByLine >= designer.highLineMinThickness && line.thinckness - toDiscountByLine <= designer.highLineMaxThickness) {
					line.thinckness -= toDiscountByLine;
					toDiscount-= toDiscountByLine;
				}
			}
			
			if (!line.dark) {
				if (line.thinckness - toDiscountByLine >= designer.lowLineMinThickness && line.thinckness - toDiscountByLine <= designer.lowLineMaxThickness) {
					line.thinckness -= toDiscountByLine;
					toDiscount-= toDiscountByLine;
				}
			}
			
			if (safeCounter > 100) {
				return false;
			}
		}
		
		return true;
	}
	
	class LineComparator implements Comparator<Line> {
	    @Override
	    public int compare(Line a, Line b) {
	        return -a.length.compareTo(b.length);
	    }
	}

	
	private Boolean isValidBuild() {
		/*if (highLinesWaste.size() + lowLinesWaste.size() > 35) {
			return false;
		}*/
		
		Boolean isThereAnyLeftover = false;
		for (Line line : highLines) {
			if (line.tableId == null) {
				isThereAnyLeftover = true;
			}
		}
		for (Line line : lowLines) {
			if (line.tableId == null) {
				isThereAnyLeftover = true;
			}
		}
		
		if (tables.size() > 4 && isThereAnyLeftover) {
			return false;
		}
		
		return true;
	}
	
	private DesignerProperties getNiceParams() {
		DesignerProperties retval = new DesignerProperties();
		retval.lineWidth = 22.2f;
		
		retval.highLineMinThickness = 27;
		retval.highLineMaxThickness = 45;
		retval.lowLineMinThickness = 10;
		retval.lowLineMaxThickness = 23;
		
		ShapeFunction shapeFunc = new IdentityShapeFunction();
		shapeFunc.setMin(FINAL_HEIGHT*1/3);
		shapeFunc.setMax(FINAL_HEIGHT*7/8);
		shapeFunc.setHighLineAcceptableSimilarLength(40);
		
		retval.setShapeFunction(shapeFunc);
		
		return retval;
	}
	
	private DesignerProperties getRandomParams() {
		DesignerProperties retval = new DesignerProperties();
		Random generator = new Random(seed);
		 
		Integer mult = generator.nextFloat() < 0.9f ? 1 : 2;
		retval.lineWidth = 22.2f*mult;
		
		retval.highLineMinThickness = 27;
		retval.highLineMaxThickness = 45;
		retval.lowLineMinThickness = 10;
		retval.lowLineMaxThickness = 23;
		
		Integer maxLines = Double.valueOf(Math.ceil(FINAL_WIDTH/retval.lineWidth)).intValue();
		ShapeFunction shapeFunc = ShapeFunction.getRndShapeFunction(generator.nextFloat(), true);
		shapeFunc.setMaxIndex(maxLines);
		shapeFunc.generateOwnMinMax(seed);
		shapeFunc.generateValues(seed);
		
		retval.setShapeFunction(shapeFunc);
		
		return retval;
	}
}
