package net.irokwa.uppa.hyperplanning;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Periode {
	private String code;
	private String label;
	private List<String> legendes;
	private Calendar startDate;
	private Calendar endDate;
	private int httpLength;

	public Periode(String label, String code) {
		this.code = code;
		this.label = label;
		this.legendes = new ArrayList<String>();
		this.startDate = Calendar.getInstance();
		this.endDate = Calendar.getInstance();
		this.httpLength = 0;
		
		this.computeDates();
	}
	public boolean isCurrentPeriode() {
		Calendar rightNow = Calendar.getInstance();
		if(this.endDate.after(rightNow)) return true;
		return false;
	}

	private void computeDates() {
		ArrayList<String> months = new ArrayList<String>();
		months.add("janvier");
		months.add("février");
		months.add("mars");
		months.add("avril");
		months.add("mai");
		months.add("juin");
		months.add("juillet");
		months.add("aout");
		months.add("septembre");
		months.add("octobre");
		months.add("novembre");
		months.add("décembre");

		LinkedList<String> liste = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(this.label, " ");
		while (st.hasMoreTokens()) {

			liste.add(st.nextToken());
		}

		int year = Integer.parseInt(liste.get(liste.size() - 1));
		int monthEnd = 0;
		if (months.contains(liste.get(liste.size() - 2)))
			monthEnd = months.indexOf(liste.get(liste.size() - 2));

		int dayEnd = Integer.parseInt(liste.get(liste.size() - 3));

		int monthStart = 0;
		int dayStart = 1;
		if (liste.size() == 7) {
			monthStart = months.indexOf(liste.get(liste.size() - 5));
			dayStart = Integer.parseInt(liste.get(liste.size() - 6));
		}
		else if (liste.size() == 6) {
			monthStart = monthEnd;
			dayStart = Integer.parseInt(liste.get(liste.size() - 5));
		}
		
		this.startDate.set(year, monthStart, dayStart);
		this.endDate.set(year, monthEnd, dayEnd);
		

	}

	public String getImageCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setImageCode(String imagecode) {
		this.code = imagecode;
	}

	public List<String> getLegendes() {
		return legendes;
	}

	public void setLegendes(List<String> legendes) {
		this.legendes = legendes;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public int getHttpLength() {
		return httpLength;
	}

	public void setHttpLength(int httpLength) {
		this.httpLength = httpLength;
	}

}
