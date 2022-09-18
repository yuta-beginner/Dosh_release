package player;

import java.util.ArrayList;
import java.util.Random;

public class Player {
	// 変数
	int playerChip; // プレーヤーのチップの枚数
	ArrayList<Integer> Dices = new ArrayList<Integer>(); // 振ったダイスの値を格納するリスト
	int diceNumberAmount; // 振ったダイスの合計値
	String playerName; // プレーヤーの名前

	// プレーヤーの名前とチップの枚数を引数としたコンストラクタ
	public Player(int playerChip, String playerName) {
		this.playerChip = playerChip;
		this.playerName = playerName;
	}

	//
	public Player() {
	}

	//プレーヤーの名前を取得
	public String getPlayerName() {
		return playerName;
	}
	
	//(debug用)プレーヤーチップのセッター
	public void setPlayerChip(int settedPlayerChip) {
		playerChip = settedPlayerChip;
	}

	// 2個のダイスを振る
	public void setDices() {
		Random rnd = new Random();
		for (int i = 0; i < 2; i++) {
			Integer randomNumber = rnd.nextInt(6) + 1;
			Dices.add(randomNumber);
		}
	}

	// ダイスの目をリセットする。
	public void clearDices() {
		Dices.clear();
	}

	// ダイスの合計値をリセットする。
	public void clearDiceAmount() {
		diceNumberAmount = 0;
	}

	// diceAmountNumber(振ったダイスの合計値)を計算
	public void setDiceNumberAmount() {
		int dicesListSize = Dices.size();
		for(int i = 0; i < dicesListSize; i++) {
			int eachDiceNumber = Dices.get(i);
			diceNumberAmount += eachDiceNumber;
		}
	}

	// diceAmountNumberのゲッター
	public int getDiceNumberAmount() {
		return diceNumberAmount;
	}

	// ダイスの目を確認する。
	public ArrayList<Integer> getDices() {
		return Dices;
	}

	// ゾロ目かどうかのチェック
	public boolean isSameDicesNumber() {
		boolean isSameNumber = false;
		int dicesSize = Dices.size();
		Integer firstDiceNumber = Dices.get(0);
		for(int i = 1; i < dicesSize; i++) {
			Integer nextDiceNumber = Dices.get(i);
			if(firstDiceNumber == nextDiceNumber) {
				isSameNumber = true;
			} else {
				isSameNumber = false;
			}
		}
		return isSameNumber;
	}

	// チップを取る
	public void addPlayerChip(int fieldChip) {
		playerChip += fieldChip;
	}

	// チップの枚数を答える
	public int getPlayerChip() {
		return playerChip;
	}

	// チップを減らす
	public void minusPlayerChip(int fieldChip) {
		playerChip -= fieldChip;
	}
}
