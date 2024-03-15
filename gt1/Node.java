package gt1;

public class Node {

	/**
	 * Diese Klasse ist ein Node Objekt, welches das Objekt für die matchfieldNodes
	 * Liste ist. Über das Node Objekt lässt sich auf den Index des Knotens(Die
	 * Position des Feldes auf dem Spielbrett) und die der Nachbar Felder zugreifen.
	 * Es müssen nur der eigene Index und ein Array der Nachbarfelder, beim
	 * erstellen eines Nodeobjektes übergeben werden, alle anderen Werte werden
	 * durch diese übergebenen Parameter berechnet oder werden durch andere Klassen
	 * gesetzt.
	 *
	 */

	public int countOfNeighbors;

	int index;
	int firstNeighbor;
	int secondNeighbor;
	int thirdNeighbor;
	int[] neighbors;

	double score;

	public boolean isMaxPlayer;
	public boolean hasThreeNeighbors;

	String isOccupiedBy = "";

	public Node(int index, int[] neighbors) {
		this.index = index;
		this.neighbors = neighbors;
		this.firstNeighbor = neighbors[0];
		this.secondNeighbor = neighbors[1];

		if (neighbors.length == 3) {
			hasThreeNeighbors = true;
			this.thirdNeighbor = neighbors[2];
		}
		countOfNeighbors = neighbors.length;
		this.isOccupiedBy = "";

	}

	public boolean getIsMaxPlayer() {
		return isMaxPlayer;
	}

	public void setScore(double d) {
		this.score = d;
	}

	public double getScore() {
		return score;

	}

	public void clearIsOccupiedBy() {
		if (!isOccupiedBy.contains("delete")) {
			isOccupiedBy = "";
		}
	}

	public void setIsOccupiedBy(String token) {
		isOccupiedBy = token;
	}

	public String getIsOccupiedBy() {
		return isOccupiedBy;

	}
}
