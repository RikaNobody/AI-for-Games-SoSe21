package gt1;

import java.util.Comparator;

import java.util.List;
import java.util.*;
import java.util.NoSuchElementException;

public class MiniMax {

	/*
	 * Diese Klasse legt einen Spielbaum an "gameTree", dann erstellt sie eine Liste
	 * mit den möglichen Zügen und ruft dafür die Liste getPossibleStates, aus der
	 * Klasse NodeConnection auf, mit der Anzahl der Nachbarn des aktuellen Knotens.
	 * Dann wird geguckt, ob der aktuelle Spieler der Max-Spieler ist.
	 * Wwas für unseren Fall natürlich noch angepasst werden muss, da wir drei
	 * Spieler haben. Dann wird für jedes Listenelement von listOfPossibleHeaps der
	 * First und Second Neighbor ermittelt und falls ein dritter Nachbar existiert
	 * wird dieser auch ermittelt. Danach wird der eben beschriebene Forgang für die
	 * drei Nachbarn wiederholt.
	 * 
	 * Grundidee von: https://www.baeldung.com/java-minimax-algorithm *
	 */
	public int nextHeap;
	public int depth = 0;
	public int childCounter = 0;
	int indexOfSecond;
	int dCounter = 0;

	public List<Node> matchfieldNodes;

	public static double a = 0.001, b = 3.77, c = 4.65;

	public GameTree gameTree;
	public Node bestNode, secondBestNode;

	Node firstNeighbor;
	Node secondNeighbor;
	Node thirdNeighbor;

	boolean check;
	boolean isMaxPlayer;
	boolean hasThreeNeighbors = false;
	boolean isChildMaxPlayer;
	boolean firstTree = true;

	public void constructTree(int rootNumber, int numberOfBonesOnHeap, boolean isMaxPlayer,
			List<Node> matchfieldNodes) {

		Node root = Client.matchfieldNodes.get(rootNumber - 1);

		System.out.println(" Root :" + rootNumber);

		this.isMaxPlayer = isMaxPlayer;
		this.matchfieldNodes = matchfieldNodes;

		gameTree = new GameTree(root);

		constructTree(root);

	}

	private void constructTree(Node parentNode) {
		System.out.println("PARENT NODE: " + parentNode);

		List<Integer> listofPossibleHeaps = NodeConnection.getPossibleStates(parentNode.countOfNeighbors);

		if (parentNode.hasThreeNeighbors) {
			hasThreeNeighbors = true;

		}

		listofPossibleHeaps.forEach(n -> {

			firstNeighbor = new Node(parentNode.firstNeighbor,
					Client.matchfieldNodes.get(parentNode.firstNeighbor).neighbors);
			secondNeighbor = new Node(parentNode.secondNeighbor,
					Client.matchfieldNodes.get(parentNode.secondNeighbor).neighbors);

			if (hasThreeNeighbors) {
				thirdNeighbor = new Node(parentNode.thirdNeighbor,
						Client.matchfieldNodes.get(parentNode.thirdNeighbor).neighbors);

			}

			nextHeap = checkWin(parentNode);

		});
	}

	public boolean checkWin() {
		Node root = gameTree.getRoot();
		checkWin(root);
		return root.getScore() == 1;
	}

	public int checkWin(Node node) {

		depth = 1;
		List<Node> children = new ArrayList<Node>();

		children.add(firstNeighbor);
		children.add(secondNeighbor);

		if (hasThreeNeighbors) {
			children.add(thirdNeighbor);
		}

		children.forEach(child -> {

			if (dCounter == depth) {
				child.setScore(isMaxPlayer ? 1 : -1);
			} else {
				++dCounter;

				if (childCounter <= 2) {
					isMaxPlayer = false;
					childCounter++;

					if (childCounter == 2) {
						isMaxPlayer = true;
						childCounter = 0;
					}
				}
				checkWin(child);
			}
		});

		Node bestChild = findBestChild(isMaxPlayer, children);

		node.setScore(bestChild.getScore());

		nextHeap = bestChild.index;

		System.out.println("BEST HEAP: " + nextHeap);

		return nextHeap;
	}

	public Node findBestChild(boolean isMaxPlayer, List<Node> children) {
		Comparator<Node> byScoreComparator = Comparator.comparing(Node::getScore);
		check = false;
		children.stream().filter(s -> s.getIsOccupiedBy().equals("")).forEach(s -> {
			double prevScore = 0;
			s.setScore(evaluateMove(s));

			if (!isMaxPlayer && s.getScore() > prevScore) {
				prevScore = s.getScore();
				bestNode = s;

				if (check) {
					indexOfSecond = (children.indexOf(s) - 1);
					secondBestNode = children.get(indexOfSecond);
					System.out.println("Children Index " + indexOfSecond);
				}
				check = true;

			}

			if (isMaxPlayer && s.getScore() < prevScore) {
				prevScore = (-1 * s.getScore());
				bestNode = s;

				if (check) {
					indexOfSecond = (children.indexOf(s) - 1);
					secondBestNode = children.get(indexOfSecond);
				}
				check = true;
			}
		});

		return bestNode;
	}

	private double evaluateMove(Node s) {

		double eval = 1;

		eval = a * s.neighbors.length + b * freeNeighbors(s, matchfieldNodes)
				+ c * distanceToDelete(s, matchfieldNodes);

		System.out.println("E V A L U A T I O N V A L U E : " + eval);
		return eval;
	}

