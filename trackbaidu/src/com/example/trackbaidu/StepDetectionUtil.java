package com.example.trackbaidu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class StepDetectionUtil {
	
	public static double EARTH_RADIUS=6378137;
	
	/**
	 * ����ϼ��ٶ�
	 * @param accel ������ٶ�
	 * @return
	 */
	public static List<Float> getMagnitudeOfAccel(List<float[]> accelList) {
		float[] accel = new float[3]; // acc: acceleration of x, y, z
		float macc = 0; // macc: magnitude of the acceleration
		List<Float> magnitudeAccel = new ArrayList<Float>();
		Iterator<float[]> it = accelList.iterator();
		while(it.hasNext()) {
			accel = it.next();
			macc = (float) Math.sqrt(accel[0] * accel[0] + accel[1] * accel[1] + accel[2] * accel[2]);
			magnitudeAccel.add(macc);
		}
		return magnitudeAccel;
	}
	
	/**
	 * ����ֲ�ƽ�����ٶ�
	 * @param magAccel �ϼ��ٶ�
	 * @param w �ֲ����ڴ�С�����ڼ���ֲ�ƽ�����ٶȺͷ���
	 * @return
	 */
	public static List<Float> getLocalMeanAccel(List<Float> magnitudeAccel, int w) {
		List<Float> macc = new ArrayList<Float>(); // macc: magnitude of acceleration
		macc = magnitudeAccel;
		List<Float> lmacc = new ArrayList<Float>(); // lmacc: local mean acceleration value
		float sum = 0f;
		for (int i = 0, size = macc.size(); i < size; i++) {
			if (i < w || size - i <= w) 
				lmacc.add(macc.get(i));
			else if (i >= w && size - i > w) {
				sum = macc.get(i);
				for (int j = 1; j <= w; j++) {
					sum += macc.get(i+j) + macc.get(i - j);
				}
				lmacc.add(sum / (2 * w + 1));
			}
		}
		return lmacc;
	}
	
	/**
	 * ����ֲ�ƽ�����ٶȵ�ƽ��ֵ������ȷ���Ų��ж���������ֵ
	 * @param localMeanAccel �ֲ�ƽ�����ٶ�
	 * @return
	 */
	public static float getAverageLocalMeanAccel(List<Float> localMeanAccel) {
		float sum = 0f;
		Iterator<Float> it = localMeanAccel.iterator();
		int size = localMeanAccel.size();
		while (it.hasNext()) {
			sum += it.next();
		}
		return sum / size;
	}
	
	/**
	 * ����ֲ����ٶȷ���
	 * @param magAccel �ϼ��ٶ�
	 * @param localMeanAccel �ֲ�ƽ�����ٶ�
	 * @param w �ֲ����ڴ�С�����ڼ���ֲ�ƽ�����ٶȺͷ���
	 * @return
	 */
	public static float[] getAccelVariance(float[] magAccel, float[] localMeanAccel, int w) {
		int size = magAccel.length;
		float[] accelVariance = new float[size];
		float sum = 0;
		for(int i = 0; i < size; i++) {
			sum = (float) Math.pow(magAccel[i] - localMeanAccel[i], 2);
			for(int j = 1; j <= w; j++) {
				int right = i + j; // ��ǰλ��i�Ҳ���±�
				int left = i - j; // ��ǰλ��i�����±�
				if(right >= size) { // ����Ҳ���±곬�����ڴ�С����������ڴ�С���ص�������ʼλ��
					right = right - size;
				}
				if(left < 0) { // ��������±�С�ڴ�����С�±��㣬����ϴ��ڴ�С���ص�����ĩβλ��
					left = size + left;
				}
				sum +=  Math.pow(magAccel[left] - localMeanAccel[left], 2)
						+ Math.pow(magAccel[right] - localMeanAccel[right], 2);
			}
			accelVariance[i] = sum / ( 2 * w + 1);
		}
		return accelVariance;
	}
	
	/**
	 * ����Ų��ж�����
	 * ����������ָ����ֵ���ж�������1��������0
	 * @param localMeanAccel �ֲ�ƽ�����ٶ�
	 * @param threshold ָ����ֵ
	 * @return
	 */
	public static List<Integer> getCondition(List<Float> localMeanAccel, float threshold) {
		List<Integer> condition = new ArrayList<Integer>();
		Iterator<Float> it = localMeanAccel.iterator();
		float flag;
		while (it.hasNext()) {
			flag = it.next();
			if (flag > threshold) {
				condition.add(1);
			}
			else {
				condition.add(0);
			}
		}
		return condition;
	}
	
	/**
	 * �ж�data�Ƿ�Ϊ1�����Ϊ1������true�����򷵻�false
	 */
	public static boolean isOne(int data) {
		if(data == 1)
			return true;
		else return false;
	}
	
	/**
	 * �������
	 * @param A ����A
	 * @param B ����B
	 * @return
	 */
	public static float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];
     
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
     
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
     
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
     
        return result;
    }
	
	/**
	 * Werberg SL ���������㷨
	 * @param k �����������������������������
	 * @param maxA �ֲ������ٶ�
	 * @param minA �ֲ���С���ٶ�
	 * @return
	 */
	public static float getSL(float k, float maxA, float minA)
	{
		return (float) (k * Math.pow(maxA - minA, 0.25f));
	}
	
	/**
	 * ƽ�����������㷨
	 * @param k �����������������������������
	 * @param meanA �ֲ�ƽ�����ٶ�
	 * @return
	 */
	public static float getSL(float k, float meanA) {
		return (float) (k * Math.pow(meanA, 1/3f));
	}
	
	/**
	 * ��ȡ��ǰ������j����Χ-w ~ +w �������������ļ��ٶ�
	 * @param magAccel �ϼ��ٶ�
	 * @param j ��ǰ������ָ��
	 * @param w �ֲ����ڴ�С�����ڼ���ֲ�ƽ�����ٶȺͷ���
	 * @return
	 */
	public static float getMax(float[] magAccel, int j, int w)
	{
		float a = magAccel[j];
		float b = 0;
		float maxA = 0;
		for(int k = -w; k < w; k++) {						
			b = magAccel[j+k];
			maxA = a > b ? a : b;
		}
		return maxA;
	}
	
	/**
	 * ��ȡ��ǰ������j����Χ-w ~ +w ������������С�ļ��ٶ�
	 * @param magAccel �ϼ��ٶ�
	 * @param j ��ǰ������ָ��
	 * @param w �ֲ����ڴ�С�����ڼ���ֲ�ƽ�����ٶȺͷ���
	 * @return
	 */
	public static float getMin(float [] magAccel, int j, int w)
	{
		float a = magAccel[j];
		float b = 0;
		float minA = 0;
		for(int k = -w; k < w; k++) {						
			b = magAccel[j+k];
			minA = a < b ? a : b;
		}
		return minA;
	}
	
	/**
	 * ��ȡ��ǰ������j����Χ-w ~ +w ���������ƽ�����ٶ�
	 * @param magnitudeAccel �ϼ��ٶ�
	 * @param j ��ǰ������ָ��
	 * @param w �ֲ����ڴ�С�����ڼ���ֲ�ƽ�����ٶȺͷ���
	 * @return
	 */
	public static float getMean(List<Float> magnitudeAccel, int j, int w)
	{
		float sum = 0;
		for(int i = -w; i < w; i++) {
			sum += magnitudeAccel.get(j + i);
		}
		return sum / (2 * w);
	}
	
	/**
	 * ��ȡE�ķ��ţ����Ϊ��������1�����Ϊ��������-1�����Ϊ0������0
	 * @param E
	 * @return
	 */
	public static int getSign(float E) {
		if(E > 0) return 1;
		if(E < 0) return -1;
		return 0;
	}
	
	public static float[] getRotationMatrixFromOrientation(float yaw, float pitch, float roll) {
		float[] xM = new float[9];
		float[] yM = new float[9];
		float[] zM = new float[9];
		
		float sinX = (float) Math.sin(pitch);
		float cosX = (float) Math.cos(pitch);
		float sinY = (float) Math.sin(roll);
		float cosY = (float) Math.cos(roll);
		float sinZ = (float) Math.sin(yaw);
		float cosZ = (float) Math.cos(yaw);
		
		// rotation about x-axis (pitch)
		xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
		xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
		xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;
		
		// rotation about y-axis (roll)
		yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
		yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
		yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;
		
		// rotation about z-axis(yaw)
		zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
		zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
		zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
		
		// rotation order is y, x, z (roll, pitch, yaw)
		float[] resultMatrix = matrixMultiplication(xM, yM);
		resultMatrix = matrixMultiplication(zM, resultMatrix);
		return resultMatrix;
	}
	
	/**
	 * 
	 * @param lat1
	 * @param lng1
	 * @param bearing
	 * @param distance
	 * @return
	 */
	public static double[] getPoint(double lat1, double lng1, double bearing, double distance) {
		lat1 = (double) Math.toRadians(lat1);
		lng1 = (double) Math.toRadians(lng1);
		bearing = (double) Math.toRadians(bearing);
		double r = distance/EARTH_RADIUS;
		double[] point2 = new double[2];
		double lat2 = Math.asin(Math.sin(lat1)*Math.cos(r) + 
				Math.cos(lat1)*Math.sin(r)*Math.cos(bearing));
		double lng2 = (lng1 + 
		Math.atan2(Math.sin(bearing)*Math.sin(r)*Math.cos(lat1),
				Math.cos(r)-Math.sin(lat1)*Math.sin(lat2)));
		point2[0] = Math.toDegrees(lat2);
		point2[1] = Math.toDegrees(lng2);
		return point2;
	}
	
	/**
	 * ����ÿһ����ƽ������
	 * @param numOne
	 * @param numZero
	 * @param j
	 * @param orientation
	 * @return
	 */
	public static double getMeanOrientation(int numOne, int numZero, int j, List<float[]> orientation, int position, int algorithm) {
		float meanOrientation = 0;
		int len = numOne + numZero;
		float x = 0, y = 0;
		for(int i = j - len; i < j; i++) {
			if (position == 1) {
				if(algorithm != 1) {
					y = (float) (y + Math.sin(orientation.get(i)[0]));
					x = (float) (x + Math.cos(orientation.get(i)[0]));
				}
				else {
					y = (float) (y + Math.sin(orientation.get(i)[0] / 180 * Math.PI));
					x = (float) (x + Math.cos(orientation.get(i)[0] / 180 * Math.PI));
				}
				
			}
			else {
				if(algorithm != 1) {
					y = (float) (y + Math.sin(orientation.get(i)[2]));
					x = (float) (x + Math.cos(orientation.get(i)[2]));
				}
				else {
					y = (float) (y + Math.sin(orientation.get(i)[2] / 180 * Math.PI));
					x = (float) (x + Math.cos(orientation.get(i)[2] / 180 * Math.PI));
				}
			}
		}
		meanOrientation = (float) (((((Math.atan2(y / len, x / len) * 180 / Math.PI)+360)%360 )+180)%360);
		return meanOrientation;
	}
	
	/**
	 * ����ֻ���ֱ���ڿ��ӿڴ��У����򴫸����ɼ��ķ�����Ҫ������ת
	 * @param orientation
	 * @return
	 */
	public static float getRotatedOrientation(float orientation) {
		if(orientation > 0 && orientation < 270)
			orientation = 270 - orientation;
		if(orientation > 270 && orientation < 360)
			orientation = 630 - orientation;
		orientation = orientation + 40;
		orientation = (orientation + 360)%360;
		return orientation;
	}
	
}
