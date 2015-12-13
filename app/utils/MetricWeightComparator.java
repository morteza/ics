package utils;

import java.util.Comparator;
import java.util.Map;

/**
 * To sort weighted maps.
 *
 */
public class MetricWeightComparator implements Comparator<Long> {
  Map<Long,Double> base;

  public MetricWeightComparator(Map<Long,Double> base) {
      this.base = base;
  }

  // Note: this comparator imposes orderings that are inconsistent with
  // equals.
  public int compare(Long a, Long b) {
      if (Double.compare(base.get(a), base.get(b))>0) {
          return -1;
      } else {
          return 1;
      } // returning 0 would merge keys
  }

}