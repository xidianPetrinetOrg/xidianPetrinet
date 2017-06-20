package edu.xidian.petrinet.S3PR;

import java.util.Comparator;

/**
 * 按照name或其中的数字排序
 */
public class StringComparator implements Comparator<String> {
	@Override
	public int compare(String o1, String o2) {
		int i1 = toInt(o1);
		int i2 = toInt(o2);
		if (i1 == 0 || i2 == 0) return o1.compareTo(o2);  // 按照字符串比较
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
        	if (c == '[' || c == '(' || c == '\r' || c == '\n') break;
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
