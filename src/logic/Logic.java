package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import constraint.DoshConstraint;
import constraint.MessageConstraint;
import constraint.PropertiesKey;
import display.DisplayManager;
import player.Player;
import player.PlayerUser;
import util.PropertiesUtil;

// interfaceを継承する。
// 違和感に気付くように整えていく（命名・パッケージ・コメントの全角半角等の習慣化）。
// レビューをする際の観点（命名・パッケージ・コメントの全角半角等）を身に付ける。
// プログラムは命名で書く。「変数宣言⇒変数がどう処理される⇒変数がどうなるか」のサイクル。
// 法則性（一部分ミスをしていたら他の部分もチェックする癖（all or nothing））を見る。
public class Logic implements DoshConstraint, PropertiesKey{
	// 変数
	// プレーヤーの人数
	int playerNumber;
	// ○周目
	int doshTurn = 0;
	// チップの初期値
	int startChip;
	private final String playerUserClassName = "PlayerUser";
	String computer;
	// 全プレーヤーのリスト
	ArrayList<Player> playerList = new ArrayList<Player>();
	// ゲーム終了後に所持チップが0枚ではないプレーヤー
	ArrayList<Player> remainedPlayerList = new ArrayList<Player>();
	// プレーヤーの順位

	Map<Integer, List<Player>> playerRank = new HashMap<>();

	// ターンとプレーヤーの配列
	Object[] ojDoshTurn = new Object[2];
	// 2個のダイスの値とその合計値の配列;
	Object[] ojPlayerDice = new Object[3];
	// チップが置かれたマスと置かれたチップの枚数の配列
	Object[] ojChipMasu = new Object[2];
	// チップを取ったマスと取ったチップの枚数の配列
	Object[] ojPlusChip = new Object[2];
	// プレーヤーの名前と所持チップの配列
	Object[] ojPlayerNameChip = new Object[2];
	// 勝者の名前
	int winnerNumber;
	Object[] ojPlayerWinner = new Object[1];

	// マス用のオブジェクト配列
	Object[] masu = new Object[11];
	//
	String[] normalMasuCharacter = new String[9];
	String[] chipMasuCharacter = new String[9];

	// 各マスのチップの枚数
	// 配列にまとめてしまう方がいい。（ヒント）
	// 連続したデータは配列。
	int[] masuChip = new int[9]; 

	// 
	MessageConstraint mc = new MessageConstraint();
	PropertiesUtil pu = new PropertiesUtil();
	DisplayManager dm = new DisplayManager();
	Scanner sc = new Scanner(System.in);

