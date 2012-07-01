package com.pjf.mat.sandbox;

public class Ema {
	
	private float	last;
	private final float	alpha;
	
	public Ema(int len) {
		alpha = 2.0f / (len+1);
		last = 0;
		System.out.println("alpha=" + fpToText(alpha) );
	}
	
	public float process(float data) {
		float next = (alpha * (data - last)) + last;
//		System.out.println("  data=" + fpToText(data) + " subR=" + fpToText(data-last) + " mltR=" + fpToText(alpha*(data-last)) + " next=" + fpToText(last));	
		last = next;
		return next;
	}
	
	public String fpToText(float f) {
		StringBuffer buf = new StringBuffer("[");
		buf.append(f);
		buf.append(',');
		buf.append(Integer.toHexString(Float.floatToIntBits(f)));
		buf.append("]");
		return buf.toString();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Ema ema = new Ema(3);
		float d = 50f;
		for (int i=0; i<5; i++) {
			System.out.println("input=" + d + " ema=" + ema.fpToText(ema.process(d)));
			d += 0.25f;
		}

	}

}
