package dfa;

import lombok.Data;

import java.util.Map;

@Data
public class DFANode {

  private boolean end;

  private int index;    // 标明单单在敏感字中的位置

  private Map sub;
}
