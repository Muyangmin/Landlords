package com.mym.landlords.ai;

import java.util.ArrayList;

 //用于隐藏数据细节，避免复杂数据结构
final class StraightNumbers{
	public ArrayList<Integer> numbers;
	//package access
	StraightNumbers(ArrayList<Integer> list){
		this.numbers = list;
	}
}