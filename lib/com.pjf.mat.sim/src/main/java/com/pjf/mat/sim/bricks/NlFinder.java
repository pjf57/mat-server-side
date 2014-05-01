package com.pjf.mat.sim.bricks;

/**
 * Find a point among a nonlinearly spaced set
 * 
 * @author pjf
 */

public class NlFinder {
	
	public class Result {
		public int idxH;
		public int idxL;
		
		public Result(int h, int l) {
			idxH = h;
			idxL = l;
		}
		
	}
	
	public Result find(int xi, int xlast, int idxLast, int[] p) {
		int idx;
		int delta;
		int idxH = 0;
		int idxL = 0;
		boolean foundIt = false;
		boolean gotEdge = false;
		int val;
		
		idx = idxLast;
		if (xi > xlast) {
			delta = 1;
		} else {
			delta = -1;
		}
		// search for the first edge, increasing delta by 2 each time
		while (!foundIt && !gotEdge) {
			idx = idx + delta;
			if (idx >= p.length) {
				idx = p.length;
				idxH = idx;
				break;
			}
			if (idx < 0) {
				idx = 0;
				idxH = idx;
				break;
			}
			val = p[idx];
			if (val == xi) {
				foundIt = true;
				break;
			}
			if (delta > 0) {
				if (val > xi) {
					idxH = idx;
					gotEdge = true;
					break;
				}
			} else {
				// delta < 0
				if (val < xi) {
					gotEdge = true;
					idxL = idx;
					break;
				}				
			}
			delta = delta * 2;
		}
		gotEdge = false;
		// now either have foundit or gotEdge
		while (!foundIt) {
			// now bring it back, cutting delta by half each time
			delta = delta / 2;
			idx = idx - delta;
			if (idx >= p.length) {
				idx = p.length;
				idxH = idx;
				break;
			}
			if (idx < 0) {
				idx = 0;
				idxH = idx;
				break;
			}
			val = p[idx];
			if (val == xi) {
				foundIt = true;
				break;
			}
			if (delta > 0) {
				if (val < xi) {
					idxH = idx + delta;
					gotEdge = true;
					break;
				}
			} else {
				// delta < 0
				if (val > xi) {
					gotEdge = true;
					idxL = idx + delta;
					break;
				}				
			}
		}
		Result rslt = new Result(idxH,idxL);
		return rslt;
	}
}
