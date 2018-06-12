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
public class TrigonometricShapeFunction extends ShapeFunction {

	private static final Float MIN_FREQUENCY = ((Double)Math.PI).floatValue(); 
	private static final Float MAX_FREQUENCY = MIN_FREQUENCY*3; 
	
	private Float amplitude;
	private Float frequency;
	private Float offset;
	
	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return "TrigonometricShapeFunction amplitude [" + amplitude + "] frequency [" + frequency + "] offset [" + offset + "]";
	}
	
	/**
	 * 
	 */
	@Override
	public Integer getMinForIndex(Integer index) {
		return (int) Math.ceil(Math.sin(frequency*index + offset)*amplitude + min);
	}

	/**
	 * 
	 */
	@Override
	public Integer getMaxForIndex(Integer index) {
		return (int) Math.floor(Math.sin(frequency*index + offset)*amplitude + max);
	}

	@Override
	public void generateValues(Long seed) {
		Random generator = new Random(seed);
		
		Integer maxAmplitude = null;
		if (Design1FromTables.FINAL_HEIGHT - max < min) {
			maxAmplitude = Design1FromTables.FINAL_HEIGHT - max;
		} else {
			maxAmplitude = min;
		}
		
		amplitude = generator.nextFloat()*(maxAmplitude*3/4) + maxAmplitude/4;
		Float totalFrequency = generator.nextFloat()*(MAX_FREQUENCY - MIN_FREQUENCY) + MIN_FREQUENCY;
		frequency = totalFrequency/maxIndex;
		offset = ((Double)(generator.nextFloat()*2*Math.PI)).floatValue();
	}
}
