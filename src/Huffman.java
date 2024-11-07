import java.util.ArrayList;
import java.util.Arrays;
import tester.Tester;

// represents either one leaf or a forest
abstract class ABranch {
  Integer frequency;

  // constructor
  public ABranch(Integer frequency) {
    this.frequency = frequency;
  }

  // adds this ABranch to a Forest at a position in the Forest
  public abstract void addToForest(int start, Forest forest);

  // calculates the position of the Abranch to the left of the root
  int getLeft(int root) {
    return 1 + (2 * root);
  }

  // calculates the position of the Abranch to the right of the root
  int getRight(int root) {
    return 2 + (2 * root);
  }

  // checks if the this Abranch's frequency is less than the other Abranch
  public boolean isLessThan(ABranch other) {
    return other.isGreaterThanValue(this.frequency);
  }

  // checks if the this Abranch's frequency is greater than a given value
  public boolean isGreaterThanValue(int value) {
    return this.frequency > value;
  }
}

// a letter with its frequencs
class Leaf extends ABranch {
  String letter;

  // constructor
  public Leaf(String letter, Integer frequency) {
    super(frequency);
    this.letter = letter;
  }

  // adds this ABranch to a Forest at a position in the Forest
  public void addToForest(int start, Forest forest) {
    forest.insert(this, start);
  }

  public boolean letterIs(String letter) {
    return this.letter == letter;
  }
}

// a collection of leaves kept in list ordered to fit a tree based of frequency
class Forest extends ABranch {
  ArrayList<Leaf> leaves;

  // constructor
  public Forest(ABranch branch1, ABranch branch2) {
    super(branch1.frequency + branch2.frequency);
    leaves = new ArrayList<Leaf>();

    branch1.addToForest(1, this);
    branch2.addToForest(2, this);
  }

  public void insert(Leaf leaf, int index) {
    while (leaves.size() <= index) {
      leaves.add(null);
    }

    leaves.set(index, leaf);
  }

  // adds this ABranch to a Forest at a position in the Forest
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

  public void appendEncodeLetter(String letter, ArrayList<Boolean> output) {
    ArrayList<Integer> checkLocs = new ArrayList<Integer>();
    checkLocs.add(1);
    checkLocs.add(2);

    int firstLoc = 0;
    while (checkLocs.get(checkLocs.size() - 1) <= leaves.size()) {
      firstLoc = getLeft(firstLoc);
      for (int i = checkLocs.size() - 1; i > -1; i--) {
        int loc = checkLocs.get(i);
        Leaf leaf = leaves.get(loc);
        if (leaf == null)
          continue;

        if (!leaf.letterIs(letter)) {
          checkLocs.remove(i);
          continue;
        }

        int locDiff = loc - firstLoc;

        outputToBinary(locDiff, output);

        return;
      }

      int checkLocsSize = checkLocs.size();
      for (int i = 0; i < checkLocsSize; i++) {
        int loc = checkLocs.get(i);
        checkLocs.set(i, getLeft(loc));
        checkLocs.add(getRight(loc));
      }
    }

    throw new IllegalArgumentException(
        "Tried to encode " + letter + " but that is not part of the language.");
  }

  public void outputToBinary(int base10, ArrayList<Boolean> output) {
    if (base10 < 1)
      return;
    boolean digit = base10 % 2 == 1;
    outputToBinary(base10 / 2, output);
    output.add(digit);
  }

  public String decode(ArrayList<Boolean> sequence) {
    String res = "";
    int readHead = 0;

    for (int i = 0; i < sequence.size(); i++) {
      readHead = sequence.get(i) ? getRight(readHead) : getLeft(readHead);
      Leaf leaf = this.leaves.get(readHead);

      if (leaf == null)
        continue;

      res += leaf.letter;
      readHead = 0;
    }

    if (readHead != 0)
      res += "?";

    return res;
  }
}

//
class Huffman {
  ArrayList<String> letters;
  ArrayList<Integer> frequencies;

  Forest cypher;

  // constructor
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

  // creates an Arraylist of ABranch that uses letters and frequencies
  public ArrayList<ABranch> generateBranchArray(ArrayList<String> letters,
      ArrayList<Integer> frequencies) {

    ArrayList<ABranch> result = new ArrayList<ABranch>();

    for (int i = letters.size() - 1; i > -1; i--) {
      Leaf leaf = new Leaf(letters.get(i), frequencies.get(i));

      sortInto(result, leaf);
    }

    return result;
  }

  // puts a ABranch into a sorted list of sorted ABranches
  public void sortInto(ArrayList<ABranch> branches, ABranch newLeaf) {
    for (int i = 0; i < branches.size(); i++) {
      if (!branches.get(i).isLessThan(newLeaf)) {
        branches.add(i, newLeaf);
        return;
      }
    }
    branches.add(newLeaf);
  }

