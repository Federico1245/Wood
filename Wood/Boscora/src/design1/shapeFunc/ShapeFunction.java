/**
 * 
 */
package design1.shapeFunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import design1.Design1FromTables;

/**
 * @author Fede
 *
 */
public abstract class ShapeFunction {

	protected Integer min;
	protected Integer max;
	
	protected Integer maxIndex;
	
	protected Integer highLineAcceptableSimilarLength;
	
	public abstract Integer getMinForIndex(Integer index);
	public abstract Integer getMaxForIndex(Integer index);
	
	public abstract void generateValues(Long seed);
	
	public abstract String getDescription();
	
	/**
	 * @return the min
	 */
	public Integer getMin() {
		return min;
	}
	/**
	 * @param min the min to set
	 */
	public void setMin(Integer min) {
		this.min = min;
	}
	/**
	 * @return the max
	 */
	public Integer getMax() {
		return max;
	}
	/**
	 * @param max the max to set
	 */
	public void setMax(Integer max) {
		this.max = max;
	}
	/**
	 * @return the maxIndex
	 */
	public Integer getMaxIndex() {
		return maxIndex;
	}
	/**
	 * @param maxIndex the maxIndex to set
	 */
	public void setMaxIndex(Integer maxIndex) {
		this.maxIndex = maxIndex;
	}
	
	/**
	 * @return the highLineAcceptableSimilarLength
	 */
	public Integer getHighLineAcceptableSimilarLength() {
		return highLineAcceptableSimilarLength;
	}
	/**
	 * @param highLineAcceptableSimilarLength the highLineAcceptableSimilarLength to set
	 */
	public void setHighLineAcceptableSimilarLength(
			Integer highLineAcceptableSimilarLength) {
		this.highLineAcceptableSimilarLength = highLineAcceptableSimilarLength;
	}
	/**
	 * 
	 */
	protected void initiallizeShapeFunction(ShapeFunction shapeFunction, Long seed) {
		shapeFunction.generateOwnMinMax(seed);
		shapeFunction.setMaxIndex(maxIndex);
		shapeFunction.generateValues(seed);
	}
	
	/**
	 * 
	 * @param rnd
	 * @return
	 */
	public static ShapeFunction getRndShapeFunction(Float rnd, Boolean addMixer) {
		List<ShapeFunction> functions = new ArrayList<ShapeFunction>();
		functions.add(new IdentityShapeFunction());
		functions.add(new PolynomialShapeFunction());
		functions.add(new TrigonometricShapeFunction());
		if (addMixer) {
			functions.add(new MixerShapeFunction());
		}
		
		return functions.get((int) Math.floor(rnd/(1f/functions.size())));
	}
	
	/**
	 * 
	 */
	public void generateOwnMinMax(Long seed) {
		Random generator = new Random(seed);
		
		min = 0;
		max = 0;
		
		while (max - min < Design1FromTables.FINAL_HEIGHT/8 || Float.valueOf(max)/Design1FromTables.FINAL_HEIGHT < 1/3f) {
			Float minRnd = generator.nextFloat();
			Float maxRnd = generator.nextFloat();

			if (minRnd > maxRnd) {
				Float tmp = minRnd;
				minRnd = maxRnd;
				maxRnd = tmp;
			}
			
			Float wideningFactor = 1.5f;
			minRnd = (float) Math.pow(minRnd, wideningFactor);
			maxRnd = (float) Math.pow(maxRnd, 1/wideningFactor);
			
			min = Math.round((Design1FromTables.FINAL_HEIGHT*8/10)*minRnd + Design1FromTables.FINAL_HEIGHT/10);
			max = Math.round((Design1FromTables.FINAL_HEIGHT*8/10)*maxRnd + Design1FromTables.FINAL_HEIGHT/10);
		}
		
		Float higherTolerance = 2f;
		highLineAcceptableSimilarLength = (int) Math.round((max - min)*Math.pow(generator.nextFloat(), higherTolerance)/2.5f);
	}
}
