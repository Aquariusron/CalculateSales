package jp.co.alh.akane_yamashita.Calculate_Sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Caluculate_Sales {

	public static void main(String[] args) {

		HashMap<String,String> branchLists = new HashMap<String,String>();
		HashMap<String,String> commodityLists = new HashMap<String,String>();
		// TODO 自動生成されたメソッド・スタブ

		// コマンドライン引数が1つ以上もしくは以下だったとき
		if(args.length != 1) {
			System.out.println("予期せぬエラーが発生しました");
		}

		// finallyでcloseしておきたいのでスコープ外でnullを入れておく
		BufferedReader bfr = null;

		// 支店定義ファイル読み込み
		try {
				File file = new File(args[0] + "\\branch.lst");
				fileExists(file);

				// 順序を忘れた
				/*InputStream is = new InputStream(file);
				BufferedInputStream bf = new BufferedInputStream(is);
				BufferedReader bfr = new BufferedReader(bf);
				 */

				FileReader fr = new FileReader(file);
				bfr = new BufferedReader(fr);

				// 1行ずつファイルを読み込む
				// 読み込む→splitする
				String[] codes;
				String ss;
				while((ss = bfr.readLine()) != null) {
					codes = ss.split(",");

				// ついでに桁数チェックをする
					if(codes.length != 2) {
						System.out.println("ファイルフォーマットが不正です");
						break;
					}

				// 支店コード数字3桁固定
				// 支店名はカンマと改行なし文字列

				// lengthで改行されているorカンマ混入の可能性を排除している(いずれも0か1それ以外の要素はひっかからない)
				// 正規表現3桁固定＆文字列かどうかを判定
						// 頭と末尾から3桁固定の数字かを見る // 文字列かを判断 → 数字かアルファベットの可能性を排除する
						if(!codes[0].matches("^\\d{3}$")) {
							// 下記sysoutデバッグ用
							System.out.println(codes[0]);
							System.out.println("ファイルフォーマットが不正です");
							break;
						}
					// マップに要素を追加
					branchLists.put(codes[0],codes[1]);
				}


				/*
				 *
				正規表現で桁数、文字列かどうかのチェック
				branch.lst:支店コードは数字3桁固定,支店名はカンマと改行なし文字列
				commodity.lst:商品定義コードは8桁,アルファベットと数字
					商品名はカンマ改行なし文字列
				000001.rcd(売り上げファイル)：支店コード数字3桁固定
				商品コード8桁,アルファベットと数字
				売上額：数字最大10桁

				試しに頭が数字もしくは大文字英字、結が3桁固定のif文
				ex : if(!^[0-9*A-Z]{3}$) {
					System.out.println("指定の表現と違います");
				}
				*/

			 	// マップに突っ込む
				// branchLists.put(1,"あいうえお");

		} catch(FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException f) {
			f.printStackTrace();

		} finally {
			System.out.println(branchLists);

			if(bfr != null) {
				try {
					bfr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}




		// 商品定義ファイル読み込み



		// 売上げファイルの読み込み
		/*
		 * ファイル名.rcdで8桁固定であるかチェック
		 * 連番チェック（番号が連続しているか）
		 * ファイルの中身が4桁以上→処理を抜ける
		 *
		 *
		 */


		// 集計（計算）



		// 集計結果出力（ファイルをはく）
//		File file = new File(args[0]);
//		file.createNewFile();


		// エラーメッセージ
		/*
		 * なんかあったら予期せぬエラーが発生しましたとか書いとく
		 */
	}

	public static void fileExists (File file) {
		if(!file.exists()) {
			System.out.println("支店定義ファイルが存在しません");
			if(!(file.isFile() && file.canRead())) {
				System.out.println("支店定義ファイルのフォーマットが不正です");
			}
		}
	}

}