	// 分けることが小回りも効いてくる。（例：start(),process(),finish()で分けるなど)
	// オブジェクト指向＝ディレクトリ構造⇒構造を押さえるということが重要
	// 原則原理を分かった上での、オブジェクト指向
	public void start() {
		// 連続したものはfor文で回せるか
		// Doshのタイトルを表示
		String doshTitle = pu.getValueStringList(DOSH_TITLE_KEY, DOSH_TITLE_START, DOSH_TITLE_END);
		dm.showDoshTitle(doshTitle);
		// Doshのゲーム盤を表示
		String doshDefaultBoard = pu.getValueStringList(DEFAULT_BOARD_KEY, DEFAULT_BOARD_START, DEFAULT_BOARD_END);
		dm.showDefaultBoard(doshDefaultBoard);
		// ゲームのルール概要を表示
		String doshRuleAbstract = pu.getValueStringList(RULE_ABSTRACT_KEY, RULE_ABSTRACT_START, RULE_ABSTRACT_END);
		dm.showRuleAbstract(doshRuleAbstract);

		// ゲームのルール詳細を表示
		String doshRuleDetail = pu.getValueStringList(RULE_DETAIL_KEY, RULE_DETAIL_START, RULE_DETAIL_END);
		dm.showRuleDetail(doshRuleDetail);

		// 特別ルールを表示
		String doshSpecialRule = pu.getValueStringList(SPECIAL_RULE_KEY, SPECIAL_RULE_START, SPECIAL_RULE_END);
		dm.showSpecialRule(doshSpecialRule);
		
		// ゲーム開始のメッセージを表示
		String startMessage = pu.getValueString(START_MESSAGE_KEY);
		dm.showStartMessage(startMessage);
		
		// 参加プレイヤーの人数を入力
		setPlayerNumber();
	    while(playerNumber < 2) {
	    	String correctPlayerNumberMessage = pu.getValueString(PLAYER_NUMBER_KEY);
	    	dm.showCorrectPlayerNumber(correctPlayerNumberMessage);
	    	setPlayerNumber();
	    }
		// プレイヤーとコンピューターを作り、playerListにセットする。
		computer = pu.getValueString(COMPUTER_KEY);
		startChip = pu.getValueInt(START_CHIP_KEY);
		//(debug用)startChipの枚数を出力する。
		//System.out.println("ゲーム開始時のチップは" + startChip + "です。");
		setPlayers(computer, startChip);

		// (debug用)Playerリストがセットされているか
		/*System.out.print("debug用:");
		for(int i = 0; i < playerList.size(); i++) {
			Player player = playerList.get(i);
			String playerName = player.getPlayerName();
			System.out.print(playerName + " ");
		}
		System.out.println();*/

		// playerListをシャッフルする。
		Collections.shuffle(playerList);

		// (debug用)Playerリストがシャッフルされているか
		/*System.out.print("debug用:");
		for(int i = 0; i < playerList.size(); i++) {
			Player player = playerList.get(i);
			String playerName = player.getPlayerName();
			System.out.print(playerName + " ");
		}
		System.out.println();*/

		// チップが置いていない場合に表示する数字を配列にセット。
		int normalMasuCharacterLength = normalMasuCharacter.length;
		for(int i = 0; i < normalMasuCharacterLength; i++) {
			int propertyNormalNumber = i + 1;
			normalMasuCharacter[i] = pu.getValueString(NORMAL_NUMBER_KEY + propertyNormalNumber);
		}

		// (debug用)チップが置いていない場合に表示する数字を配列にセット。
		/*System.out.print("debug用:");
		for(int i = 0; i < normalMasuCharacterLength; i++) {
			System.out.print(normalMasuCharacter[i]);
		}*/

		System.out.println();

		// チップが置いてある場合に表示する数字を配列にセット。
		int chipMasuCharacterLength = chipMasuCharacter.length;
		for(int i = 0; i < chipMasuCharacterLength; i++) {
			int propertyChipNumber = i + 1;
			chipMasuCharacter[i] = pu.getValueString(CHIP_NUMBER_KEY + propertyChipNumber);
		}

		// (debug用)チップが置いてある場合に表示する数字を配列にセット
		/*System.out.print("debug用:");
		for(int i = 0; i < chipMasuCharacterLength; i++) {
			System.out.print(chipMasuCharacter[i]);
		}*/

		System.out.println();

		// 各マスに初期値0枚のチップをセット
		int masuChipLength = masuChip.length;
		for(int i = 0; i < masuChipLength; i++) {
			masuChip[i] = 0;
		}

		//(debug用)3のマスに5枚チップ置く。
		//masuChip[0] = 5;

		// Doshスタートのメッセージを出力
		String startDoshPlay = pu.getValueString(START_DOSH_PLAY_KEY);
		dm.showDoshStart(startDoshPlay);

		// 各プレーヤーの順番を表示
		int playerListSize = playerList.size();
		for(int i = 0; i < playerListSize; i++){
			int playerTurn = i + 1;
			String eachPlayerName = playerList.get(i).getPlayerName();
			String turn = pu.getValueString(TURN_KEY);
			dm.showPlayerTurn(playerTurn, turn, eachPlayerName);
		}

		System.out.println();

		// Press Enterを表示。
		String pressEnter = pu.getValueString(PRESS_ENTER_KEY);
		dm.showPressEnter(pressEnter);
		sc.nextLine();

		//System.out.println("start終了");// (debug用)

		process();
	}

