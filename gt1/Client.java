package gt1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

import java.util.List;
import java.util.Map.Entry;

import lenz.htw.blocks.Move;
import lenz.htw.blocks.net.NetworkClient;

public class Client {

	public static HashMap<Integer, int[]> matchfieldNeighbors = new HashMap<Integer, int[]>();

	public static List<Node> matchfieldNodes = new ArrayList<Node>();

	public static MiniMax miniMaxCurrentPlayer;

	public static boolean isMaxPlayer;

	public static int delCount = 0;
	public static int validMoves = -2;

	public static void main(String[] args) throws IOException {

		HashMap<String, Integer> token = new HashMap<String, Integer>();

		Node currentNode_1 = null;
		Node currentNode_2 = null;

		int delete = 1;
		int playerNr = 0;

		Move move = new Move(0, 0, 0, 0);
		Move recMove = new Move(0, 0, 0, 0);

		boolean firstMove = true;

		NetworkClient client = new NetworkClient("127.0.0.1", "TheLaserLine" + Math.random() * Math.random() * 100,
				ImageIO.read(new File("src/img/Logo.jpg")));

		//Erstellen des Spielfeldes
		putAllNeighbors(getMatchfield());

		//Erstellen der Start Konfiguration
		getStartConfiguration(token);

		//Erstellen der Node Liste des Spielfeldes
		createNodes(matchfieldNeighbors, matchfieldNodes, token);

		while (true) {
			System.out.println("Player " + client.getMyPlayerNumber());
			playerNr = client.getMyPlayerNumber();

			System.out.println("Token Keys " + token.keySet());
			System.out.println("Token Values " + token.values());

			while ((recMove = client.receiveMove()) != null || firstMove) {

				if (recMove != null) {
					delCount = updateMatchfield(token, delCount, recMove);
				}

				if (playerNr == 0) {
					System.out.println("- - - PLAYER 0 - - -");
					playerIsPlayerZero(delete, move, token, matchfieldNeighbors, client, currentNode_1, currentNode_2);

				}
				if (playerNr == 1) {
					System.out.println("- - - PLAYER 1 - - -");
					playerIsPlayerOne(delete, move, token, matchfieldNeighbors, client, currentNode_1, currentNode_2);

				}

				if (playerNr == 2) {
					System.out.println("- - - PLAYER 2 - - -");
					playerIsPlayerTwo(delete, move, token, matchfieldNeighbors, client, currentNode_1, currentNode_2);

				}

				firstMove = false;
			}
			if (recMove == null) {
				validMoves = validMoves + 2;
				System.out.println("Spieler " + playerNr + " hat " + validMoves + " valide moves");
			}

		}
	}

	public static HashMap<Integer, int[]> getMatchfield() {
		return matchfieldNeighbors;
	}

	/*
	 * 
	 * Mit dieser Funktion bekommt man die aktuelle Position eines Spielsteines auf
	 * dem Spielfeld
	 *
	 */

	public static int getCurrentPosition(String tokenName, HashMap<String, Integer> token) {
		int tokenPosition;
		tokenPosition = token.get(tokenName);
		System.out.println("Current Token Position: " + tokenPosition);
		return tokenPosition;
	}

	/*
	 *
	 * In dieser Methode wird die Node Liste des SPielfeldes erstellt indem die
	 * Hashmaps in eine Liste eingefügt werden.
	 *
	 */
	public static void createNodes(HashMap<Integer, int[]> matchfield, List<Node> matchfieldNodes,
			HashMap<String, Integer> token) {

		for (Integer key : matchfield.keySet()) {
			System.out.println("KEY: " + key);
			int k = key.intValue();
			int[] v = matchfield.get(key);
			Node node = new Node(k, v);

			if (node.index == 24) {
				node.setIsOccupiedBy("delete");
			}
			matchfieldNodes.add(node);

		}

		updateMatchfieldNodes(matchfieldNodes, token);

		/*
		 * for (Entry<String, Integer> entry : token.entrySet()) {
		 * 
		 * int value = ((entry.getValue().intValue() - 1));
		 * System.out.println("-----------Entry: " + value + "------------");
		 * 
		 * matchfieldNodes.get(value).isOccupiedBy = entry.getKey();
		 * 
		 * if (value > 26) {
		 * 
		 * value = (entry.getValue().intValue() - 2);
		 * System.out.println("HALLOO MOIN IHC BIN HIER !!!!!" + value);
		 * matchfieldNodes.get(value - 2).isOccupiedBy = entry.getKey(); }
		 * 
		 * else {
		 * 
		 * // value = ((entry.getValue().intValue()) - 1); // }
		 * 
		 * System.out.println("create Nodes - - - Node " +
		 * matchfieldNodes.get(value).index + " is Occupied by " + entry.getKey());
		 * 
		 * }
		 */

	}

