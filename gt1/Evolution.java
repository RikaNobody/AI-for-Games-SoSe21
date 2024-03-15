package gt1;

import java.awt.AWTException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lenz.htw.blocks.Server;

/**
 * 
 * Die Klasse beschäftigt sich mit der Evolution der Gewichtung der einzelene
 * Parameter der Bewertungsfunktion des Minimax-Algorithmus. Diese Klasse
 * erstellt sechs verschiedene Vorfaktoren, für die Bewertungsfunktion. Dann
 * wird das Spiel mit all diesen gewählten Vorfaktoren. Danach wählt er die
 * beiden Besten aus, rekombiniert diese und mutiert sie, dann wird eine
 * Komination aus den beiden besten Elternteilen genommen, dem Kind und einer
 * mutierten Version, von jedem Elternteil und dem Kind und spielt dann das
 * Spiel erneut um zu überprüfen, ob es dann besser ist.
 *
 */
public class Evolution {

	public static void main(final String[] args) throws IOException, AWTException, InterruptedException {
		String pathToJar = "W:\\Master\\1. Semester\\GT1 AI for Games and Interactive Systems\\Uebung\\blocks\\";
		boolean gui = true;
		int winner;

		double[][] weight = new double[6][3];
		// List<Integer> allPoints = new ArrayList();
		int allPoints[] = new int[6];
		int points = 0, secondPoints = 0;
		double bestA, bestB, bestC, secBestA, secBestB, secBestC;
		double childA, childB, childC;
		int trainingGames = 10;

		weight = initWeight(weight);

		// train for X Number of Games
		for (int g = 0; g < trainingGames; g++) {

			// try 6 different Weights
			for (int k = 0; k < weight.length; k++) {
				Client.validMoves = -2;

				MiniMax.a = weight[k][0];
				MiniMax.b = weight[k][1];
				MiniMax.c = weight[k][2];

				StartServerThread server = new StartServerThread("Server");

				server.start();

				// System.out.println("!!!The Winner is!!! " + winner);
				// startServer(pathToJar, gui);
				// startAllClients();

				StartClientThread client0 = new StartClientThread("Client 0");
				StartClientThread client1 = new StartClientThread("Client 1");
				StartClientThread client2 = new StartClientThread("Client 2");

				client0.start();
				client1.start();
				client2.start();
				client0.join();
				client1.join();
				client2.join();
				server.join();
				winner = server.getValue();

				System.out.println("- - - Your winner is: " + winner + " - - -");

				// Selection

				allPoints[k] = Client.validMoves;

//			bestA = weight[k][0];
//			bestB = weight[k][1];
//			bestC = weight[k][2];

			}
			// Select Best Parents
			final List<double[]> weightCopy = Arrays.asList(weight);
			ArrayList<double[]> sortedWeight = new ArrayList(weightCopy);
			Collections.sort(sortedWeight, Comparator.comparing(s -> allPoints[weightCopy.indexOf(s)]));

			bestA = weight[weight.length - 1][0];
			bestB = weight[weight.length - 1][1];
			bestC = weight[weight.length - 1][2];

			secBestA = weight[weight.length - 2][0];
			secBestB = weight[weight.length - 2][1];
			secBestC = weight[weight.length - 2][2];

			// Recomb Best Parents
			childA = ((bestA + secBestA) / 2);
			childB = ((bestB + secBestB) / 2);
			childC = ((bestC + secBestC) / 2);

			// Printout best Childs
			System.out.println("\nBest weights are\nfor A: " + childA + "; B " + childB + "; C " + childC + "\n");

			// Get Parents and Childs
			weight[0][0] = childA;
			weight[0][1] = childB;
			weight[0][2] = childC;

			weight[1][0] = bestA;
			weight[1][1] = bestB;
			weight[1][2] = bestC;

			weight[2][0] = secBestA;
			weight[2][1] = secBestB;
			weight[2][2] = secBestC;

			// Mutate Parents & Childs
			weight[3][0] = childA * Math.random();
			weight[3][1] = childB * Math.random();
			weight[3][2] = childC * Math.random();

			weight[4][0] = bestA * Math.random();
			weight[4][1] = bestB * Math.random();
			weight[4][2] = bestC * Math.random();

			weight[5][0] = secBestA * Math.random();
			weight[5][1] = secBestB * Math.random();
			weight[5][2] = secBestC * Math.random();

		}
	}

	private static double[][] initWeight(double[][] weight) {

		for (int k = 0; k < weight.length; k++) {
			for (int i = 0; i < weight[k].length; i++) {
				weight[k][i] = Math.random();
			}
		}
		return weight;
	}

	private static void startServer(String path, boolean headless) throws IOException {
		String cmd = "java -Djava.library.path=" + '"' + path + "lib/native" + '"' + " -jar " + '"' + path
				+ "blocks.jar" + '"';
		if (!headless) {
			cmd = cmd + " 8 headless";
		}
		System.out.println("Start Server\nShow GUI " + headless);
		System.out.println(cmd);
		Runtime.getRuntime().exec(cmd);

	}

	private static void startAllClients() throws IOException {

	}
}