	public void process() {
		int playerListSize = playerList.size();
		boolean isRepeatGame = true;
		while(isRepeatGame) {
			// 〇週目をカウントアップ
			doshTurn = doshTurn + 1;

			loop:for(int i = 0; i < playerListSize; i++) {
				// そのターンのプレーヤーを取得。
				Player player = playerList.get(i);
				// そのターンのプレーヤー名を取得。
				String playerName = player.getPlayerName();
				// プロパティファイルに渡すためのojDoshTurn配列に値をセット。
				setOjDoshTurn(doshTurn, playerName);
				// プロパティファイルから取得。
				String turnMessage = pu.getValueFormattedString(TURN_MESSAGE_KEY, ojDoshTurn);
				// 〇週目と誰の順番なのかを表示
				dm.showTurnMessage(turnMessage);

				setMasu();
				String board = pu.getValueFormattedString(BOARD_KEY, masu);
				System.out.println();

				dm.showBoard(board);
				
				
				String playerClass = player.getClass().getSimpleName();
				//System.out.println(playerClass);
				if (playerClass.equals(playerUserClassName)) {
					//(debug用)
					/*int debugPlayerChip = 1;
					player.setPlayerChip(debugPlayerChip);*/
					
					PlayerUser playerUser = (PlayerUser)player;

					String selectCheatMessage = pu.getValueString(PLAYER_USER_SELECT_CHEAT_KEY);
					String choiceMessage = pu.getValueString(PLAYER_USER_CHOICE_MESSAGE_KEY);
					dm.showCheatSelect(selectCheatMessage);
					dm.showChoiceMessage(choiceMessage);
					int playerUserSelect = playerUser.selectCheat();

					while(playerUserSelect > 1 || playerUserSelect < 0) {
						String choiceAgainMessage = pu.getValueString(PLAYER_USER_CHOICE_AGAIN_MESSAGE_KEY);
						dm.showChoiceAgainMessage(choiceAgainMessage);
						dm.showChoiceMessage(choiceMessage);
						playerUserSelect = playerUser.selectCheat();
					}

					if(playerUserSelect == 0) { // イカサマを選択した場合の処理
						//System.out.println("debug用：「イカサマをする」を選択しました。");
						String cheatMessage = pu.getValueString(PLAYER_USER_CHEAT_MESSAGE_KEY);
						String cheatMessageDetail = pu.getValueString(PLAYER_USER_CHEAT_MESSAGE_DETAIL_KEY);
						dm.showCheatMessage(cheatMessage);
						dm.showCheatMessageDetail(cheatMessageDetail);
						int cheatType = playerUser.selectCheatType();

						while(cheatType > 2 || cheatType < 0) {
							String cheatMessageAgain = pu.getValueString(PLAYER_USER_CHEAT_MESSAGE_AGAIN_KEY);
							dm.showCheatMessageAgain(cheatMessageAgain);
							dm.showCheatMessageDetail(cheatMessageDetail);
							cheatType = playerUser.selectCheat();
						}

						playerUser.playCheat(cheatType);
					} else { // イカサマをしないを選択した場合の処理
						//System.out.println("debug用：「イカサマをしない」を選択しました。");
						playerUser.setDices();
						playerUser.setDiceNumberAmount();
					}
				} else {
					player.setDices();
					player.setDiceNumberAmount();
				}

				ArrayList<Integer> playerDices = player.getDices();
				int playerDiceNumberAmount = player.getDiceNumberAmount();

				// ダイスを振った後の、出た目と出た目の合計メッセージ
				setOjPlayerDice(playerDices, playerDiceNumberAmount);
				String playerDiceMessage = pu.getValueFormattedString(PLAYER_DICE_MESSAGE_KEY, ojPlayerDice);
				dm.showPlayerDiceMessage(playerDiceMessage);
				System.out.println();

				// 出た目の合計と同じマスにチップが置かれているかのチェック
				boolean isMasuChip = false;
				if(playerDiceNumberAmount > 2 && playerDiceNumberAmount < 12 ) {
					isMasuChip = isMasuChip(playerDices);
				}
				//System.out.println(isMasuChip);


				if(isMasuChip && playerDiceNumberAmount != 7) {
					int playerGetChip = reduceChipFromMasu(playerDiceNumberAmount);
					player.addPlayerChip(playerGetChip);

					// チップを取ったマスと取ったチップの枚数を表示
					setOjPlusChip(playerDiceNumberAmount, playerGetChip);
					String plusChipMessage = pu.getValueFormattedString(CHIP_GET_MESSAGE_KEY, ojPlusChip);
					dm.showPlusChipMessage(plusChipMessage);

				} else if(playerDiceNumberAmount == 2 || playerDiceNumberAmount == 12){
					int masuAllChip = reduceAllChip();
					player.addPlayerChip(masuAllChip);

					// 総取りのメッセージを表示
					String plusAllChipMessage = pu.getValueString(CHIP_GET_ALL_MESSAGE_KEY);
					dm.showPlusAllChipMessage(plusAllChipMessage);

					// プレイヤーのダイスとダイスの合計をリセットする。
					player.clearDices();
					player.clearDiceAmount();
					
					// ボードを表示
					setMasu();
					String resultBoard = pu.getValueFormattedString(BOARD_KEY, masu);
					System.out.println();
					dm.showBoard(resultBoard);
					
					// 各プレーヤーの所持チップを表示
					String allPlayerChip = pu.getValueString(PLAYER_CHIP_KEY);
					dm.showAllPlayerChip(allPlayerChip);
					System.out.println();
					for(int j = 0; j < playerListSize; j++) {
						int eachPlayerChip = playerList.get(j).getPlayerChip();
						String eachPlayerName = playerList.get(j).getPlayerName();
						setOjPlayerNameChip(eachPlayerName, eachPlayerChip);
						String doshArrow = pu.getValueFormattedString(ARROW_KEY, ojPlayerNameChip);
						dm.showPlayerChip(doshArrow);
					}
					System.out.println();

					// 「もう一度、ダイスを振ります。」のメッセージを表示。
					String playerDiceAgainMessage = pu.getValueString(PLAYER_DICE_AGAIN_MESSAGE_KEY);
					dm.showPlayerDiceAgainMessage(playerDiceAgainMessage);
					System.out.println();
					
					// Press Enterを表示。
					String pressEnter = pu.getValueString(PRESS_ENTER_KEY);
					dm.showPressEnter(pressEnter);
					sc.nextLine();

					// ダイスを振る・出た目の合計値をセットする。
					player.setDices();
					player.setDiceNumberAmount();

					// もう一度振った後の、出た目と出た目の合計メッセージ
					ArrayList<Integer> playerSecondDices = player.getDices();
					int playerSecondDiceAmount = player.getDiceNumberAmount();
					setOjPlayerDice(playerSecondDices, playerSecondDiceAmount);
					playerDiceMessage = pu.getValueFormattedString(PLAYER_DICE_MESSAGE_KEY, ojPlayerDice);
					dm.showPlayerDiceMessage(playerDiceMessage);

					// 出た目がゾロ目かどうかのチェック
					boolean isSameDicesNumber = player.isSameDicesNumber();
					
					System.out.println();


					if(isSameDicesNumber) { // ゾロ目だった場合
						int playerAllChip = player.getPlayerChip();
						setChipToAllMasu(playerAllChip);
						int allMasuChip = getAllMasuChip();
						player.minusPlayerChip(allMasuChip);
						String setAllChipMessage = pu.getValueString(CHIP_SET_ALL_MESSAGE_KEY);
						dm.showSetAllChipMessage(setAllChipMessage);
						System.out.println();
					} else { // ゾロ目でなかった場合
						int playerAllChip = player.getPlayerChip();
						//int playerSecondAmount = player.getDiceNumberAmount();
						int playerMinusChip = setChipToMasu(playerSecondDiceAmount, playerAllChip);
						//(debug用)
						//System.out.println("debug用：2回目のダイスを振った結果、" + playerSecondDiceAmount + "のマスに" + playerMinusChip + "枚置きました");
						//int playerMinusChip = getMasuChip(playerSecondDiceAmount);
						player.minusPlayerChip(playerMinusChip);

						//7が出た時の「牢獄に入りました。」のメッセージを表示
						if(playerSecondDiceAmount == 7) {
							String masuSevenMessage = pu.getValueString(CHIP_SEVEN_MASU_MESSAGE_KEY);
							dm.showMasuSevenMessage(masuSevenMessage);
						}

						// チップを置いたマスと置いたチップの枚数を表示
						setOjChipMasu(playerSecondDiceAmount, playerMinusChip);
						String chipMasuMessage = pu.getValueFormattedString(CHIP_MASU_MESSAGE_KEY, ojChipMasu);
						dm.showChipMasuMessage(chipMasuMessage);

						//(debug用)2回目のダイスを振って、ゾロ目ではなかった時のプレーヤーの枚数
						//System.out.println("2回目のダイスを振った後のプレイヤーの2個のダイスの合計の数は" + player.getDiceNumberAmount() + "です。");
						System.out.println();
						//(debug用)2回目のダイスを振って、プレイヤーユーザーが特定のマスにチップを置いた後の各マスのチップ枚数
						/*for(int j = 0; j < masuChip.length; j++) {
							System.out.print(masuChip[j] + " ");
						}
						System.out.println();*/
					}
				} else {
					int playerAllChip = player.getPlayerChip();
					int playerMinusChip = setChipToMasu(playerDiceNumberAmount, playerAllChip);
					//System.out.println("debug用：ダイスを振った結果、" + playerDiceNumberAmount + "のマスに" + playerMinusChip + "枚置きました");

					//7が出た時の「牢獄に入りました。」のメッセージを表示
					if(playerDiceNumberAmount == 7) {
						String masuSevenMessage = pu.getValueString(CHIP_SEVEN_MASU_MESSAGE_KEY);
						dm.showMasuSevenMessage(masuSevenMessage);
					}

					// チップを置いたマスと置いたチップの枚数を表示
					setOjChipMasu(playerDiceNumberAmount, playerMinusChip);
					String chipMasuMessage = pu.getValueFormattedString(CHIP_MASU_MESSAGE_KEY, ojChipMasu);
					dm.showChipMasuMessage(chipMasuMessage);
					player.minusPlayerChip(playerMinusChip);
				}


				//(debug用)masuChipを表示
				/*for(int j = 0; j < masuChip.length; j++) {
					System.out.print(masuChip[j] + " ");
				}*/

				//(debug用)そのターンのプレーヤーのチップを表示
				/*int playerChip = player.getPlayerChip();
				System.out.println(playerName + "のチップ枚数は、" + playerChip + "です。");*/

				System.out.println();

				setMasu();
				String resultBoard = pu.getValueFormattedString(BOARD_KEY, masu);
				System.out.println();

				dm.showBoard(resultBoard);

				// 各プレーヤーのダイスをクリア
				player.clearDices();
				// 各プレーヤーダイスの合計値をクリア
				player.clearDiceAmount();

				//(debug用)playerUserのチップを強制的に0にする。
				/*if(playerName.equals("あなた")) {
					playerChip = 0;
					player.setPlayerChip(playerChip);
				}*/

				// 各プレーヤーのチップを表示
				for(int j = 0; j < playerListSize; j++) {
					int eachPlayerChip = playerList.get(j).getPlayerChip();
					String eachPlayerName = playerList.get(j).getPlayerName();
					setOjPlayerNameChip(eachPlayerName, eachPlayerChip);
					String doshArrow = pu.getValueFormattedString(ARROW_KEY, ojPlayerNameChip);
					dm.showPlayerChip(doshArrow);
				}

				for(int j = 0; j < playerListSize; j++) {
					int eachPlayerChip = playerList.get(j).getPlayerChip();
					if(eachPlayerChip <= 0) {
						isRepeatGame = false;
						break loop;
					}
				}
				
				System.out.println();

				// Press Enterを表示。
				String pressEnter = pu.getValueString(PRESS_ENTER_KEY);
				dm.showPressEnter(pressEnter);
				sc.nextLine();
			}
		}
		//(debug用)for文・while文抜けたかどうかチェック
		//System.out.println("debug用：process()が終了しました。");
		finish();
	}

