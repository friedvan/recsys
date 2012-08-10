package probs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DataModel {
	private HashMap<ArrayList<Integer>, Integer> map;
	private String filePath;
	private int userLength;
	private int itemLength;
	private int[][] adjaMatrix;
	private int[] userDegree;
	private int[] itemDegree;
	private float[][] weightMatrix;

	public DataModel(String filePath) {
		this.filePath = filePath;
		map = new HashMap<ArrayList<Integer>, Integer>();
		userLength = 0;
		itemLength = 0;

		long begin, end;
		begin = System.currentTimeMillis();
		this.readDataFile();		
		end = System.currentTimeMillis();
		System.out.println("readfile:\t" + (end - begin));
		
		begin = System.currentTimeMillis();
		this.generateAdjaMatrix();
		end = System.currentTimeMillis();
		System.out.println("adjaMatrix:\t" + (end - begin));
		
		begin = System.currentTimeMillis();	
		this.calcDegree();
		end = System.currentTimeMillis();
		System.out.println("degree:\t" + (end - begin));
		
		begin = System.currentTimeMillis();
		this.calcWeightMatrix();
		end = System.currentTimeMillis();
		System.out.println("wightMatrix:\t" + (end - begin));
	}

	public int getUserLength() {
		return userLength;
	}

	public int getItemLength() {
		return itemLength;
	}

	private void generateAdjaMatrix() {
		int userID, itemID;
		Iterator<ArrayList<Integer>> it;
		ArrayList<Integer> key;

		adjaMatrix = new int[userLength][itemLength];
		userDegree = new int[userLength];
		itemDegree = new int[itemLength];
		weightMatrix = new float[itemLength][itemLength];

		it = map.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();

			userID = key.get(0);
			itemID = key.get(1);
			// 大于或等于三分的表示喜欢
			if (map.get(key) >= 3)
				adjaMatrix[userID][itemID] = 1;
		}

		/*
		int user, item;
		for (user = 1; user < userLength; user++) {
			for (item = 1; item < itemLength; item++) {
				System.out.print(adjaMatrix[user][item] + " ");
			}
			System.out.println();
		}*/

	}

	private void calcDegree() {
		int user, item;
		int degree = 0;

		// get degree of item
		for (user = 1; user < userLength; user++) {
			for (item = 1; item < itemLength; item++) {
				if (adjaMatrix[user][item] != 0) {
					degree++;
				}
			}
			userDegree[user] = degree;
			degree = 0;
		}

		// get degree of item
		degree = 0;
		for (item = 1; item < itemLength; item++) {
			for (user = 1; user < userLength; user++) {
				if (adjaMatrix[user][item] != 0)
					degree++;
			}
			itemDegree[item] = degree;
			degree = 0;
		}
	}

	private void calcWeightMatrix() {
		int itemI, itemJ, user;
		float resourse = 0.0f;

		//待优化，速度太慢了！
		for (itemI = 1; itemI < itemLength; itemI++) {
			for (itemJ = 1; itemJ < itemLength; itemJ++) {
				for (user = 1; user < userLength; user++) {
					if (userDegree[user] == 0 || adjaMatrix[user][itemI] == 0 || adjaMatrix[user][itemJ] == 0)
						continue;
					resourse += (float) (adjaMatrix[user][itemI] * adjaMatrix[user][itemJ])
							/ (float) userDegree[user];
				}
				if (itemDegree[itemJ] == 0)
					continue;
				weightMatrix[itemI][itemJ] = resourse
						/ (float) itemDegree[itemJ];
				resourse = 0.0f;
			}
		}

		/*
		for (itemI = 1; itemI < itemLength; itemI++) {
			for (itemJ = 1; itemJ < itemLength; itemJ++) {
				System.out.printf("%4.3f\t", weightMatrix[itemI][itemJ]);
			}
			System.out.println();
		}*/
	}

	public float[][] getWeightMatrix() {
		return weightMatrix;
	}

	public int[][] getAdjaMatrix() {
		return adjaMatrix;
	}

	private void insertLine(int userID, int itemID, int rating) {
		ArrayList<Integer> key = new ArrayList<Integer>();
		key.add(userID);
		key.add(itemID);

		map.put(key, rating);
	}

	private void readDataFile() {
		FileReader fr = null;
		BufferedReader br = null;
		int userID, itemID, rating;

		try {
			try {
				// try to read data from file
				fr = new FileReader(filePath);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}

			br = new BufferedReader(fr);
			String line = br.readLine();

			int beginIndex = 0;
			int endIndex = 0;

			// read lines from data file
			while (line != null) {

				// read columns
				beginIndex = 0;
				endIndex = line.indexOf('\t', beginIndex);
				userID = Integer.parseInt(line.substring(beginIndex, endIndex));

				beginIndex = endIndex + 1;
				endIndex = line.indexOf('\t', beginIndex);
				itemID = Integer.parseInt(line.substring(beginIndex, endIndex));

				beginIndex = endIndex + 1;
				endIndex = line.indexOf('\t', beginIndex);
				rating = Integer.parseInt(line.substring(beginIndex, endIndex));

				// this.readColumn(line, 0, 'p');
				// insert the data to hashmap
				this.insertLine(userID, itemID, rating);

				// get maximum user and item number
				userLength = userLength > userID ? userLength : userID;
				itemLength = itemLength > itemID ? itemLength : itemID;

				//System.out.printf("%d\t%d\t%d\n", userID, itemID, rating);

				line = br.readLine();
			}

			// start from 0, and the maximum is userLength, so the total
			// number is maximum
			userLength++;
			itemLength++;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		long begin, end;
		begin = System.currentTimeMillis();
		DataModel data = new DataModel("d://movielens.data");
		end = System.currentTimeMillis();
		System.out.println("datamodel:\t" + (end - begin));
		
		begin = System.currentTimeMillis();
		ProbS probs = new ProbS(data);
		end = System.currentTimeMillis();
		System.out.println("ProbS:\t" + (end - begin));
		
		probs.allocateInitialResourse();
		
		begin = System.currentTimeMillis();
		float[][] recResult = probs.recommendation();
		end = System.currentTimeMillis();
		System.out.println("recmmendation:\t" + (end - begin));
				
		
		System.out.println("-------------------------------------------------------------");
		int user = 2;
		/*int length = recResult[user].length;
		for (int i = 1; i < length; i++) {
			//System.out.println(i + "\t" + recResult[user][i]);
			System.out.printf("%d\t%4.3f\n", i, recResult[user][i]);
		}*/
		
		TopN top = new TopN(recResult[user], 10);
		top.ranking();

	}
}
