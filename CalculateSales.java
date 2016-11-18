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
		if(args.length != 1){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		HashMap<String, String> branch = new HashMap<String, String>();
		HashMap<String, String> commodity = new HashMap<String, String>();
		HashMap<String, Long> branchSales = new HashMap<String, Long>();
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();
		BufferedReader br = null;
		try {
			File f = new File(args[0], "branch.lst");
			FileReader fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine()) != null) {
				String[] code = line.split(",");
				if(!(code.length == 2)) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				if(!code[0].matches("^\\d{3}")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
				}
				branch.put(code[0], code[1]);
				branchSales.put(code[0], (long)0);
			}

		} catch(FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
			return;
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		try {
			File f = new File(args[0], "commodity.lst");
			FileReader fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line = "";

			while((line = br.readLine()) != null) {
				String code[] = line.split(",");
				if(!(code.length == 2)) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				if(!code[0].matches("[0-9 A-Z]{8}")) {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodity.put(code[0], code[1]);
				commoditySales.put(code[0], (long)0);
			}
		} catch(FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
			return;
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました");
			return;
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return;
			}
		}

		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		ArrayList<String> chooseRcd = new ArrayList<String>();
		if(files != null) {
			for(File f : files) {
				if(f.getName().matches("^\\d{8}.rcd$")) {
					if (f.isFile()){
						chooseRcd.add(f.getName());
					}
				}
			}
		}

		long chainNumbercheck = 0;
		for(int i = 0; i < chooseRcd.size(); i++) {
			int x = Integer.parseInt(chooseRcd.get(i).split("\\.")[0]);
			if(x - chainNumbercheck != 1) {
				System.out.println("売上ファイル名が連番になっていません");
				return;
			}
			chainNumbercheck = x;
		}

		for(int i = 0; i < chooseRcd.size(); i++) {
			File f = new File(args[0], chooseRcd.get(i));
			FileReader fr = null;
			ArrayList<String> rcdData = new ArrayList<String>();

			try {
				fr = new FileReader(f);
				br = new BufferedReader(fr);
				String line = null;
				while((line = br.readLine()) != null) {
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
					if(br != null) {
						br.close();
					}
				} catch(IOException e) {
					System.out.println("予期せぬエラーが発生しました");
					return;
				}
			}

			if(rcdData.size() != 3) {
				System.out.println(chooseRcd.get(i)+"のフォーマットが不正です");
				return;
			}
			if(!branchSales.containsKey(rcdData.get(0))) {
				System.out.println(chooseRcd.get(i)+"の支店コードが不正です");
				return;
				}
			if(!commoditySales.containsKey(rcdData.get(1))) {
				System.out.println(chooseRcd.get(i)+"の商品コードが不正です");
				return;
			}
			if(!rcdData.get(2).matches("[0-9]+")){
				System.out.println("予期せぬエラーが発生しました");
				return;
			}

			long rcdValue = Long.parseLong(rcdData.get(2));
			long branchValue = branchSales.get(rcdData.get(0));
			long branchTotal = branchValue + rcdValue;;
			long commodityValue = commoditySales.get(rcdData.get(1));
			long commodityTotal = commodityValue + rcdValue;

			if(branchTotal < 9999999999L && commodityTotal < 9999999999L) {
				branchSales.put(rcdData.get(0), branchTotal);
				commoditySales.put(rcdData.get(1), commodityTotal);
			} else {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
		}

		List<Map.Entry<String,Long>> compareBranch = new ArrayList<Map.Entry<String,Long>>(branchSales.entrySet());
	    Collections.sort(compareBranch, new Comparator<Map.Entry<String,Long>>() {
		    public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
		    	return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
	    });
	    List<Map.Entry<String,Long>> compareCommodity = new ArrayList<Map.Entry<String,Long>>(commoditySales.entrySet());
	    Collections.sort(compareCommodity, new Comparator<Map.Entry<String,Long>>() {
		    public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
		    	return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
	    });

		BufferedWriter bwBranchtotal = null;
	    try{
	    	FileWriter fwBranchtotal = new FileWriter( new File (args[0], "branch.out"));
	    	bwBranchtotal = new BufferedWriter(fwBranchtotal);
			dir.createNewFile();
		    for(Map.Entry<String,Long> e : compareBranch ) {
				bwBranchtotal.write(e.getKey() + "," + branch.get(e.getKey()) + "," + e.getValue() + System.getProperty("line.separator"));
		    }
		} catch (FileNotFoundException f) {
		    System.out.println("予期せぬエラーが発生しました");
		    return;
	    } catch (IOException f) {
			System.out.println("予期せぬエラーが発生しました");
			return;
	    } finally {
	    	try {
	    		if(bwBranchtotal != null) {
	    			bwBranchtotal.close();
	    		}
	    	} catch(IOException e) {
	    		System.out.println("予期せぬエラーが発生しました");
	    		return;
	    	}

	    }

		BufferedWriter bwCommodity = null;
	    try{
	    	FileWriter fwCommodity = new FileWriter( new File (args[0], "commodity.out"));
	    	bwCommodity = new BufferedWriter(fwCommodity);
			dir.createNewFile();
			for(Map.Entry<String,Long> e : compareCommodity ) {
				bwCommodity.write(e.getKey() + "," + commodity.get(e.getKey()) + "," + e.getValue() + System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException f) {
		    System.out.println("予期せぬエラーが発生しました");
		    return;
	    } catch (IOException f) {
			System.out.println("予期せぬエラーが発生しました");
			return;
	    } finally {
	    	try {
	    		if(bwCommodity != null) {
	    			bwCommodity.close();
	    		}
	    	} catch(IOException e) {
	    		System.out.println("予期せぬエラーが発生しました");
	    		return;
	    	}

	    }


	}

}



