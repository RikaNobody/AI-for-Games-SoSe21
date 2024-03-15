package gt1;

/*
 * 
 * Diese Klasse bildet das GmeTree Objekt, welchem man beim 
 * Erstellen einen Knoten übergeben muss, welcher dann den Root Knoten 
 * bildet, anhand von diesem wird dann in der Klasse MiniMax der Baum berechnet. 
 * 
 * Grundidee von:
 * https://www.baeldung.com/java-minimax-algorithm
 * */

public class GameTree {
	public Node rootNode;

	public GameTree(Node rootNode) {
		this.rootNode = rootNode;
	}

	public Node getRoot() {
		return rootNode;
	}
}
