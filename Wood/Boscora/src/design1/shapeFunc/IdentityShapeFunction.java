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
public class IdentityShapeFunction extends ShapeFunction {

	private Boolean niceParams;
	
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return "IdentityShapeFunction. Nice Params[" + niceParams + "]";
	}
	
	/**
	 * 
	 */
	@Override
	public Integer getMinForIndex(Integer index) {
		return min;
	}

	/**
	 * 
	 */
	@Override
	public Integer getMaxForIndex(Integer index) {
		return max;
	}


	@Override
	public void generateOwnMinMax(Long seed) {
		Random generator = new Random(seed);
		if (generator.nextFloat() < 1/3f) {
			setMin(Design1FromTables.FINAL_HEIGHT*1/3);
			setMax(Design1FromTables.FINAL_HEIGHT*7/8);
			setHighLineAcceptableSimilarLength(40);
			niceParams = true;
		} else {
			super.generateOwnMinMax(seed);
			niceParams = false;
		}
	}
	
	@Override
	public void generateValues(Long seed) {
		// Nothing to do
	}
}
