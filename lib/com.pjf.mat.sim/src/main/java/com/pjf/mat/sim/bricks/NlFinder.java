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
		public int iter1;
		public int iter2;
		
		public Result(int l, int h, int iter1, int iter2) {
			idxL = l;
			idxH = h;
			this.iter1 = iter1;
			this.iter2 = iter2;
		}

		@Override
		public String toString() {
			return "Result [idxH=" + idxH + ", idxL=" + idxL + ", iter1="
					+ iter1 + ", iter2=" + iter2 + "]";
		}		
	}
	
	public Result find(int xi, int xlast, int idxLast, int[] p) {
		int idx;
		int delta;
		int idxH = 0;
		int idxL = 0;
		int vH;
		int vL;
		boolean foundIt = false;
		boolean gotEdge = false;
		int val;
		int iter1 = 0;
		int iter2 = 0;
		
		idx = idxLast;
		if (xi > xlast) {
			delta = 1;
		} else {
			delta = -1;
		}
		idxL = idxLast;
		idxH = idxL;
		vH = xlast;
		vL = xlast;
		// search for the first edge, increasing delta by 2 each time
		while (!foundIt && !gotEdge) {
			iter1++;
			if (delta > 0) {
				idxH = idxH + delta;
				if (idxH > p.length-1) {
					idxH = p.length-1;
					break;
				}
				vH = p[idxH];
				if (vH == xi) {
					idxL = idxH;
					foundIt = true;
				} else if (vH > xi) {
					gotEdge = true;
				} else {
					idxL = idxH;
				}
			} else {
				idxL = idxL + delta;
				if (idxL < 0) {
					idxL = 0;
					break;
				}
				vL = p[idxL];
				if (vL == xi) {
					idxH = idxL;
					foundIt = true;
				} else if (vL < xi) {
					gotEdge = true;
				} else {
					idxH = idxL;
				}
			}
			delta = 2 * delta;
		}
		while (!foundIt) {
			iter2++;
			idx = (idxL + idxH) / 2;
			val = p[idx];
			if (xi == val) {
				idxL = idx;
				idxH = idx;
				foundIt = true;
			} else if (xi > val) {
				idxL = idx;
			} else {
				idxH = idx;
			}
			if (idxH == idxL + 1) {
				foundIt = true;
			}			
		}
		Result rslt = new Result(idxL,idxH,iter1,iter2);
		System.out.println("Result = " + rslt);
		return rslt;
	}
}
