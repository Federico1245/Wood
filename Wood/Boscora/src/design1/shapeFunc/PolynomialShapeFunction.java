/**
 * 
 */
package design1.shapeFunc;

import java.util.Random;

import design1.Design1FromTables;

/**
 * @author Fede
 *
 */
public class PolynomialShapeFunction extends ShapeFunction {

	private Float m;
	private Float b;
	
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return "PolynomialShapeFunction m [" + m + "] b [" + b + "]";
	}
	
	/**
	 * 
	 */
	@Override
	public Integer getMinForIndex(Integer index) {
		return (int) Math.ceil(index*m + b);
	}

	/**
	 * 
	 */
	@Override
	public Integer getMaxForIndex(Integer index) {
		return (int) Math.floor(index*m + b + (max - min));
	}

	@Override
	public void generateValues(Long seed) {
		Random generator = new Random(seed);
		
		Float avg = (max - min)/2f + min;
		Float minPos = avg - min;
		Float maxPos = (Design1FromTables.FINAL_HEIGHT - max) + avg;
		
		m = (maxPos - minPos)/(maxIndex - 1);
		
		Float tendsToLine = 2.5f; // Higher number will make most designs with little inclination
		Float rnd = generator.nextFloat()*2 - 1f;
		rnd = (float) Math.pow(Math.abs(rnd), tendsToLine) * Math.signum(rnd);
		m *= rnd;
		b = -(rnd + 1)/2*(Design1FromTables.FINAL_HEIGHT - (max - min)) + Design1FromTables.FINAL_HEIGHT - (max - min);
	}
}
