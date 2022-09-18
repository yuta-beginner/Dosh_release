package player;

import java.util.Random;
import java.util.Scanner;

import constraint.DoshConstraint;

public class PlayerUser extends Player implements DoshConstraint{

	private final String userName = "あなた";

	public PlayerUser(int userChip) {
		this.playerChip = userChip;
		this.playerName = userName;
	}

	// イカサマするか選択する
	public int selectCheat() {
		int userCheat = 0;
		Scanner scanner = new Scanner(System.in);
		userCheat = scanner.nextInt();
		return userCheat;
	}

	// イカサマの種類を選択
	public int selectCheatType() {
		int userCheatType = 0;
		Scanner scanner = new Scanner(System.in);
		// デバッグ用メッセージ
		//System.out.println("イカサマするか選択してください。(0～2を入力)");
		//System.out.println("0 : ゾロ目を出す" + " " + "1 : 7以下の目を出す" + " " + "2：7以上の目を出す");

		// イカサマの種類を入力して選択
		userCheatType = scanner.nextInt();

		/*while(userCheatType > 2 || userCheatType < 0) {
			System.out.println("0～2を入力してください");
			System.out.println("0 : ゾロ目を出す" + " " + "1 : 7以下の目を出す" + " " + "2：7以上の目を出す");
			userCheatType = scanner.nextInt();
		}*/

		return userCheatType;
	}

	// ゾロ目を出す
	public void setSameDiceNumber() {
		Random rnd = new Random();
		Integer randomNumber = rnd.nextInt(6) + 1;
		for(int i = 0; i < 2; i++) {
			Dices.add(randomNumber);
		}
		/*for (int i = 0; i < 2; i++) {
			Integer diceNumber = Dices.get(i);
			int dice = i + 1;
			System.out.println("ダイス" + dice + "個目は" + diceNumber + "です。");
		}*/
		setDiceNumberAmount();
		//System.out.println("合計値は" + diceNumberAmount + "です");
	}

	// 7以下の目を出す
	public void setUnderSeven() {
		setDices();
		setDiceNumberAmount();
		while(diceNumberAmount > 7) {
			clearDices();
			clearDiceAmount();
			setDices();
			setDiceNumberAmount();
		}
	}

	// 7以上の目を出す
	public void setOverSeven() {
		setDices();
		setDiceNumberAmount();
		while(diceNumberAmount < 7) {
			clearDices();
			clearDiceAmount();
			setDices();
			setDiceNumberAmount();
		}
	}

	// PlayerUser選択後の流れ
	public void playCheat(int playerUserCheatType) {
		switch(playerUserCheatType) {
		case 0:
			setSameDiceNumber();
			break;
		case 1:
			setUnderSeven();
			break;
		case 2:
			setOverSeven();
			break;
		}		
	}
}