	private int freeNeighbors(Node currentNode, List<Node> matchfieldNodes) {
		Node neighbor_1 = matchfieldNodes.get(currentNode.firstNeighbor);
		Node neighbor_2 = matchfieldNodes.get(currentNode.secondNeighbor);

		int freeFieldCounter = 0;

		if (neighbor_1.getIsOccupiedBy() == "") {
			freeFieldCounter++;

			if (matchfieldNodes.get(neighbor_1.firstNeighbor).getIsOccupiedBy() == "") {
				freeFieldCounter++;
			} else {
				freeFieldCounter--;
			}

			if (matchfieldNodes.get(neighbor_1.secondNeighbor).getIsOccupiedBy() == "") {
				freeFieldCounter++;
			} else {
				freeFieldCounter--;
			}

			if (neighbor_1.hasThreeNeighbors) {
				if (matchfieldNodes.get(neighbor_1.thirdNeighbor).getIsOccupiedBy() == "") {
					freeFieldCounter++;
				} else {
					freeFieldCounter--;
				}
			}

		} else {
			freeFieldCounter--;
		}

		if (neighbor_2.getIsOccupiedBy() == "") {
			freeFieldCounter++;

			if (matchfieldNodes.get(neighbor_2.firstNeighbor).getIsOccupiedBy() == "") {
				freeFieldCounter++;
			} else {
				freeFieldCounter--;
			}
			/*
			 * if (matchfieldNodes.get(neighbor_2.secondNeighbor).getIsOccupiedBy() == "") {
			 * freeFieldCounter++; } else { freeFieldCounter--; }
			 */

			if (neighbor_2.hasThreeNeighbors) {
				if (matchfieldNodes.get(neighbor_2.thirdNeighbor).getIsOccupiedBy() == "") {
					freeFieldCounter++;
				} else {
					freeFieldCounter--;
				}
			}

		} else {
			freeFieldCounter--;
		}

		if (currentNode.hasThreeNeighbors) {
			Node neighbor_3 = matchfieldNodes.get(currentNode.thirdNeighbor);

			if (neighbor_3.getIsOccupiedBy() == "") {
				freeFieldCounter++;

				if (matchfieldNodes.get(neighbor_3.firstNeighbor).getIsOccupiedBy() == "") {
					freeFieldCounter++;
				} else {
					freeFieldCounter--;
				}

				if (matchfieldNodes.get(neighbor_3.secondNeighbor).getIsOccupiedBy() == "") {
					freeFieldCounter++;
				} else {
					freeFieldCounter--;
				}

				if (neighbor_3.hasThreeNeighbors) {

					if (matchfieldNodes.get(neighbor_3.thirdNeighbor).getIsOccupiedBy() == "") {
						freeFieldCounter++;
					} else {
						freeFieldCounter--;
					}
				}
			} else {
				freeFieldCounter--;
			}
		}

		return freeFieldCounter;
	}

	private int distanceToDelete(Node currentNode, List<Node> matchfieldNodes) {
		// int depthCounter = 0;
		int distancePoints = 0;
		Node neighbor_1 = matchfieldNodes.get(currentNode.firstNeighbor);
		Node neighbor_2 = matchfieldNodes.get(currentNode.secondNeighbor);

		if (neighbor_1.getIsOccupiedBy().contains("delete")) {
			distancePoints--;
		} else {
			distancePoints++;

			if (matchfieldNodes.get(neighbor_1.firstNeighbor).getIsOccupiedBy().contains("delete")) {
				distancePoints--;
			} else {
				distancePoints++;

			}

			if (matchfieldNodes.get(neighbor_1.secondNeighbor).getIsOccupiedBy().contains("delete")) {
				distancePoints--;
			} else {
				distancePoints++;

			}

			if (matchfieldNodes.get(neighbor_1.firstNeighbor).hasThreeNeighbors) {

				if (matchfieldNodes.get(neighbor_1.thirdNeighbor).getIsOccupiedBy().contains("delete")) {
					distancePoints--;
				} else {
					distancePoints++;

				}
			}

		}

		if (neighbor_2.getIsOccupiedBy().contains("delete")) {
			distancePoints--;
		} else {

			distancePoints++;

			if (matchfieldNodes.get(neighbor_2.firstNeighbor).getIsOccupiedBy().contains("delete")) {
				distancePoints--;
			} else {
				distancePoints++;

			}

			if (matchfieldNodes.get(neighbor_2.secondNeighbor).getIsOccupiedBy().contains("delete")) {
				distancePoints--;
			} else {
				distancePoints++;

			}

			if (matchfieldNodes.get(neighbor_2.firstNeighbor).hasThreeNeighbors) {

				if (matchfieldNodes.get(neighbor_2.thirdNeighbor).getIsOccupiedBy().contains("delete")) {
					distancePoints--;
				} else {
					distancePoints++;

				}
			}

		}
		if (currentNode.hasThreeNeighbors) {
			Node neighbor_3 = matchfieldNodes.get(currentNode.thirdNeighbor);

			if (currentNode.hasThreeNeighbors) {

				if (neighbor_3.getIsOccupiedBy().contains("delete")) {
					distancePoints--;
				} else {
					distancePoints++;

					if (matchfieldNodes.get(neighbor_3.firstNeighbor).getIsOccupiedBy().contains("delete")) {
						distancePoints--;
					} else {
						distancePoints++;

					}

					if (matchfieldNodes.get(neighbor_3.secondNeighbor).getIsOccupiedBy().contains("delete")) {
						distancePoints--;
					} else {
						distancePoints++;

					}

					if (matchfieldNodes.get(neighbor_1.firstNeighbor).hasThreeNeighbors) {

						if (matchfieldNodes.get(neighbor_3.thirdNeighbor).getIsOccupiedBy().contains("delete")) {
							distancePoints--;
						} else {
							distancePoints++;

						}
					}

				}
			}
		}

		// depthCounter++;

		return distancePoints;
	}
}