  // combines all the ABranches in a list of ABranches into one Forest.
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

  // takes a string and converts it to binary where 0 is represented by false and
  // 1 by true
  public ArrayList<Boolean> encode(String toEncode) {
    ArrayList<Boolean> encoded = new ArrayList<Boolean>();
    for (int i = 0; i < toEncode.length(); i++) {
      appendEncodeLetter(toEncode.substring(i, i + 1), encoded);
    }
    return encoded;

  }

  public void appendEncodeLetter(String letter, ArrayList<Boolean> output) {
    cypher.appendEncodeLetter(letter, output);
  }

  public String decode(ArrayList<Boolean> sequence) {
    return cypher.decode(sequence);
  }
}

// tests methods and objects
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

  ArrayList<String> aTof = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f"));
  ArrayList<Integer> aTofFre = new ArrayList<Integer>(Arrays.asList(8, 2, 3, 4, 13, 2));
  
  Huffman abcdef = new Huffman(aTof, aTofFre);
  /* a = 10
   * b = 1100
   * c = 1110
   * d = 1111
   * e = 0
   * f = 1101
   */
  ArrayList<Boolean> aCode;
  ArrayList<Boolean> bCode;
  ArrayList<Boolean> cCode;
  ArrayList<Boolean> dCode;
  ArrayList<Boolean> eCode;
  ArrayList<Boolean> fCode;
  
  void codeCreate() {
  aCode = new ArrayList<Boolean>(Arrays.asList(true, false));
  bCode = new ArrayList<Boolean>(Arrays.asList(true, true, false, false));
  cCode = new ArrayList<Boolean>(Arrays.asList(true, true, true, false));
  dCode = new ArrayList<Boolean>(Arrays.asList(true, true, true, true));
  eCode = new ArrayList<Boolean>(Arrays.asList(false));
  fCode = new ArrayList<Boolean>(Arrays.asList(true, true, false, true));
  }

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

  // tests the isLessThan method
  boolean testIsLessThan(Tester t) {
    boolean res = true;
    // letter to branch
    res &= t.checkExpect(a.isLessThan(fBF), false);
    // branch to letter
    res &= t.checkExpect(fBF.isLessThan(a), true);
    // branch to branch
    res &= t.checkExpect(fCD.isLessThan(fBF), false);
    // letter to letter
    res &= t.checkExpect(a.isLessThan(e), true);
    // same value
    res &= t.checkExpect(a.isLessThan(a), false);
    return res;
  }

  // tests the isGreaterThanValue method
  boolean testIsGreaterThanValue(Tester t) {
    boolean res = true;
    // same val
    res &= t.checkExpect(a.isGreaterThanValue(8), false);
    // greater than
    res &= t.checkExpect(a.isGreaterThanValue(1), true);
    // less than
    res &= t.checkExpect(a.isGreaterThanValue(10), false);
    return res;
  }

  // tests the addToForest method
  boolean testAddToForest(Tester t) {
    boolean res = true;
    return res;
  }

  // tests the insert method
  boolean testInsert(Tester t) {
    boolean res = true;
    return res;
  }

 //tests the encode method
 boolean testEncode(Tester t) {
   boolean res = true;
   // generic test
   codeCreate();
   cCode.addAll(aCode);
   cCode.addAll(bCode);
   res &= t.checkExpect(abcdef.encode("cab"), cCode);
   // tests an illeal letter
   res &= t.checkException(new IllegalArgumentException(
       "Tried to encode r but that is not part of the language."), abcdef, "encode", "car");
   // tests multiple illeal letter
   res &= t.checkException(new IllegalArgumentException(
       "Tried to encode r but that is not part of the language."), abcdef, "encode", "cars");
   
   return res;
 }
 
 //tests the encode method
 boolean testDecode(Tester t) {
   boolean res = true;
   // generic test
   codeCreate();
   cCode.addAll(aCode);
   cCode.addAll(bCode);
   res &= t.checkExpect(abcdef.decode(cCode), "cab");
   // tests adding a question mark at the end
   codeCreate();
   cCode.addAll(aCode);
   cCode.addAll(bCode);
   cCode.add(true);
   res &= t.checkExpect(abcdef.decode(cCode), "cab?");
   // using all letters
   codeCreate();
   dCode.addAll(eCode);
   dCode.addAll(cCode);
   dCode.addAll(aCode);
   dCode.addAll(fCode);
   cCode.add(true);
   res &= t.checkExpect(abcdef.decode(cCode), "decaf?");
   return res;
 }
  
}