	public void finish() {
		//(debug用)finish()に入ったかどうかチェック
		//System.out.println("debug用：finish()に入りました。");
		
		//GAME OVERを表示する。
		System.out.println();
		String gameOverMessage = pu.getValueString(DOSH_GAME_OVER_KEY);
		dm.showDoshGameOverMessage(gameOverMessage);
		System.out.println();

		//0枚のプレーヤー以外を残ったプレーヤーのリストとして格納。
		setRemainedPlayers(playerList);

		// プレーヤーの順位を決めるメソッド
		int remainedPlayerListSize = remainedPlayerList.size();
		for(int i = 0; i < remainedPlayerListSize; i++) {
			Player remainedPlayer = remainedPlayerList.get(i);
			Integer remainedPlayerRank = 1;
			int remainedPlayerChip = remainedPlayer.getPlayerChip();
			for(int j = 0; j < remainedPlayerListSize; j++) {
				Player comparisonPlayer = remainedPlayerList.get(j);
				int comparisonPlayerChip = comparisonPlayer.getPlayerChip();
				if(remainedPlayerChip < comparisonPlayerChip) {
					remainedPlayerRank ++;
					//(debug用)プレイヤーのランクがカウントアップされたことを出力。
					//System.out.println("debug用：" + remainedPlayer.getPlayerName() + "のランクが" + remainedPlayerRank + "位になりました。");
				}
			}
			List<Player> winners;
			try {
				winners = playerRank.get(remainedPlayerRank);
				winners.add(remainedPlayer);
			} catch(Exception e) {
				//System.out.println("debug用：リストを取得できませんでした。");
				winners = new ArrayList<Player>();
				winners.add(remainedPlayer);
			}
			//winners.add(remainedPlayer);
			playerRank.put(remainedPlayerRank, winners);
			//playerRank.put(remainedPlayerRank, remainedPlayer);
		}
		//(debug用)残ったプレーヤーの順位と名前を出力する。
		/*try {
			List<Player> winner1 = playerRank.get(1);
			for(int i = 0; i < winner1.size(); i++) {
				Player eachPlayer = winner1.get(i);
				System.out.println("1位のプレーヤーは、" + eachPlayer.getPlayerName() + "です。");
			}
		} catch(Exception e) {
			System.out.println("1位のプレーヤーはいません。");
		}
		try {
			List<Player> winner2 = playerRank.get(2);
			for(int i = 0; i < winner2.size(); i++) {
				Player eachPlayer = winner2.get(i);
				System.out.println("2位のプレーヤーは、" + eachPlayer.getPlayerName() + "です。");
			}
		} catch(Exception e) {
			System.out.println("2位のプレーヤーはいません。");
		}
		try {
			List<Player> winner3 = playerRank.get(3);
			for(int i = 0; i < winner3.size(); i++) {
				Player eachPlayer = winner3.get(i);
				System.out.println("3位のプレーヤーは、" + eachPlayer.getPlayerName() + "です。");
			}
		} catch(Exception e) {
			System.out.println("3位のプレーヤーはいません。");
		}*/
		
		System.out.println();
		
		List<Player> winner1 = playerRank.get(1);
		winnerNumber = winner1.size();
		for(int i = 0; i < winnerNumber; i++) {
			ojPlayerWinner[0] = winner1.get(i).getPlayerName();
			String winnerResult = pu.getValueFormattedString(PLAYER_WINNER_KEY, ojPlayerWinner);
			dm.showWinner(winnerResult);
		}
	}

