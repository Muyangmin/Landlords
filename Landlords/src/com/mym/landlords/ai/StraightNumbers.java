package com.mym.landlords.ai;

import java.util.ArrayList;

//用于隐藏数据细节，避免复杂数据结构
final class StraightNumbers{
	/** 代表能组成顺子的某个数字序列。其内容为升序排列。 */
	public final ArrayList<Integer> numbers;
	private int[] intNumbers;
	//package access
	StraightNumbers(ArrayList<Integer> list){
		this.numbers = list;
	}
	/**
	 * 将 {@link #numbers}转换为一个数组并返回。
	 * @return 返回一个数组，其顺序和遍历 {@link #numbers}的顺序一样。
	 */
	public final int[] asIntegerArray(){
		if (intNumbers==null){
			int size = numbers.size();
			intNumbers = new int[size];
			for (int i=0; i<size; i++){
				intNumbers[i] = numbers.get(i);
			}
		}
		return intNumbers;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StraightNumbers [numbers=").append(numbers).append("]");
		return builder.toString();
	}
}