package gt1;

import java.util.List;
import java.util.stream.*;

/*
 * Grundidee von:
 * https://www.baeldung.com/java-minimax-algorithm
 * */

public class NodeConnection {
	
	static List<Integer> getPossibleStates(int numberOfBonesInHeap) {
        return IntStream.rangeClosed(1, 3).boxed()
          .map(i -> numberOfBonesInHeap - i)
          .filter(boardCount -> boardCount >= 0)
          .collect(Collectors.toList());
    }

}