	// プレーヤー人数を設定するメソッド
	private void setPlayerNumber() {
		// 「プレーヤーの人数を入力してください。」と表示
		// Scannerで人数を入力できるようにする。
		Scanner scan = new Scanner(System.in);
		playerNumber = scan.nextInt();
	}

	// プレイヤーとコンピューターを作るメソッド
	private void setPlayers(String otherPlayer,int initialChip) {
		for(int i = 0; i < playerNumber; i++) {
			if(i == 0) {
				PlayerUser playerUser = new PlayerUser(initialChip);
				playerList.add(playerUser);
			} else {
				String computerName = otherPlayer + i;
				Player computerPlayer = new Player(initialChip, computerName); 
				playerList.add(computerPlayer);
			}
		}
	}

	// ゲーム終了後残ったプレーヤーをセットするメソッド
	private void setRemainedPlayers(ArrayList<Player> playerList) {
		int playerListSize = playerList.size();
		for(int i = 0; i < playerListSize; i++) {
			Player afterGamePlayer =  playerList.get(i);
			int afterGamePlayerChip = afterGamePlayer.getPlayerChip();
			//(debug用)各プレーヤーのゲーム終了後のチップ枚数を出力。
			//System.out.println(afterGamePlayer.getPlayerName() + "のチップ枚数は、" + afterGamePlayerChip + "枚です。");
			if(afterGamePlayerChip > 0) {
				remainedPlayerList.add(afterGamePlayer);
				//(debug用)remainedPlayerListに加えたプレーヤーの名前を出力。
				//System.out.println(afterGamePlayer.getPlayerName() + "をremainedPlayerListに加えました。");
			} /*else {
				//(debug用)remainedPlayerListに加えなかったプレーヤーの名前を出力。
				//System.out.println(afterGamePlayer.getPlayerName() + "をremainedPlayerListに加えませんでした。");
			}*/
		}
	}

