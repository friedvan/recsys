package probs;

import java.util.Arrays;

public class ProbS {
	private float[][] weightMatrix;
	private float[][] initialResourse;
	private float[][] finalResourse;
	private int itemLength;
	private int userLength;
	private DataModel data;

	public ProbS(DataModel data) {
		this.data = data;
		this.itemLength = data.getItemLength();
		this.userLength = data.getUserLength();
		this.weightMatrix = data.getWeightMatrix();
	}

	public void allocateInitialResourse() {
		int user, item;
		byte[][] adja = data.getAdjaMatrix();
		initialResourse = new float[userLength][itemLength];
		for (user = 1; user < userLength; user++) {
			for (item = 1; item < itemLength; item++) {
				initialResourse[user][item] = (float) adja[user][item];
			}
		}
	}

	public float[][] recommendation() {
		int user, itemI, itemJ;
		finalResourse = new float[userLength][itemLength];

		
		for (user = 1; user < userLength; user++) {
			for (itemI = 1; itemI < itemLength; itemI++) {
				for (itemJ = 1; itemJ < itemLength; itemJ++) {
					finalResourse[user][itemI] += (weightMatrix[itemI][itemJ] * initialResourse[user][itemJ]);
				}
			}

			// Arrays.sort(finalResourse[user]);
		}
		return finalResourse;
	}
}
