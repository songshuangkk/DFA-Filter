package dfa;

/**
 * DFA过滤算法的实现.
 */
public interface TextFilter {

  /**
   * 文本过滤.
   * @param str   被过滤字符串
   * @param mask  是否掩盖敏感字
   * @return      过滤结果
   */
  FilterResult filtering(String str, boolean mask);
}
