import java.util.ArrayList;

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
    while(leaves.size() <= index) {
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