	// そのマスにチップがあるか答える
	public boolean isMasuChip(ArrayList<Integer> playerDices) {
		boolean isMasuChip = false;
		int masuNumber = 0;
		int diceListSize = playerDices.size();
		int diceNumberAmount = 0;

		for(int i = 0; i < diceListSize; i++) {
			int eachDiceNumber = playerDices.get(i);
			diceNumberAmount = diceNumberAmount + eachDiceNumber;
		}

		switch(diceNumberAmount) {
		case 7:
			masuNumber = masuChip[5];
			break;
		case 8:
			masuNumber = masuChip[4];
			break;
		default:
			int masuChipNumber = diceNumberAmount - 3;
			masuNumber = masuChip[masuChipNumber];
		}

		if (masuNumber > 0) {
			isMasuChip = true;
		}
		return isMasuChip;
	}

	// 各マスにチップを置く
	public int setChipToMasu(int diceNumberAmount, int playerChip) {
		int originalDiceNumberAmount = diceNumberAmount;
		if(playerChip < diceNumberAmount) {
			diceNumberAmount = playerChip;
		}
		switch(originalDiceNumberAmount) {
		case 7:
			masuChip[5] += diceNumberAmount;
			break;
		case 8:
			masuChip[4] += diceNumberAmount;
			break;
		default:
			int masuChipNumber = originalDiceNumberAmount - 3;
			masuChip[masuChipNumber] += diceNumberAmount;
		}

		return diceNumberAmount;
	}

