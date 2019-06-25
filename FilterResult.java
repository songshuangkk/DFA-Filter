package dfa;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class FilterResult {

  private String str;

  private boolean sensitive;
}
