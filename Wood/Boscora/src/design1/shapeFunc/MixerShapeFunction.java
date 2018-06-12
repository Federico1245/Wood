/**
 * 
 */
package design1.shapeFunc;

import java.util.Random;

/**
 * @author Fede
 *
 */
public class MixerShapeFunction extends ShapeFunction {

	private ShapeFunction shapeFuncA;
	private ShapeFunction shapeFuncB;
	
	private Integer limitIndexA;
	
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return "MixerShapeFunction composed by [" + shapeFuncA.getDescription() + "] and [" + shapeFuncB.getDescription() + "] limiting at [" + limitIndexA + "/" + maxIndex + "]";
	}
	
	/**
	 * 
	 */
	@Override
	public Integer getMinForIndex(Integer index) {
		return (index < limitIndexA) ? shapeFuncA.getMinForIndex(index) : shapeFuncB.getMinForIndex(index);
	}

	/**
	 * 
	 */
	@Override
	public Integer getMaxForIndex(Integer index) {
		return (index < limitIndexA) ? shapeFuncA.getMaxForIndex(index) : shapeFuncB.getMaxForIndex(index);
	}

	@Override
	public void generateValues(Long seed) {
		Random generator = new Random(seed);

		shapeFuncA = getRndShapeFunction(generator.nextFloat(), false);
		shapeFuncB = getRndShapeFunction(generator.nextFloat(), false);
		
		initiallizeShapeFunction(shapeFuncA, seed - 1);
		initiallizeShapeFunction(shapeFuncB, seed - 2);
		
		limitIndexA = (int) Math.round(generator.nextFloat()*getMaxIndex());
	}

}