	// 各マスチップの総置き
	public void setChipToAllMasu(int playerChip) {
		int masuChipLength = masuChip.length;
		for(int i = 0; i < masuChipLength; i++) {
			switch(i) {
			case 4:
				masuChip[i] = 8;
				break;
			case 5:
				masuChip[i] = 7;
				break;
			default:
				int masuChipNumber = i + 3;
				masuChip[i] = masuChipNumber;
				break;
			}
			int playerOriginalChip = playerChip;
			playerChip -= masuChip[i];
			if(playerChip <= 0) {
				masuChip[i] = playerOriginalChip;
				playerChip = playerOriginalChip;
				playerChip -= masuChip[i];
				break;
			}
		}
	}

	// 各マスのチップの全合計枚数を取得
	public int getAllMasuChip() {
		int allMasuChip = 0;
		int masuChipLength = masuChip.length;
		for(int i = 0; i < masuChipLength; i++) {
			allMasuChip += masuChip[i];
		}

		return allMasuChip;
	}

	// 各マスからチップを取る
	public int reduceChipFromMasu(int diceNumberAmount) {
		int fieldChip = 0;
		switch(diceNumberAmount) {
		case 7:
			break;
		case 8:
			fieldChip = masuChip[4];
			masuChip[4] -= fieldChip;
			break;
		default:
			int masuChipNumber = diceNumberAmount - 3;
			fieldChip = masuChip[masuChipNumber];
			masuChip[masuChipNumber] -= fieldChip;
		}
		return fieldChip;
	}

