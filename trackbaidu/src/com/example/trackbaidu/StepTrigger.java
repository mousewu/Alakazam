package com.example.trackbaidu;

/*
 * interface for callback func
 */
public interface StepTrigger {
	/**
	 * ÿ��̽�⵽�Ų�ʱ����trigger
	 * 
	 * @param strideLength ����
	 * @param orientation ����
	 */
	public void trigger(int stepCount, float strideLength, float[] orientation);
}
