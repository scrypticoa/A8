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
  
  public abstract int value();
  
  public boolean isLessThan(ABranch other) {
    return other.isGreaterThanValue(this.value());
  }
  
  public boolean isGreaterThanValue(int value) {
    return this.value() > value;
  }
}

class Leaf extends ABranch {
  String letter;
  Integer frequency;
  
  public Leaf(String letter, Integer frequency) {
    this.letter = letter;
    this.frequency = frequency;
  }
  
  public int value() {
    return frequency;
  }
  
  public void addToForest(int start, Forest forest) {
    forest.insert(this, start);
  }
}

class Forest extends ABranch {
  ArrayList<Leaf> leaves;
  Integer total;
  
  public Forest(ABranch branch1, ABranch branch2) {
    this.total = branch1.value() + branch2.value();
    
    leaves = new ArrayList<Leaf>();
    
    branch1.addToForest(1, this);
    branch2.addToForest(2, this);
  }
  
  public int value() {
    return total;
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
  
  Forest cypher;
  
  public Huffman(ArrayList<String> letters, ArrayList<Integer> frequencies) {
    
    if (letters.size() != frequencies.size()) {
      throw new IllegalArgumentException("Mismatch in input size");
    }
    
    if (letters.size() < 2) {
      throw new IllegalArgumentException("Cannot encode alphabet with 1 or fewer characters");
    }
    
    this.letters = letters;
    this.frequencies = frequencies;
    
    ArrayList<ABranch> branches = generateBranchArray(letters, frequencies);
    
    this.cypher = mergeAll(branches);
  }
  
  public ArrayList<ABranch> generateBranchArray(ArrayList<String> letters, ArrayList<Integer> frequencies) {
    
    ArrayList<ABranch> result = new ArrayList<ABranch>();
    
    for (int i = letters.size() - 1; i > -1; i--) {
      Leaf leaf = new Leaf(letters.get(i), frequencies.get(i));
      
      sortInto(result, leaf);
    }
    
    return result;
  }
  
  public void sortInto(ArrayList<ABranch> branches, ABranch newLeaf) {
    for (int i = 0; i < branches.size(); i++) {
      if (!branches.get(i).isLessThan(newLeaf)) {
        branches.add(i, newLeaf);
        return;
      }
    }
    branches.add(newLeaf);
  }
  
  public Forest mergeAll(ArrayList<ABranch> branches) {
    while (branches.size() > 2) {
      ABranch small0 = branches.remove(0);
      ABranch small1 = branches.remove(0);
      
      sortInto(branches, new Forest(small0, small1));
    }
    
    ABranch small0 = branches.remove(0);
    ABranch small1 = branches.remove(0);
    
    return new Forest(small0, small1);
  }
  
  public ArrayList<Boolean> encode(String toEncode) {
    ArrayList<Boolean> encoded;
    for (int i = 0; i < toEncode.length(); i++) {
      encoded.add(null);
    }
    
  }
  
  public void appendEncodeLetter(String letter, ArrayList<Boolean> output) {
    
  }
}