	// 各マスのチップの総取り
	public int reduceAllChip() {
		int masuAllChip = 0;
		int masuChipLength = masuChip.length;
		for(int i = 0; i < masuChipLength; i++) {
			int eachMasuChip = masuChip[i];
			masuChip[i] -= eachMasuChip;
			masuAllChip += eachMasuChip;
		}
		return masuAllChip;
	}

	// ojDoshTurnのセッター
	private void setOjDoshTurn(int doshTurn, String playerName) {
		ojDoshTurn[0] = (Object)doshTurn;
		ojDoshTurn[1] = (Object)playerName;
	}

	// masuのセッター
	private void setMasu() {
		int masuLength = masuChip.length;
		for(int i = 0; i < masuLength; i++) {
			if(masuChip[i] == 0) {
				masu[i] = normalMasuCharacter[i];
			} else {
				masu[i] = chipMasuCharacter[i];
			}
		}
		if(masuChip[5] == 0) {
			String space = pu.getValueString(BOARD_SPACE_KEY);
			masu[9] = space;
			masu[10] = space;
		} else {
			String times = pu.getValueString(BOARD_TIMES_KEY);
			masu[9] = times;
			Integer sevenTimes = masuChip[5] / 7;
			String stSevenTimes = sevenTimes.toString();
			if(sevenTimes < 10) {
				String fullStSevenTimes = half2Full(stSevenTimes);
				masu[10] = fullStSevenTimes;
			} else {
				masu[10] = stSevenTimes;
			}
			
		}
	}

	public String half2Full(String strDigit) {
		if (strDigit == null) {
			throw new IllegalArgumentException();
		}
		StringBuffer sb = new StringBuffer(strDigit);
		for (int i = 0; i < strDigit.length(); i++) {
			char c = strDigit.charAt(i);
			if ('0'<= c && c <= '9') {
				sb.setCharAt(i, (char) (c - '0' + '０'));
			}
		}
		return sb.toString();
	}

	//ojPlayerDiceのセッター
	private void setOjPlayerDice(ArrayList<Integer> playerDices, int diceNumberAmount) {
		int playerDicesSize = playerDices.size(); 
		for(int i = 0; i < playerDicesSize; i++) {
			ojPlayerDice[i] = playerDices.get(i);
		}
		int lastNumber = ojPlayerDice.length - 1;
		ojPlayerDice[lastNumber] = diceNumberAmount; 
	}

	//ojChipMasuのセッター
	private void setOjChipMasu(int masu, int minusChip) {
		ojChipMasu[0] = masu;
		ojChipMasu[1] = minusChip;
	}

	// ojPlusChipのセッター
	private void setOjPlusChip(int masu, int plusChip) {
		ojPlusChip[0] = masu;
		ojPlusChip[1] = plusChip;
	}
	
	// ojPlayerNameChipのセッター
	private void setOjPlayerNameChip(String playerName, int playerChip) {
		ojPlayerNameChip[0] = playerName;
		ojPlayerNameChip[1] = playerChip;
	}
}
