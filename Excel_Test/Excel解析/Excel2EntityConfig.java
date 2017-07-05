package com.iris.utils.excel;

import java.text.SimpleDateFormat;

public class Excel2EntityConfig {

	private String[] columns;

	private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private int currPosittion = 0;

	private int colStartPosittion = 1;

	public SimpleDateFormat getFormater() {
		return formater;
	}

	/***************************************************************************
	 * �����������ֶε�ת����ʽ Ĭ��ֵΪ new SimpleDateFormat("yyyy-MM-dd HH��mm��ss ");
	 */
	public void setFormater(SimpleDateFormat formater) {
		this.formater = formater;
	}

	public String[] getColumns() {
		return columns;
	}

	/***************************************************************************
	 * ����Excel����ʵ���ֶεĶ�Ӧ��ϵ
	 * 
	 * @param columns
	 *            ʵ���ֶε� �ַ��������ʾ ���磺 String[] columns = {"�ֶ�һ", "�ֶζ�", "�ֶ���","�ֶ�n..." };
	 *            Excel����һ�ж�Ӧʵ������ֶ�һ��Excel���ڶ��ж�Ӧʵ������ֶζ�....��������
	 */
	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public int getCurrPosittion() {
		return currPosittion;
	}

	/***************************************************************************
	 * ����excel������ݴӵڼ��п�ʼ��Ĭ��ֵ,���������У��ӵڶ��п�ʼ ��ֵ����0
	 */
	public void setCurrPosittion(int currPosittion) {
		this.currPosittion = currPosittion - 1;
	}

	public int getColStartPosittion() {
		return colStartPosittion;
	}

	/***************************************************************************
	 * ����excel������ݴӵڼ��п�ʼ��Ĭ��ֵ,�ӵ�1�п�ʼ ��ֵ����0
	 */
	public void setColStartPosittion(int colStartPosittion) {
		this.colStartPosittion = colStartPosittion - 1;
	}

}
