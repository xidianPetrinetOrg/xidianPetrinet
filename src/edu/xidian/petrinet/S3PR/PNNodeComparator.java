package edu.xidian.petrinet.S3PR;

import java.util.Comparator;

import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;

/**
 * 按照AbstractPNNode的name或其中的数字排序
 */
public class PNNodeComparator implements Comparator<AbstractPNNode<?>> {
	@Override
	public int compare(AbstractPNNode<?> o1, AbstractPNNode<?> o2) {
		String s1 = o1.getName();
		String s2 = o2.getName();
		int i1 = toInt(s1);
		int i2 = toInt(s2);
		if (i1 == 0 || i2 == 0) return s1.compareTo(s2);  // 按照字符串比较
		else return Integer.compare(i1, i2); // 按照字符串中的数字比较
	}
	
	/**
	 * '['以前的字符串转int，例如："p20[20]" ==> 20 
	 * @param s
	 * @return
	 */
	private int toInt(String s) {
		char c;
		StringBuilder str = new StringBuilder(); 
        for(int i = 0; i<s.length(); i++){
        	c = s.charAt(i);
        	if (c == '[') break;
        	if (c >= '0' && c <= '9') str.append(c);
        }
        try {
			return Integer.parseInt(str.toString());
		} catch (NumberFormatException e) {
			//e.printStackTrace();
			return 0;
		}
	}
}
