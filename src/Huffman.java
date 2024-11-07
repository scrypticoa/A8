import java.util.ArrayList;
import java.util.Arrays;
import tester.Tester;

abstract class ABranch {
  public abstract void addToForest(int start, Forest forest);

  int getLeft(int root) {
    return 1 + (2 * root);
  }

  int getRight(int root) {
    return 2 + (2 * root);
  }

  int getChild(int node) {
    return (node - 1) / 2;
  }
}

class Leaf extends ABranch {
  String letter;
  Integer frequency;

  public Leaf(String letter, Integer frequency) {
    this.letter = letter;
    this.frequency = frequency;
  }

  public void addToForest(int start, Forest forest) {
    forest.insert(this, start);
  }
}

class Forest extends ABranch {
  ArrayList<Leaf> leaves;
  Integer total;

  public Forest(ABranch branch1, ABranch branch2) {

  }

  public void insert(Leaf leaf, int index) {
    while (leaves.size() <= index) {
      leaves.add(null);
    }

    leaves.set(index, leaf);
  }

  public void addToForest(int start, Forest forest) {
    doAddToForest(start, 0, forest);
  }

  public void doAddToForest(int insertLoc, int extractLoc, Forest forest) {
    Leaf leaf = this.leaves.get(extractLoc);

    if (leaf != null) {
      leaf.addToForest(insertLoc, forest);
      return;
    }

    this.doAddToForest(getLeft(insertLoc), getLeft(extractLoc), forest);
    this.doAddToForest(getRight(insertLoc), getRight(extractLoc), forest);
  }
}

class Huffman {
  ArrayList<String> letters;
  ArrayList<Integer> frequencies;

  public Huffman(ArrayList<String> letters, ArrayList<Integer> frequencies) {

    if (letters.size() != frequencies.size()) {
      throw new IllegalArgumentException("Mismatch in input size");
    }

    if (letters.size() < 2) {
      throw new IllegalArgumentException("Cannot encode alphabet with 1 or fewer characters");
    }

    this.letters = letters;
    this.frequencies = frequencies;
  }
}

class ExamplesHuffman {
  Leaf a = new Leaf("a", 8);
  Leaf b = new Leaf("b", 2);
  Leaf c = new Leaf("c", 3);
  Leaf d = new Leaf("d", 4);
  Leaf e = new Leaf("e", 13);
  Leaf f = new Leaf("f", 2);

  Forest fBF = new Forest(b, f);// 4
  Forest fCD = new Forest(c, d);// 7
  Forest fBFCD = new Forest(fBF, fCD);// 11
  Forest fABFCD = new Forest(a, fBFCD);// 19
  Forest fABCDEF = new Forest(e, fABFCD);// 32

  // tests the getLeft method
  boolean testGetLeft(Tester t) {
    boolean res = true;
    // tests 0
    res &= t.checkExpect(fBF.getLeft(0), 1);
    // tests 1
    res &= t.checkExpect(fBF.getLeft(1), 3);
    // tests 2
    res &= t.checkExpect(a.getLeft(2), 5);
    return res;
  }

  // tests the getRight method
  boolean testGetRight(Tester t) {
    boolean res = true;
    // tests 0
    res &= t.checkExpect(fBF.getRight(0), 2);
    // tests 1
    res &= t.checkExpect(fBF.getRight(1), 4);
    // tests 2
    res &= t.checkExpect(a.getRight(2), 6);
    return res;
  }



}