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

// a letter with its frequencies
class Leaf extends ABranch {
  String letter;

  // constructor
  public Leaf(String letter, Integer frequency) {
    super(frequency);
    this.letter = letter;
  }

  // adds this ABranch to a Forest at a position in the Forest
  public void addToForest(int start, Forest forest) {
    forest.insert(new Leaf(this.letter, this.frequency), start);
  }

  public boolean letterIs(String letter) {
    return this.letter.equals(letter);
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

  // inserts a given leaf at an index of leaves
  // adds nulls for blank indexes if needed
  public void insert(Leaf leaf, int index) {
    while (leaves.size() <= index) {
      leaves.add(null);
    }

    leaves.set(index, leaf);
  }

  // adds this ABranch to a Forest at a position in the Forest
  public void addToForest(int start, Forest forest) {
    forest.insert(null, start);
    doAddToForest(getLeft(start), 1, forest);
    doAddToForest(getRight(start), 2, forest);
  }

  // helper for add forest which branches out, ending recursion on a given
  // branch when it finds its leaf and adds it to forest
  public void doAddToForest(int insertLoc, int extractLoc, Forest forest) {
    Leaf leaf = this.leaves.get(extractLoc);

    if (leaf != null) {
      leaf.addToForest(insertLoc, forest);
      return;
    }

    this.doAddToForest(getLeft(insertLoc), getLeft(extractLoc), forest);
    this.doAddToForest(getRight(insertLoc), getRight(extractLoc), forest);
  }

  // appends the binary sequence to output which represents letter in this
  // forest. If letter does not exist in this forest throws an exception
  public void appendEncodeLetter(String letter, ArrayList<Boolean> output) {
    // System.out.println(this.leaves);
    // System.out.println(letter);

    /*
     * for (int i = 0; i < leaves.size(); i++) { if (leaves.get(i) != null) {
     * System.out.print(i + " " + leaves.get(i).letter + " "); } }
     */

    ArrayList<Integer> checkLocs = new ArrayList<Integer>();
    checkLocs.add(1);
    checkLocs.add(2);

    int firstLoc = 0;
    while (checkLocs.size() > 0) {
      if (checkLocs.get(checkLocs.size() - 1) > leaves.size()) {
        break;
      }
      // System.out.println(checkLocs);
      firstLoc = getLeft(firstLoc);
      for (int i = checkLocs.size() - 1; i > -1; i--) {
        int loc = checkLocs.get(i);
        Leaf leaf = leaves.get(loc);
        if (leaf == null) {
          continue;
        }

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

  // converts base10 to binary represented by boolean values and appends
  // those bools to output
  public void outputToBinary(int base10, ArrayList<Boolean> output) {
    if (base10 < 1) {
      return;
    }
    boolean digit = base10 % 2 == 1;
    outputToBinary(base10 / 2, output);
    output.add(digit);
  }

  // given a binary sequence, returns the string they represent in this forest
  // if the final character being deciphered is undefined in this forest,
  // adds ? to the output string
  public String decode(ArrayList<Boolean> sequence) {
    String res = "";
    int readHead = 0;

    for (int i = 0; i < sequence.size(); i++) {
      readHead = sequence.get(i) ? getRight(readHead) : getLeft(readHead);
      Leaf leaf = this.leaves.get(readHead);

      if (leaf == null) {
        continue;
      }

      res += leaf.letter;
      readHead = 0;
    }

    if (readHead != 0) {
      res += "?";
    }

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

    for (int i = 0; i < letters.size(); i++) {
      Leaf leaf = new Leaf(letters.get(i), frequencies.get(i));

      sortInto(result, leaf);
    }

    return result;
  }

  // puts a ABranch into a sorted list of sorted ABranches
  public void sortInto(ArrayList<ABranch> branches, ABranch newLeaf) {
    for (int i = 0; i < branches.size(); i++) {
      if (newLeaf.isLessThan(branches.get(i))) {
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

  // this huffman's computed cypher appends the encoded binary representation
  // of letter to output
  public void appendEncodeLetter(String letter, ArrayList<Boolean> output) {
    cypher.appendEncodeLetter(letter, output);
  }

  // decodes a given binary sequence according to the computed cypher
  public String decode(ArrayList<Boolean> sequence) {
    return cypher.decode(sequence);
  }
}

// tests methods and objects
class ExamplesHuffman {

  boolean subForestsEqual(Forest a, int aHead, Forest b, int bHead) {
    Leaf aLeaf = a.leaves.get(aHead);
    Leaf bLeaf = b.leaves.get(bHead);

    if (aLeaf != null && bLeaf != null) {
      return aLeaf.letterIs(bLeaf.letter);
    }

    if ((aLeaf == null) != (bLeaf == null)) {
      return false;
    }

    if (!subForestsEqual(a, a.getLeft(aHead), b, b.getLeft(bHead))) {
      return false;
    }
    return subForestsEqual(a, a.getRight(aHead), b, b.getRight(bHead));
  }

  Leaf a = new Leaf("a", 8);
  Leaf b = new Leaf("b", 2);
  Leaf c = new Leaf("c", 3);
  Leaf d = new Leaf("d", 4);
  Leaf e = new Leaf("e", 13);
  Leaf f = new Leaf("f", 2);

  Forest f1 = new Forest(b, f);// 4
  Forest fCD = new Forest(c, d);// 7
  Forest fBFCD = new Forest(f1, fCD);// 11
  Forest fABFCD = new Forest(a, fBFCD);// 19
  Forest fABCDEF = new Forest(e, fABFCD);// 32

  ArrayList<String> aTof = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f"));
  ArrayList<Integer> aTofFre = new ArrayList<Integer>(Arrays.asList(8, 2, 3, 4, 13, 2));

  Huffman abcdef = new Huffman(aTof, aTofFre);
  
  Forest f2 = new Forest(c, d);
  Forest f3 = new Forest(f1, f2);
  Forest f4 = new Forest(a, f3);
  Forest f5 = new Forest(e, f4);
  
  /*
   * a = 10 b = 1100 c = 1110 d = 1111 e = 0 f = 1101
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
    res &= t.checkExpect(f1.getLeft(0), 1);
    // tests 1
    res &= t.checkExpect(f1.getLeft(1), 3);
    // tests 2
    res &= t.checkExpect(a.getLeft(2), 5);
    return res;
  }

  // tests the getRight method
  boolean testGetRight(Tester t) {
    boolean res = true;
    // tests 0
    res &= t.checkExpect(f1.getRight(0), 2);
    // tests 1
    res &= t.checkExpect(f1.getRight(1), 4);
    // tests 2
    res &= t.checkExpect(a.getRight(2), 6);
    return res;
  }

  // tests the isLessThan method
  boolean testIsLessThan(Tester t) {
    boolean res = true;
    // letter to branch
    res &= t.checkExpect(a.isLessThan(f1), false);
    // branch to letter
    res &= t.checkExpect(f1.isLessThan(a), true);
    // branch to branch
    res &= t.checkExpect(fCD.isLessThan(f1), false);
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

  // tests letterIs method
  boolean testLetterIs(Tester t) {
    boolean res = true;
    // tests a false
    res &= t.checkExpect(a.letterIs("b"), false);
    // tests a true
    res &= t.checkExpect(a.letterIs("a"), true);
    return res;
  }

  // tests the addToForest method
  boolean testAddToForest(Tester t) {
    boolean res = true;

    // leaf test

    Forest fCDCopy = new Forest(c, d);

    a.addToForest(2, fCDCopy);

    res &= t.checkExpect(fCDCopy.leaves.get(2), a);

    // forest test

    f1.addToForest(1, fCDCopy);

    res &= t.checkExpect(subForestsEqual(fCDCopy, 1, f1, 0), true);

    return res;
  }

  boolean testDoAddToForest(Tester t) {
    boolean res = true;

    Forest fCDCopy = new Forest(c, d);

    // generic with forest

    fCDCopy.insert(null, 1);
    f1.doAddToForest(f1.getLeft(1), 1, fCDCopy);
    f1.doAddToForest(f1.getRight(1), 2, fCDCopy);

    res &= t.checkExpect(subForestsEqual(fCDCopy, 1, f1, 0), true);

    return res;
  }

  // tests the insert method
  boolean testInsert(Tester t) {
    boolean res = true;

    // replace case

    Forest fCDCopy = new Forest(c, d);

    fCDCopy.insert(a, 1);

    res &= t.checkExpect(fCDCopy.leaves.get(1), a);

    // add blank nulls case

    fCDCopy.insert(f, 5);

    res &= t.checkExpect(fCDCopy.leaves.get(3), null);
    res &= t.checkExpect(fCDCopy.leaves.get(4), null);
    res &= t.checkExpect(fCDCopy.leaves.get(5), f);

    return res;
  }

  // tests the encode method
  boolean testEncode(Tester t) {
    boolean res = true;
    // generic test
    codeCreate();
    cCode.addAll(aCode);
    cCode.addAll(bCode);
    res &= t.checkExpect(abcdef.encode("cab"), cCode);
    // tests an illeal letter
    res &= t.checkException(
        new IllegalArgumentException("Tried to encode r but that is not part of the language."),
        abcdef, "encode", "car");
    // tests multiple illeal letter
    res &= t.checkException(
        new IllegalArgumentException("Tried to encode r but that is not part of the language."),
        abcdef, "encode", "cars");

    return res;
  }

  // tests the decode method in the Huffman class
  boolean testHuffmanDecode(Tester t) {
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
    dCode.add(true);
    res &= t.checkExpect(abcdef.decode(dCode), "decaf?");
    return res;
  }

  // tests the decode method in the Forest class
  boolean testForestDecode(Tester t) {
    boolean res = true;
    // generic test
    codeCreate();
    cCode.addAll(aCode);
    cCode.addAll(bCode);
    res &= t.checkExpect(abcdef.cypher.decode(cCode), "cab");
    // tests adding a question mark at the end
    codeCreate();
    cCode.addAll(aCode);
    cCode.addAll(bCode);
    cCode.add(true);
    res &= t.checkExpect(abcdef.cypher.decode(cCode), "cab?");
    // using all letters
    codeCreate();
    dCode.addAll(eCode);
    dCode.addAll(cCode);
    dCode.addAll(aCode);
    dCode.addAll(fCode);
    dCode.add(true);
    res &= t.checkExpect(abcdef.cypher.decode(dCode), "decaf?");
    return res;
  }
 
  boolean testAppendEncodeLetter(Tester t) {
    boolean res = true;
    
    // generic case
    
    ArrayList<Boolean> bools = new ArrayList<Boolean>();
    abcdef.cypher.appendEncodeLetter("a", bools);
    
    codeCreate();
    
    res &= t.checkExpect(bools, aCode);
    
    // error case
    
    t.checkException(
        new IllegalArgumentException("Tried to encode g but that is not part of the language."),
        abcdef, "appendEncodeLetter", "g", bools);
    
    return res;
  }
 
  boolean testOutputToBinary(Tester t) {
    boolean res = true;
    
    ArrayList<Boolean> bin3 = new ArrayList<Boolean>();
        
    f1.outputToBinary(3, bin3);
    res &= t.checkExpect(bin3, new ArrayList<Boolean>(Arrays.asList(true, true)));
    
    ArrayList<Boolean> bin8 = new ArrayList<Boolean>();
    
    f1.outputToBinary(8, bin8);
    res &= t.checkExpect(bin8, new ArrayList<Boolean>(Arrays.asList(true, false, false, false)));
    
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
    
    return res;
  }
 
  boolean testMergeAll(Tester t) {
    boolean res = true;
    
    // generic merge all
    
    res &= t.checkExpect(
        abcdef.mergeAll(new ArrayList<ABranch>(Arrays.asList(b, f, c, d, a, e))),
        f5);
    
    // intermediate merge all
    
    res &= t.checkExpect(
        abcdef.mergeAll(new ArrayList<ABranch>(Arrays.asList(c, d, f1, a, e))),
        f5);
    
    // final merge all
 
    res &= t.checkExpect(
        abcdef.mergeAll(new ArrayList<ABranch>(Arrays.asList(e, f4))),
        new Forest(e, f4));
    
    return res;
  }

  // tests the sortInto method
  boolean testSortInto(Tester t) {
    boolean res = true;

    // tests adding to the front
    ArrayList<ABranch> sorted = new ArrayList<ABranch>(Arrays.asList(a, e));
    abcdef.sortInto(sorted, b);
    ArrayList<ABranch> sortedNew = new ArrayList<ABranch>(Arrays.asList(b, a, e));
    res &= t.checkExpect(sorted, sortedNew);

    // tests adding in the middle
    sorted = new ArrayList<ABranch>(Arrays.asList(b, f, e));
    abcdef.sortInto(sorted, a);
    sortedNew = new ArrayList<ABranch>(Arrays.asList(b, f, a, e));
    res &= t.checkExpect(sorted, sortedNew);

    // tests adding to the end
    sorted = new ArrayList<ABranch>(Arrays.asList(b, f, a));
    abcdef.sortInto(sorted, e);
    sortedNew = new ArrayList<ABranch>(Arrays.asList(b, f, a, e));
    res &= t.checkExpect(sorted, sortedNew);

    return res;
  }

  //tests the generateBranchArray method
  boolean testGenerateBranchArray(Tester t) {
    ArrayList<ABranch> result = new ArrayList<ABranch>(Arrays.asList(b, f, c, d, a, e));
    return t.checkExpect(abcdef.generateBranchArray(aTof, aTofFre), result);
  }

}