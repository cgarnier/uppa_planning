package net.irokwa.uppa.hyperplanning;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DataHandler {
	private Calendar lastUpdate;
	private List<Promotion> promotions;
	private Promotion currentPromotion;
	
	private HttpConnection connection;
	private Parser parser;
	private String planningBaseUrl;
	private Context appContext;
	
	
	private static final String PERIODJS = "_periode.js";
	private static final String PROMOJS = "_ressource.js";
	private static final String LEGENDJS = "_grille.js";
	
	public DataHandler(Context context, String planningBaseUrl) {
		this.appContext = context;
		this.planningBaseUrl = planningBaseUrl;
		this.connection = new HttpConnection();
		this.parser = new Parser();
		this.promotions = new ArrayList<Promotion>();
		this.lastUpdate = null;
	}
	
	//// Public methods
	
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) this.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	

	//// Private methods
	
	protected void init() {
		
	}
	
	private String getPeriodUrl() {
		return this.planningBaseUrl + PERIODJS;
	}
	private String getPromoUrl() {
		return this.planningBaseUrl + PROMOJS;
	}
	private String getGrilleUrl() {
		return this.planningBaseUrl + LEGENDJS;
	}
	private void checKForUpdate() {
		
		
		Calendar lastMod = this.connection.getLastModified(planningBaseUrl);
		if( this.getLastUpdate() == null || this.lastUpdate.before(lastMod)){
			Parser par = new Parser();
			ArrayList<Promotion> result = new ArrayList<Promotion>();
			String content = this.connection.getContent(planningBaseUrl + PROMOJS);
			if (!this.connection.isError()){
				this.promotions = par.parsePromo(content);
				if( this.currentPromotion != null){
					for (Promotion promotion : this.promotions) {
						if(this.currentPromotion.getCode().equals(promotion.getCode())){
							this.currentPromotion = promotion;
							this.currentPromotion.setPeriodes(this.getPeriodFor(this.currentPromotion));
							break;
						}
						
					}
				}
			}
		}
		

	}
	
	private ArrayList<Periode> getPeriodFor(Promotion promo){
		ArrayList<Periode> result = new ArrayList<Periode>();
		
		String content = this.connection.getContent(planningBaseUrl + PERIODJS);
		if(!this.connection.isError()){
			result = this.parser.parsePeriode(content, promo.getCode());
			for (Periode periode : result) {
				int contentLenght = this.connection.getContentLenght(this.planningBaseUrl +"diplomes/" + periode.getImageCode() +".png");
				if(!this.connection.isError()) periode.setHttpLength(contentLenght);
			}
		}
		return result;
	}
	
	//// Getters and Setters
	public Calendar getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Calendar lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public List<Promotion> getPromotions() {

		return promotions;
	}
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}
	public Promotion getCurrentPromotion() {
		return currentPromotion;
	}
	public void setCurrentPromotion(Promotion currentPromotion) {
		this.currentPromotion = currentPromotion;
	}
	
}