	/*
	 * 
	 * Diese Methode wird verwendet wenn der Spieler mit der Nummer 2 der aktuelle
	 * Clienbt Spieler ist, hier werden zwei Objekte der Klasse MiniMax angelegt,
	 * außerdem werden sich die aktuellen Positionen der Spielsteine aus der HashMap
	 * Token geholt. Danach wird die Methode constructTree aus MiniMax aufgerufen
	 * und dieser werden die nötigen Parameter übergeben, welche für die Operationen
	 * in der Klasse Minimax benötigt werden. Danach werden die alten Positionen der
	 * Spielsteine gecleard und das Matchfield geupdated mit der Methode
	 * UpdateMatchfield. Als nächstes wird der Fall bearbeitet, in dem der erste und
	 * der zweite Spielstein den selben Zug machen möchten. In diesem Falle soll der
	 * Stein, der auf dem Feld mit der höheren Feldnummer ist der zweite Move sein
	 * und auch nur den für ihn zweitbesten Move ausführen. Danach folgt die Logik
	 * für das setzten der schwarzen Blockersteine. Daraufhin wird das Matchfield
	 * nocheinmal geupdated und der Zug wird abgeschickt.
	 * 
	 */

	public static void playerIsPlayerTwo(int delete, Move move, HashMap<String, Integer> token,
			HashMap<Integer, int[]> matchfieldNeighbors, NetworkClient client, Node currentNode_1, Node currentNode_2) {

		System.out.println("--- Ich bin Player: " + client.getMyPlayerNumber() + " ---");
		MiniMax miniMax_1 = new MiniMax();
		MiniMax miniMax_2 = new MiniMax();

		isMaxPlayer = true;
		move.player = client.getMyPlayerNumber();

		currentNode_1 = matchfieldNodes.get((getCurrentPosition("p21", token)));
		currentNode_2 = matchfieldNodes.get((getCurrentPosition("p22", token)));

		miniMax_1.constructTree(getCurrentPosition("p21", token), currentNode_1.countOfNeighbors, isMaxPlayer,
				matchfieldNodes);
		miniMax_2.constructTree(getCurrentPosition("p22", token), currentNode_2.countOfNeighbors, isMaxPlayer,
				matchfieldNodes);

		currentNode_1.clearIsOccupiedBy();
		currentNode_2.clearIsOccupiedBy();
		/*
		 * random = (int) (Math.random() * (34 - 1)); while (random == 25) {
		 * 
		 * random = (int) (Math.random() * (34 - 1)); }
		 * 
		 * while (token.containsValue(random)) {
		 * 
		 * random = (int) (Math.random() * (34 - 1)); }
		 * 
		 * move.delete = random;
		 */
		updateMatchfieldNodes(matchfieldNodes, token);

		if ((int) token.get("p21") < (int) token.get("p22")) {
			if (miniMax_1.nextHeap == miniMax_2.nextHeap) {
				move.second = miniMax_2.secondBestNode.index;
			}

			move.first = miniMax_1.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p21");

			move.second = miniMax_2.nextHeap;
			matchfieldNodes.get(miniMax_2.nextHeap).setIsOccupiedBy("p22");
		}

		else if ((int) token.get("p21") > (int) token.get("p22")) {
			if (miniMax_1.nextHeap == miniMax_2.nextHeap) {

				move.second = miniMax_1.secondBestNode.index;
			}

			move.first = miniMax_2.nextHeap;
			matchfieldNodes.get(miniMax_2.nextHeap).setIsOccupiedBy("p22");

			move.second = miniMax_1.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p21");
		}

		int[] neighbors = matchfieldNeighbors.get(getCurrentPosition("p11", token));
		delete = neighbors[0];
		int allFull = 1;
		for (int i = 0; i < neighbors.length; i++) {
			if (token.containsValue(delete) || delete == move.second || delete == move.first) {
				delete = neighbors[i];
				allFull++;
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p12", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p01", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p02", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			delete = (int) (Math.random() * (34 - 1));
			while (token.containsValue(delete) || delete == 25) {

				delete = (int) (Math.random() * (34 - 1));
			}
		}
		move.delete = delete;
		delCount = updateMatchfield(token, delCount, move);
		System.out.println(
				"---Move--\n" + move.player + " " + move.first + " " + move.second + " " + move.delete + "\n---");
		client.sendMove(move);

	}

	/*
	 * 
	 * Diese Methode wird verwendet wenn der Spieler mit der Nummer 1 der aktuelle
	 * Clienbt Spieler ist, hier werden zwei Objekte der Klasse MiniMax angelegt,
	 * außerdem werden sich die aktuellen Positionen der Spielsteine aus der HashMap
	 * Token geholt. Danach wird die Methode constructTree aus MiniMax aufgerufen
	 * und dieser werden die nötigen Parameter übergeben, welche für die Operationen
	 * in der Klasse Minimax benötigt werden. Danach werden die alten Positionen der
	 * Spielsteine gecleard und das Matchfield geupdated mit der Methode
	 * UpdateMatchfield. Als nächstes wird der Fall bearbeitet, in dem der erste und
	 * der zweite Spielstein den selben Zug machen möchten. In diesem Falle soll der
	 * Stein, der auf dem Feld mit der höheren Feldnummer ist der zweite Move sein
	 * und auch nur den für ihn zweitbesten Move ausführen. Danach folgt die Logik
	 * für das setzten der schwarzen Blockersteine. Daraufhin wird das Matchfield
	 * nocheinmal geupdated und der Zug wird abgeschickt.
	 * 
	 */
	public static void playerIsPlayerOne(int delete, Move move, HashMap<String, Integer> token,
			HashMap<Integer, int[]> matchfieldNeighbors, NetworkClient client, Node currentNode_1, Node currentNode_2) {

		MiniMax miniMax_1 = new MiniMax();
		MiniMax miniMax_2 = new MiniMax();

		isMaxPlayer = true;

		move.player = client.getMyPlayerNumber();

		currentNode_1 = matchfieldNodes.get(getCurrentPosition("p11", token));
		currentNode_2 = matchfieldNodes.get(getCurrentPosition("p12", token));

		miniMax_1.constructTree(getCurrentPosition("p11", token), currentNode_1.countOfNeighbors, isMaxPlayer,
				matchfieldNodes);
		miniMax_2.constructTree(getCurrentPosition("p12", token), currentNode_2.countOfNeighbors, isMaxPlayer,
				matchfieldNodes);

		currentNode_1.clearIsOccupiedBy();
		currentNode_2.clearIsOccupiedBy();

		/*
		 * random = (int) (Math.random() * (34 - 1)); while (random == 25) { random =
		 * (int) (Math.random() * (34 - 1)); }
		 * 
		 * while (token.containsValue(random)) { random = (int) (Math.random() * (34 -
		 * 1)); }
		 * 
		 * move.delete = random;
		 */

		updateMatchfieldNodes(matchfieldNodes, token);

		if ((int) token.get("p11") < (int) token.get("p12")) {

			if (miniMax_1.nextHeap == miniMax_2.nextHeap) {
				move.second = miniMax_2.secondBestNode.index;

			}

			move.first = miniMax_1.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p11");

			move.second = miniMax_2.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p12");

		} else if ((int) token.get("p11") > (int) token.get("p12")) {
			if (miniMax_1.nextHeap == miniMax_2.nextHeap) {
				move.second = miniMax_1.secondBestNode.index;

			}

			move.first = miniMax_2.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p12");

			move.second = miniMax_1.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p11");
		}

		int[] neighbors = matchfieldNeighbors.get(getCurrentPosition("p01", token));
		delete = neighbors[0];
		int allFull = 1;
		for (int i = 0; i < neighbors.length; i++) {
			if (token.containsValue(delete) || delete == move.second || delete == move.first) {
				delete = neighbors[i];
				allFull++;
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p02", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p21", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p22", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			delete = (int) (Math.random() * (34 - 1));
			while (token.containsValue(delete) || delete == 25) {

				delete = (int) (Math.random() * (34 - 1));
			}
		}

		move.delete = delete;
		delCount = updateMatchfield(token, delCount, move);
		System.out.println(
				"---Move--\n" + move.player + " " + move.first + " " + move.second + " " + move.delete + "\n---");
		client.sendMove(move);

	}

	/*
	 * 
	 * Diese Methode wird verwendet wenn der Spieler mit der Nummer 0 der aktuelle
	 * Clienbt Spieler ist, hier werden zwei Objekte der Klasse MiniMax angelegt,
	 * außerdem werden sich die aktuellen Positionen der Spielsteine aus der HashMap
	 * Token geholt. Danach wird die Methode constructTree aus MiniMax aufgerufen
	 * und dieser werden die nötigen Parameter übergeben, welche für die Operationen
	 * in der Klasse Minimax benötigt werden. Danach werden die alten Positionen der
	 * Spielsteine gecleard und das Matchfield geupdated mit der Methode
	 * UpdateMatchfield. Als nächstes wird der Fall bearbeitet, in dem der erste und
	 * der zweite Spielstein den selben Zug machen möchten. In diesem Falle soll der
	 * Stein, der auf dem Feld mit der höheren Feldnummer ist der zweite Move sein
	 * und auch nur den für ihn zweitbesten Move ausführen. Danach folgt die Logik
	 * für das setzten der schwarzen Blockersteine. Daraufhin wird das Matchfield
	 * nocheinmal geupdated und der Zug wird abgeschickt.
	 * 
	 */

	public static void playerIsPlayerZero(int delete, Move move, HashMap<String, Integer> token,
			HashMap<Integer, int[]> matchfieldNeighbors, NetworkClient client, Node currentNode_1, Node currentNode_2) {

		MiniMax miniMax_1 = new MiniMax();
		MiniMax miniMax_2 = new MiniMax();

		isMaxPlayer = true;

		move.player = client.getMyPlayerNumber();

		currentNode_1 = matchfieldNodes.get(getCurrentPosition("p01", token));
		currentNode_2 = matchfieldNodes.get(getCurrentPosition("p02", token));

		miniMax_1.constructTree(getCurrentPosition("p01", token), currentNode_1.countOfNeighbors, isMaxPlayer,
				matchfieldNodes);
		miniMax_2.constructTree(getCurrentPosition("p02", token), currentNode_2.countOfNeighbors, isMaxPlayer,
				matchfieldNodes);

		currentNode_1.clearIsOccupiedBy();
		currentNode_2.clearIsOccupiedBy();

		/*
		 * random = (int) (Math.random() * (34 - 1)); while (random == 25) {
		 * 
		 * random = (int) (Math.random() * (34 - 1)); }
		 * 
		 * while (token.containsValue(random)) {
		 * 
		 * random = (int) (Math.random() * (34 - 1)); }
		 */

		// move.delete = random;
		// matchfieldNodes.get(random).isOccupiedBy = "delete";

		updateMatchfieldNodes(matchfieldNodes, token);

		if ((int) token.get("p01") < (int) token.get("p02")) {

			if (miniMax_1.nextHeap == miniMax_2.nextHeap) {
				move.second = miniMax_2.secondBestNode.index;
			}

			move.first = miniMax_1.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p01");

			move.second = miniMax_2.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p02");

		} else if ((int) token.get("p01") > (int) token.get("p02")) {
			if (miniMax_1.nextHeap == miniMax_2.nextHeap) {
				move.second = miniMax_1.secondBestNode.index;
			}
			move.first = miniMax_2.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p02");

			move.second = miniMax_1.nextHeap;
			matchfieldNodes.get(miniMax_1.nextHeap).setIsOccupiedBy("p01");
		}

		int[] neighbors = matchfieldNeighbors.get(getCurrentPosition("p11", token));
		delete = neighbors[0];
		int allFull = 1;
		for (int i = 0; i < neighbors.length; i++) {
			if (token.containsValue(delete) || delete == move.second || delete == move.first) {
				delete = neighbors[i];
				allFull++;
			}
		}

		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p12", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p21", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			allFull = 1;
			neighbors = matchfieldNeighbors.get(getCurrentPosition("p22", token));
			delete = neighbors[0];
			allFull = 1;
			for (int i = 0; i < neighbors.length; i++) {
				if (token.containsValue(delete) || delete == move.second || delete == move.first) {
					delete = neighbors[i];
					allFull++;
				}
			}
		}
		if (allFull > neighbors.length) {
			delete = (int) (Math.random() * (34 - 1));
			while (token.containsValue(delete) || delete == 25) {

				delete = (int) (Math.random() * (34 - 1));
			}
		}
		move.delete = delete;
		delCount = updateMatchfield(token, delCount, move);
		System.out.println(
				"---Move--\n" + move.player + " " + move.first + " " + move.second + " " + move.delete + "\n---");
		client.sendMove(move);

	}

	/*
	 * 
	 * Diese Methode updatet die HashMap des Spielfeldes nach jedem Zug.
	 * 
	 */
	public static int updateMatchfield(HashMap<String, Integer> token, int delCount, Move recMove) {
		if (recMove.first == 255 || recMove.second == 255 || recMove.delete == 255) {
			System.out.println("Ungültiger Zug von jemand anderem");
		} else {
			if (recMove.player == 0) {
				if ((int) token.get("p01") < (int) token.get("p02")) {
					token.put("p01", recMove.first);
					token.put("p02", recMove.second);
				} else if ((int) token.get("p01") > (int) token.get("p02")) {
					token.put("p02", recMove.first);
					token.put("p01", recMove.second);
				}

				if (token.containsKey("delete" + delCount)) {
					token.put(("delete" + (++delCount)), recMove.delete);
				} else {
					token.put("delete", recMove.delete);
				}

			}
			if (recMove.player == 1) {
				if ((int) token.get("p11") < (int) token.get("p12")) {
					token.put("p11", recMove.first);
					token.put("p12", recMove.second);
				} else if ((int) token.get("p11") > (int) token.get("p12")) {
					token.put("p12", recMove.first);
					token.put("p11", recMove.second);
				}

				if (token.containsKey("delete" + delCount)) {
					token.put(("delete" + (++delCount)), recMove.delete);
				} else {
					token.put("delete", recMove.delete);
				}

			}
			if (recMove.player == 2) {

				if ((int) token.get("p21") < (int) token.get("p22")) {
					token.put("p21", recMove.first);
					token.put("p22", recMove.second);
				} else if ((int) token.get("p21") > (int) token.get("p22")) {
					token.put("p22", recMove.first);
					token.put("p21", recMove.second);
				}

				if (token.containsKey("delete" + delCount)) {
					token.put(("delete" + (++delCount)), recMove.delete);
				} else {
					token.put("delete", recMove.delete);
				}

			}
		}

		return delCount;

	}

	/*
	 * 
	 * Diese Methode updatet die NodeListe des Spielfeldes nach jedem Zug.
	 * 
	 */

	public static void updateMatchfieldNodes(List<Node> matchfieldNodes, HashMap<String, Integer> token) {

		for (Entry<String, Integer> entry : token.entrySet()) {
			System.out.println("UpdateMatchfield Value " + (entry.getValue()) + " " + entry.getKey());
			int value = (entry.getValue() - 1);

			matchfieldNodes.get(value).setIsOccupiedBy(entry.getKey());
		}

	}

	/*
	 * 
	 * Diese Methode initialisiert das Spielfeld in eine Hashmap
	 * 
	 */

	private static void putAllNeighbors(HashMap<Integer, int[]> matchfield) {
		matchfield.put(1, new int[] { 5, 2 });
		matchfield.put(2, new int[] { 1, 3 });
		matchfield.put(3, new int[] { 2, 7 });
		matchfield.put(4, new int[] { 10, 5 });
		matchfield.put(5, new int[] { 4, 6, 1 });
		matchfield.put(6, new int[] { 5, 7, 12 });
		matchfield.put(7, new int[] { 3, 8, 6 });
		matchfield.put(8, new int[] { 7, 14 });
		matchfield.put(9, new int[] { 17, 10 });
		matchfield.put(10, new int[] { 9, 4, 11 });
		matchfield.put(11, new int[] { 10, 19, 12 });
		matchfield.put(12, new int[] { 11, 6, 13 });
		matchfield.put(13, new int[] { 12, 21, 14 });
		matchfield.put(14, new int[] { 13, 8, 15 });
		matchfield.put(15, new int[] { 14, 23 });
		matchfield.put(16, new int[] { 26, 17 });
		matchfield.put(17, new int[] { 16, 9, 18 });
		matchfield.put(18, new int[] { 17, 19, 28 });
		matchfield.put(19, new int[] { 18, 11, 20 });
		matchfield.put(20, new int[] { 19, 21, 30 });
		matchfield.put(21, new int[] { 20, 13, 22 });
		matchfield.put(22, new int[] { 21, 23, 32 });
		matchfield.put(23, new int[] { 22, 24, 15 });
		matchfield.put(24, new int[] { 23, 34 });
		matchfield.put(25, new int[] { 0, 0 });
		matchfield.put(26, new int[] { 16, 27 });
		matchfield.put(27, new int[] { 26, 28 });
		matchfield.put(28, new int[] { 27, 18, 29 });
		matchfield.put(29, new int[] { 28, 30 });
		matchfield.put(30, new int[] { 29, 20, 31 });
		matchfield.put(31, new int[] { 30, 32 });
		matchfield.put(32, new int[] { 31, 22, 33 });
		matchfield.put(33, new int[] { 32, 34 });
		matchfield.put(34, new int[] { 33, 24 });
		matchfield.put(35, new int[] { 0, 0, 0 });

	}

	/*
	 * 
	 * Diese Methode initialisiert die Position der Spielsteine in eine Hashmap
	 * 
	 */
	private static void getStartConfiguration(HashMap<String, Integer> token) {
		token.put("p01", 8);
		token.put("p02", 15);
		token.put("p11", 4);
		token.put("p12", 9);
		token.put("p21", 29);
		token.put("p22", 31);
		token.put("delete0", 25);
		token.put("delete 35", 35);
	}
}