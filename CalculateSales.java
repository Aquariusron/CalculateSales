package jp.co.plusize.yamashita_akane.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CalculateSales {
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		}

		HashMap<String, String> branchName = new HashMap<String, String>();
		HashMap<String, String> commodityName = new HashMap<String, String>();
		HashMap<String, Long> branchSales = new HashMap<String, Long>();
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();

		if(!checkAndPut(args[0] + File.separator + "branch.lst", "支店", "^\\d{3}", branchName, branchSales)){
			return;
		}
		if(!checkAndPut(args[0] + File.separator + "commodity.lst", "商品", "[0-9 A-Z  a-z]{8}", commodityName, commoditySales)){
			return;
		}

		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		ArrayList<String> rcdList = new ArrayList<String>();
		if (files != null) {
			for (File f : files) {
				if (f.getName().matches("^\\d{8}.rcd$")) {
					if (f.isFile()) {
						rcdList.add(f.getName());
					}
				}
			}
		}

		int chainNumber = 0;
		for (int i = 0; i < rcdList.size(); i++) {
			int num = Integer.parseInt(rcdList.get(i).split("\\.")[0]);
			if (num - chainNumber != 1) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
			chainNumber = num;
		}

		BufferedReader br = null;

		for (int i = 0; i < rcdList.size(); i++) {
			File f = new File(args[0], rcdList.get(i));
			FileReader fr = null;
			ArrayList<String> rcdData = new ArrayList<String>();

			try {
				fr = new FileReader(f);
				br = new BufferedReader(fr);
				String line = null;
				while ((line = br.readLine()) != null) {
					rcdData.add(line);
				}
			} catch (FileNotFoundException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}

			if (rcdData.size() != 3) {
				System.out.println(rcdList.get(i) + "のフォーマットが不正です");
				return;
			}
			if (!branchSales.containsKey(rcdData.get(0))) {
				System.out.println(rcdList.get(i) + "の支店コードが不正です");
				return;
			}
			if (!commoditySales.containsKey(rcdData.get(1))) {
				System.out.println(rcdList.get(i) + "の商品コードが不正です");
				return;
			}
			if (!rcdData.get(2).matches("[0-9]+")) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

			long rcdValue = Long.parseLong(rcdData.get(2));
			long branchValue = branchSales.get(rcdData.get(0));
			long branchTotal = branchValue + rcdValue;
			long commodityValue = commoditySales.get(rcdData.get(1));
			long commodityTotal = commodityValue + rcdValue;

			if (branchTotal < 9999999999L && commodityTotal < 9999999999L) {
				branchSales.put(rcdData.get(0), branchTotal);
				commoditySales.put(rcdData.get(1), commodityTotal);
			} else {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
		}

		if(!output(branchSales, "branch.out", branchName, args[0])){
			return;
		}
		if(!output(commoditySales, "commodity.out", commodityName, args[0])){
			return;
		}
	}

	public static boolean checkAndPut(String mainPath, String definition,
			 String format, HashMap<String, String> mapName, HashMap<String, Long> salesMap){

		File f = new File(mainPath);
		BufferedReader br = null;

		if(!f.exists()){
			System.out.println(definition + "定義ファイルが存在しません");
			return false;
		}

		try {
			FileReader fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null) {
				String[] code = line.split(",");
				if(code.length != 2) {
					System.out.println(definition + "定義ファイルのフォーマットが不正です");
					return false;
				}
				if(!code[0].matches(format)) {
					System.out.println(definition + "定義ファイルのフォーマットが不正です");
					return false;
				}

				mapName.put(code[0], code[1]);
				salesMap.put(code[0], (long)0);
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}

	public static boolean output(HashMap<String, Long> salesMap,String fileName,
		HashMap<String, String> firstMap, String path){

		List<Map.Entry<String,Long>> sortSalesList = new ArrayList<Map.Entry<String,Long>>(salesMap.entrySet());
		Collections.sort(sortSalesList, new Comparator<Map.Entry<String,Long>>() {
		    	public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
		    		return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
	    });

		BufferedWriter bw = null;

		try{
			FileWriter fw = new FileWriter( new File (path, fileName));
			bw = new BufferedWriter(fw);

		    for(Map.Entry<String,Long> e : sortSalesList) {
				bw.write(e.getKey() + "," + firstMap.get(e.getKey()) + "," + e.getValue() + System.getProperty("line.separator"));
		    }
		} catch (FileNotFoundException f) {
		    System.out.println("予期せぬエラーが発生しました");
		    return false;
	    } catch (IOException f) {
			System.out.println("予期せぬエラーが発生しました");
			return false;
	    } finally {
	    	try {
	    		if(bw != null) {
	    			bw.close();
	    		}
	    	} catch(IOException e) {
	    		System.out.println("予期せぬエラーが発生しました");
	    		return false;
	    	}
	    }
		return true;
	}
}
