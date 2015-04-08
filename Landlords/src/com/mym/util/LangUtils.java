package com.mym.util;

import java.util.ArrayList;

/**
 * 一些语言上的快捷方法。
 * @author Muyangmin
 * @create 2015-4-8
 */
public final class LangUtils {
	/**
	 * 创建一个ArrayList，保证该列表的迭代顺序和参数顺序一样。
	 * @param args 需要加入ArrayList的元素。<b>必须注意元素的类型，否则可能导致堆污染。</b>
	 * @return 返回装有这些元素的列表。
	 */
	@SafeVarargs
	public static final <T> ArrayList<T> createList(T... args){
		ArrayList<T> list = new ArrayList<>();
		for (T element:args){
			list.add(element);
		}
		return list;
	} 
}
