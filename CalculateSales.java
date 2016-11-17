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
			return;
		}
		HashMap<String, String> branch = new HashMap<String, String>();
		HashMap<String, String> commodity = new HashMap<String, String>();
		HashMap<String, Long> branchSales = new HashMap<String, Long>();//支店コード：売り上げ
		HashMap<String, Long> commoditySales = new HashMap<String, Long>();//商品コード：売り上げ
		BufferedReader br = null;
		try {
			File f = new File(args[0], "branch.lst");
			FileReader fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line = "";

			while((line = br.readLine()) != null) {
				System.out.println(line);
				String[] code = line.split(",");
				if(code.length == 2){
					branch.put(code[0], code[1]);
					branchSales.put(code[0], (long)0);
				}
				if(!code[0].matches("^\\d{3}")) {
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;
					/*支店定義コードに数値以外(アルファベット、ひらがな、
					 * カタカナ、漢字、記号)が含まれていた場合*/
				}


			}
		} catch(FileNotFoundException e) {
			System.out.println("支店定義ファイルが存在しません");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("予期せぬエラーが発生しました");
			}
		}

		try {
			File f = new File(args[0], "commodity.lst");
			FileReader fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line = "";

			while((line = br.readLine()) != null) {
				System.out.println(line);
				String code[] = line.split(",");
				if(code.length == 2){
					commodity.put(code[0], code[1]);
					commoditySales.put(code[0], (long)0);
				} else {
					System.out.println("商品定義ファイルのフォーマットが不正です");
				}

				if(code[0].matches("[0-9 A-Z]{8}")) {

				} else {
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
					/*商品定義コードに数値以外(アルファベット、ひらがな、
					 * カタカナ、漢字、記号)が含まれていた場合*/
				}

			}
		} catch(FileNotFoundException e) {
			System.out.println("商品定義ファイルが存在しません");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
				System.out.println("予期せぬエラーが発生しました");
			}

		}



		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		ArrayList<String> choose = new ArrayList<String>();
		if(files != null) {
			for(File f : files) {
				if(f.getName().matches("^\\d{8}.rcd$")) {
					//System.out.println(Integer.parseInt(f.getName().split("\\.")[0]));
					//rcdだけの箱を作る
					choose.add(f.getName());
				}
			}
		}

		// rcdだけが入った箱を回す　連番チッェクのため
		int hoge = 0;

			for(int i = 0; i < choose.size(); i++) {
				int x = Integer.parseInt(choose.get(i).split("\\.")[0]);
				if(x - hoge != 1) {
					System.out.println("売り上げファイルが連番になっていません");
					break;
				}
				hoge = x;
			}


		// rcdだけが入った箱を回す　売り上げの計算

		for(int i = 0; i < choose.size(); i++) {
			File f = new File(args[0], choose.get(i));
			FileReader fr = null;
			ArrayList<String> rcdData = new ArrayList<String>();

			try {
				// rcdDataの内容の要素数が３でなければ処理終了
				fr = new FileReader(f);
				br = new BufferedReader(fr);
				String line = "";
				while((line = br.readLine()) != null) { // rcdファイル
					// 一行ずつ読み込んで、リストに格納する
					rcdData.add(line);
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("予期せぬエラーが発生しました");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("予期せぬエラーが発生しました");
			}

			if(rcdData.size() != 3) {
				System.out.println("＜"+choose.get(0)+"＞のフォーマットが不正です");
				return;
			}
			if(!branchSales.containsKey(rcdData.get(0))) {
				System.out.println("＜"+branchSales.get(0)+"＞の支店コードが不正です");
			}
			if(!commoditySales.containsKey(rcdData.get(1))) {
				System.out.println("＜"+commoditySales.get(1)+"＞の商品コードが不正です");
			}


			 // 集計
			 /*rcdData.get(0) -> 支店コード
			 rcdData.get(1) -> 商品コード
			 rcdData.get(2) -> 金額 */

			long vAlue;
			long branchValue;
			long branchTotal;
			long commodityValue;
			long commodityTotal;
			vAlue = Integer.parseInt(rcdData.get(2));
			branchValue = branchSales.get(rcdData.get(0));
			branchTotal = branchValue + vAlue;
			commodityValue = commoditySales.get(rcdData.get(1));
			commodityTotal = commodityValue + vAlue;

			if(branchTotal < 9999999999L && commodityTotal < 9999999999L) {
				branchSales.put(rcdData.get(0), branchTotal);
				commoditySales.put(rcdData.get(1), commodityTotal);
			} else {
				System.out.println("合計金額が10桁を超えました");
				return;
			}
		}




		//売り上げの降順になるようにマップをソート　リストの中でマップの値を比べる作業をしている？
		//以下はbranchSales
		List<Map.Entry<String,Long>> compareBranch
		= new ArrayList<Map.Entry<String,Long>>(branchSales.entrySet());
	    Collections.sort(compareBranch, new Comparator<Map.Entry<String,Long>>() {
		    public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
		    	return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
	    });
		//以下はcommoditySales
	    List<Map.Entry<String,Long>> compareCommodity
		= new ArrayList<Map.Entry<String,Long>>(commoditySales.entrySet());
	    Collections.sort(compareCommodity, new Comparator<Map.Entry<String,Long>>() {
		    public int compare(Entry<String,Long> entry1, Entry<String,Long> entry2) {
		    	return ((Long)entry2.getValue()).compareTo((Long)entry1.getValue());
			}
	    });



	    //branch.out
	    //ファイル開く（bufferWriter）


	    //Listの中身出力


	    //Close

//	    System.out.println(compareBranch.get(0));
//	    System.out.println(branchSales.get("001") + "," + branchSales.get("002") + ",");
//	    System.out.println(commoditySales.get("compareBranch.get(0)") + "," + commoditySales.get("MNU00002") + ",");
//	    System.out.println(branch.keySet());
//	    System.out.println(branch.get("0"));

	    try{
		    String BR = System.getProperty("line.separator");
		    FileWriter fw = new FileWriter( new File (args[0], "branch.out"));
			BufferedWriter bw = new BufferedWriter(fw);
			dir.createNewFile();

		    for(Map.Entry<String,Long> e : compareBranch ) {
//			System.out.println(e.getKey() + "," + branch.get(e.getKey()) + ","+ e.getValue());
//			System.out.println();
				bw.write(e.getKey() + "," + branch.get(e.getKey()) + "," + e.getValue() + BR);
		    }
		    bw.close();
		} catch (FileNotFoundException f) {
		    f.printStackTrace();
		    System.out.println("予期せぬエラーが発生しました");
	    } catch (IOException f) {
			f.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");
	    }


	    try{
		    String BR = System.getProperty("line.separator");
		    FileWriter fw = new FileWriter( new File (args[0], "commodity.out"));
			BufferedWriter bw = new BufferedWriter(fw);
			dir.createNewFile();

			for(Map.Entry<String,Long> e : compareCommodity ) {
//			System.out.println(e.getKey() + "," + commodity.get(e.getKey()) +","+ e.getValue() + BR);
//			System.out.println();
			bw.write(e.getKey() + "," + commodity.get(e.getKey()) + "," + e.getValue() + BR);
		}
		    bw.close();
		} catch (FileNotFoundException f) {
		    f.printStackTrace();
		    System.out.println("予期せぬエラーが発生しました");
	    } catch (IOException f) {
			f.printStackTrace();
			System.out.println("予期せぬエラーが発生しました");
	    }


}
//			try {
//				FileWriter fw = new FileWriter(dir, true);
//				BufferedWriter bw = new BufferedWriter(fw);
//				bw.write(e.getKey());
//				bw.write(branch.get(e.getKey()));
//				bw.write(e.getValue());
//				bw.close();
//		    } catch (FileNotFoundException e) {
//		    	e.printStackTrace();
//		    } catch (IOException e) {
//				e.printStackTrace();
//		    }
//

//
//	    try {
//			FileWriter fw = new FileWriter(dir, true);
//			BufferedWriter bw = new BufferedWriter(fw);
//			bw.write(compareBranch);
//			bw.close();
//	    } catch (FileNotFoundException e) {
//	    	e.printStackTrace();
//	    } catch (IOException e) {
//			e.printStackTrace();
//	    }




	}





