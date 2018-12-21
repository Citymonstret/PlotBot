package xyz.kvantum.plotbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * String comparison library.
 */
public class StringComparison<T> {

  private T bestMatch;
  private double match = Integer.MAX_VALUE;
  private T bestMatchObject;

  /**
   * Constructor
   *
   * @param input   Input Base Value
   * @param objects Objects to compare
   */
  public StringComparison(String input, T[] objects) {
    init(input, objects);
  }

  public StringComparison(String input, Collection<T> objects) {
    init(input, (T[]) objects.toArray());
  }

  public static int getLevenshteinDistance(String s, String t) {
    int n = s.length();
    int m = t.length();
    if (n == 0) {
      return m;
    } else if (m == 0) {
      return n;
    }
    if (n > m) {
      String tmp = s;
      s = t;
      t = tmp;
      n = m;
      m = t.length();
    }
    int p[] = new int[n + 1];
    int d[] = new int[n + 1];
    int i;
    for (i = 0; i <= n; i++) {
      p[i] = i;
    }
    for (int j = 1; j <= m; j++) {
      char t_j = t.charAt(j - 1);
      d[0] = j;

      for (i = 1; i <= n; i++) {
        int cost = s.charAt(i - 1) == t_j ? 0 : 1;
        d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
      }
      int[] _d = p;
      p = d;
      d = _d;
    }
    return p[n];
  }

  /**
   * You should call init(...) when you are ready to get a String comparison value.
   */
  public StringComparison() {
  }

  /**
   * Compare two strings
   *
   * @param s1 String Base
   * @param s2 Object
   * @return match
   */
  public static int compare(String s1, String s2) {
    int distance = getLevenshteinDistance(s1, s2);
    if (s2.contains(s1)) {
      distance -= Math.min(s1.length(), s2.length());
    }
    if (s2.startsWith(s1)) {
      distance -= 4;
    }
    return distance;
  }

  /**
   * Create an ArrayList containing pairs of letters
   *
   * @param s string to split
   * @return ArrayList
   */
  public static ArrayList<String> wLetterPair(String s) {
    ArrayList<String> aPairs = new ArrayList<>();
    String[] wo = s.split("\\s");
    for (String aWo : wo) {
      String[] po = sLetterPair(aWo);
      Collections.addAll(aPairs, po);
    }
    return aPairs;
  }

  /**
   * Get an array containing letter pairs
   *
   * @param s string to split
   * @return Array
   */
  public static String[] sLetterPair(String s) {
    int numPair = s.length() - 1;
    String[] p = new String[numPair];
    for (int i = 0; i < numPair; i++) {
      p[i] = s.substring(i, i + 2);
    }
    return p;
  }

  public void init(String input, T[] objects) {
    int c;
    this.bestMatch = objects[0];
    this.bestMatchObject = objects[0];
    input = input.toLowerCase();
    for (T o : objects) {
      if ((c = compare(input, getString(o).toLowerCase())) < this.match) {
        this.match = c;
        this.bestMatch = o;
        this.bestMatchObject = o;
      }
    }
  }

  public String getString(T o) {
    if (o instanceof StringComparable) {
      return ((StringComparable) o).getComparableString();
    }
    return o.toString();
  }

  /**
   * Get the object
   *
   * @return match object
   */
  public T getMatchObject() {
    return this.bestMatchObject;
  }

  /**
   * Get the best match value
   *
   * @return match value
   */
  public String getBestMatch() {
    return getString(this.bestMatch);
  }

  /**
   * Will return both the match number, and the actual match string
   *
   * @return object[] containing: double, String
   */
  public ComparisonResult getBestMatchAdvanced() {
    return new ComparisonResult(this.match, this.bestMatch);
  }

  public interface StringComparable {
    String getComparableString();
  }


  /**
   * The comparison result
   */
  public class ComparisonResult {

    public final T best;
    public final double match;

    /**
     * The constructor
     *
     * @param match Match value
     * @param best  Best Match
     */
    public ComparisonResult(double match, T best) {
      this.match = match;
      this.best = best;
    }
  }
}
