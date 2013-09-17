package net.irokwa.uppa.hyperplanning;

import java.util.ArrayList;
import java.util.List;

public class Promotion {
	private List<Periode> periodes;
	private String name;
	private String code;
	private Periode currentPeriode;
	public Promotion() {
		this.periodes = new ArrayList<Periode>();
	}
	public Promotion(String name, String code) {
		this.periodes = new ArrayList<Periode>();
		this.name = name;
		this.code = code;
	}
	
	public List<Periode> getPeriodes() {
		return periodes;
	}
	public void setPeriodes(List<Periode> periodes) {
		this.periodes = periodes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Periode getCurrentPeriode() {
		return currentPeriode;
	}
	public void setCurrentPeriode(Periode currentPeriode) {
		this.currentPeriode = currentPeriode;
	}
	
}
