package dfa;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class DFATextFilter implements TextFilter {

  private static Map hashMap = Maps.newHashMap();

  static {
    try {
      loadFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void loadFile() throws IOException {
    File file = new File(
        DFATextFilter.class.getClassLoader().getResource("sensitive.txt").getFile());

    List<String> list = Files.readLines(file, Charset.forName("utf-8"));

    Set<Sensitive> sensitiveWordSet = new HashSet<>(40);

    list.forEach(item -> {
      sensitiveWordSet.add(Sensitive.builder().text(item).build());
    });

    init(sensitiveWordSet);
  }

  // Load  Init File.
  private static void init(Set<Sensitive> sensitiveWordSet) {

    Iterator<Sensitive> iterator = sensitiveWordSet.iterator();

    while (iterator.hasNext()) {
      Sensitive sensitive = iterator.next();

      char[] text = sensitive.getText().toCharArray();

      Map tempMap = hashMap;
      int index = 1;
      for (int i=0; i<text.length; i++) {
        char item = text[i];
        if (!tempMap.containsKey(item)) {
          DFANode dfaNode = new DFANode();
          dfaNode.setIndex(index);
          dfaNode.setSub(new HashMap());
          dfaNode.setEnd(i == (text.length-1));     // end?

          tempMap.put(item, dfaNode);
          tempMap = dfaNode.getSub();
        } else {
          DFANode subNode = (DFANode) tempMap.get(item);
          tempMap = subNode.getSub();
        }
        index++;
      }
    }
  }

  @Override
  public FilterResult filtering(String str, boolean mask) {
    return check(str, mask);
  }

  private  FilterResult check(String str, boolean mask) {
    Map tempCheckMap = hashMap;
    boolean exist = false;
    char[] chars = str.toCharArray();
    for (int i=0; i<chars.length; i++) {
      if (tempCheckMap.containsKey(chars[i])) {
        DFANode subNode = (DFANode) tempCheckMap.get(chars[i]);

        if (subNode.isEnd()) {
          if (mask) {
            int length = subNode.getIndex();
            for (int j=0; j<length; j++) {
              chars[i-j] = 'X';
            }
          }

          exist = true;
          str = String.valueOf(chars);
        } else {
          tempCheckMap = subNode.getSub();
        }
      } else {
        tempCheckMap = hashMap;
        if (tempCheckMap.containsKey(chars[i])) {
          DFANode subNode = (DFANode) tempCheckMap.get(chars[i]);

          if (subNode.isEnd()) {
            if (mask) {
              int length = subNode.getIndex();
              for (int j=0; j<length; j++) {
                chars[i-j] = 'X';
              }
            }
            exist = true;
            str = String.valueOf(chars);
            tempCheckMap = hashMap;
          } else {
            tempCheckMap = subNode.getSub();
          }
        }
      }
    }

    return FilterResult.builder().sensitive(exist).str(str).build();
  }
}
