package apriori;

import java.util.ArrayList;
import java.util.Arrays;

public class Apriori {
  private static Apriori _instance;
  private ArrayList<int[]> mTransaction = new ArrayList<int[]>();
  private int mMinSup = 2;
  private int mMinConf;

  /**
   * @param args
   */
  public static void main(String[] args) {
    _instance = new Apriori();
    _instance.initializeValues();
    _instance.executeApriori();
  }
  
  private void initializeValues() {
    int[] item1 = {1, 3, 4};
    int[] item2 = {2, 3, 5};
    int[] item3 = {1, 2, 3, 5};
    int[] item4 = {2, 5};
    mTransaction.add(item1);
    mTransaction.add(item2);
    mTransaction.add(item3);
    mTransaction.add(item4);
    printArray(mTransaction);
  }
  
  public void executeApriori() {
    ArrayList<ItemList> first = generateCandidateFromTransaction();
    System.out.println("First State");
    printItemList(first);
    while(true) {
      ArrayList<ItemList> second = generateCandidateFromList(first);
      ArrayList<ItemList> second_2 = generateNextListFromCandidate(second);
      System.out.println("After Generate Second");
      printItemList(second_2);
      if (second_2.size() <= 1) {
        System.out.println("done");
        break;
      }
      first = second_2;
    }
  }
  
  public class ItemList {
    private int mCount;
    private int[] mItems;
    private int mSup = 2;
    
    public ItemList() {
    }
    
    public ItemList(int c, int[] i, int s) {
      mCount = c;
      mItems = i;
      mSup = s;
    }
    public int getSupport() {
      return mSup;
    }
    public int[] getItems() {
      return mItems;
    }
    public int getItem(int index) {
      return mItems[index];
    }
    public int getCount() {
      return mCount;
    }
    public boolean isAlreadyExist(ArrayList<ItemList> items) {
      boolean res = false;
      for (int i = 0; i < items.size(); i++) {
        if (Apriori._instance.contain(items.get(i).getItems(), this.getItems())) {
          return true;
        }
      }
      return res;
    }
    
    private void print() {
      String out = "";
      for(int i = 0; i < mItems.length; i++) {
        out += mItems[i] + ", ";
      }
      System.out.println("mCount : " + mCount + ", mItems : " + out + ", mSup : " + mSup);
    }
  }
  
  private int countAppearance(ArrayList<int[]> set, int[] target) {
    int ret = 0;
    for (int i = 0; i < set.size(); i++) {
      if (contain(set.get(i), target)) {
        ret++;
      }
    }
    return ret;
  }
  
  private boolean contain(int[] source, int[] find) {
    boolean ret = false;
    int match = 0;
    for (int i = 0; i < find.length; i++) {
      for (int j = 0; j < source.length; j++) {
        if (source[j] == find[i]) {
          match++;
        }
      }
    }
    if(match == find.length) {
      ret = true;
    }
    return ret;
  }
  
  private ArrayList<ItemList> generateCandidateFromTransaction() {
    ArrayList<ItemList> ret = new ArrayList<ItemList>();
    ArrayList<Integer> items = new ArrayList<Integer>();
    for (int i = 0; i < mTransaction.size(); i++) {
      int[] temp_items = mTransaction.get(i);
      for (int j = 0; j < temp_items.length; j++) {
        Integer data = new Integer(temp_items[j]);
        if (!items.contains(data)) {
          items.add(data);
        }
      }
    }
    for (int ii = 0; ii < items.size(); ii++) {
      int[] temp = {items.get(ii)};
      ret.add(new ItemList(1, temp, countAppearance(mTransaction, temp)));
    }
    return ret;
  }
  
  private ArrayList<ItemList> generateCandidateFromList(ArrayList<ItemList> list) {
    ArrayList<ItemList> candidate = new ArrayList<ItemList>();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getSupport() >= mMinSup) {
        if (!list.get(i).isAlreadyExist(candidate)) {
          candidate.add(list.get(i));
        }
      }
    }
    return candidate;
  }
  
  private ArrayList<ItemList> generateNextListFromCandidate(ArrayList<ItemList> candidate) {
    ArrayList<ItemList> list = new ArrayList<ItemList>();
    for (int i = 0; i < candidate.size(); i++) {
      for (int j = i; j < candidate.size(); j++) {
        System.out.println("generateNextListFromCandidate");
        printItemList(candidate);
        if (distance(candidate.get(i), candidate.get(j)) == 1) {
          System.out.println("i:" + i + ", j:" + j);
          candidate.get(i).print();
          candidate.get(j).print();
          int[] items = pickItems(candidate.get(i), candidate.get(j));
          ItemList temp = new ItemList(candidate.get(0).getCount() + 1, items, countAppearance(mTransaction, items));
          temp.print();
          list.add(temp);
        }
      }
    }
    return list;
  }
  
  private int distance(ItemList one, ItemList another) {
    int ret = one.getCount();
    for (int i = 0; i < one.getCount(); i++) {
      for (int j = 0; j < another.getCount(); j++) {
        if (one.getItem(i) == another.getItem(j)) {
          ret--;
        }
      }
    }
    return ret;
  }
  
  private int[] pickItems(ItemList one, ItemList another) {
    int[] ret = new int[one.getCount() + 1];
    int i = 0;
    int j = 0;
    int index = 0;
    System.out.println("pickItems");
    one.print();
    another.print();
    while((i < one.getCount()) || (j < another.getCount())) {
      if (one.getCount() == i) {
        System.out.println("1");
        ret[index++] = another.getItem(j);
        j++;
      } else if (another.getCount() == j) {
        System.out.println("2");
        ret[index++] = one.getItem(i);
        i++;
      } else if (one.getItem(i) < another.getItem(j)) {
        System.out.println("3");
        ret[index++] = one.getItem(i);
        i++;
      } else if (one.getItem(i) > another.getItem(j)) {
        System.out.println("4");
        ret[index++] = another.getItem(j);
        j++;
      } else {
        System.out.println("5");
        ret[index++] = one.getItem(i);
        i++;
        j++;
      }
    }
    return ret;
  }
  
  private void printArray(ArrayList<int[]> array) {
    for (int i = 0; i < array.size(); i++) {
      String out = "";
      int[] buf = array.get(i);
      for (int j = 0; j < buf.length; j++) {
        out += buf[j] + ", ";
      }
      System.out.println("transactionId : " + i + " - items : " + out);
    }
  }

  private void printItemList(ArrayList<ItemList> list) {
    for (int i = 0; i < list.size(); i++) {
      int[] items = list.get(i).getItems();
      System.out.print("ID : " + i + " - Items : ");
      for (int j = 0; j < items.length; j++) {
        System.out.print(items[j] + ", ");
      }
      System.out.println("MinSupport : " + list.get(i).getSupport());
    }
  }

}
