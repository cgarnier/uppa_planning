package net.irokwa.uppa.planning;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import net.irokwa.uppa.planning.R;

import org.xml.sax.SAXException;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {

	private final static int CODE_SETTINGS = 1;

	private TextView tvPromo;
	private Spinner spPeriodes;
	// private Spinner spPromos;
	private ImageButton bSettings;
	private WebView webView;

	private Promotion thePromo;
	private ArrayList<Promotion> promoList;
	private ProgressDialog progressBar;
	public int progressBarStatus;
	private Handler progressBarHandler = new Handler();

	public Exception eTmp;

	public static final String PREFS_NAME = "settings";

	private ArrayList<Periode> periodeList;

	private String test;

	private ErrorHandler errorHandler;
	private boolean first = false;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		errorHandler = new ErrorHandler(this);

		// Initialisation des elements UI
		this.tvPromo = (TextView) findViewById(R.id.tv_promo);
		this.spPeriodes = (Spinner) findViewById(R.id.spinner1);
		// this.spPromos = (Spinner) findViewById(R.id.spinner2);
		this.bSettings = (ImageButton) findViewById(R.id.imageButton1);
		this.webView = (WebView) findViewById(R.id.webView1);
		this.webView.getSettings().setBuiltInZoomControls(true);
		this.webView.getSettings().setUseWideViewPort(true);

		this.bSettings.setOnClickListener(this);
		this.spPeriodes.setOnItemSelectedListener(this);

		// Lecture des pref
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		this.thePromo = new Promotion(settings.getString("name", "null"),
				settings.getString("code", "null"));

		this.restorePromotion();
		this.populateSpinners();
		this.setTheCurrentPeriode();
		Log.e("!!!!!", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	private void setTheCurrentPeriode() {
		// TODO: Chercher dans les periode la plus proche et la mettre dans la
		// webview
	}

	private void restorePromotion() {

		if (this.thePromo.getCode().equalsIgnoreCase("null")
				|| this.thePromo.getName().equalsIgnoreCase("null")) {
			// TODO: Envoyer sur la futurewelcome activity
			this.thePromo.setCode("D0000000197");
			this.thePromo.setName("M1 Technologie de l'Internet");

			first = true;
		}

		/*
		 * TODO: Recuperation de la promo en xml et parsing Avec progress bar
		 */
		this.tvPromo.setText(this.thePromo.getName());
		// prepare for a progress bar dialog
		progressBar = new ProgressDialog(this);
		progressBar.setCancelable(true);
		progressBar.setMessage("Recupération du planning ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		// reset progress bar status
		progressBarStatus = 0;

		test = "";

		new Thread(new Runnable() {
			public void run() {

				URL url = null, urlList = null;
				try {
					url = new URL("http://www.irokwa.net/uppa/hp/promo-"
							+ MainActivity.this.thePromo.getCode() + ".xml");
					urlList = new URL(
							"http://www.irokwa.net/uppa/hp/promolist.xml");

				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				HttpURLConnection conn = null;
				HttpURLConnection connList = null;

				try {
					conn = (HttpURLConnection) url.openConnection();

					connList = (HttpURLConnection) urlList.openConnection();
					if (conn.getResponseCode() != 200
							|| connList.getResponseCode() != 200) {
						eTmp = new ProxyException();
						MainActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								errorHandler.newException(eTmp);
							}
						});
						return;
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// ConnectivityManager cm;
				// cm =
				// (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
				// cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
				// url = new
				// URL("http://www.irokwa.net/uppa/hp/promo-"+code+".xml");
				// conn = (HttpURLConnection) url.openConnection();
				// }

				try {
					periodeList = MyXMLParser.getPromo(thePromo.getCode(),
							conn.getInputStream());
					promoList = MyXMLParser.getPromos(connList.getInputStream());
					if (periodeList == null || promoList == null)
						throw new ProxyException();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					eTmp = e;
					MainActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							errorHandler.newException(eTmp);
						}
					});

				} catch (IOException e) {
					// TODO Auto-generated catch block
					eTmp = e;
					MainActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							errorHandler.newException(eTmp);
						}
					});

				} catch (ProxyException e) {
					// TODO Auto-generated catch block
					eTmp = e;
					MainActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							errorHandler.newException(eTmp);
						}
					});
					
				}

				if (periodeList != null && promoList != null)
				progressBarHandler.post(new Runnable() {
					public void run() {

						thePromo.setPeriodes(periodeList);
						populateSpinners();
						if (first) {
							showSettingsDialog();
							first = false;
						}
					}
				});

				// close the progress bar dialog
				progressBar.dismiss();

			}
		}).start();

	}

	private void populateSpinners() {
		if (this.thePromo != null) {
			ArrayAdapter<Periode> spinAdapter = new ArrayAdapter<Periode>(this,
					android.R.layout.simple_spinner_item,
					this.thePromo.getPeriodes());
			spinAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			this.spPeriodes.setAdapter(spinAdapter);
			this.spPeriodes.setSelection(spinAdapter.getPosition(this.thePromo
					.getCurrentPeriode()));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		webView.loadDataWithBaseURL(null,
				((Periode) spPeriodes.getSelectedItem()).toHtml(), "text/html",
				"UTF-8", null);

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButton1:
			showSettingsDialog();

			// Intent intent = new Intent(MainActivity.this,
			// ReglagesActivity.class);
			// intent.putExtra("promo", this.thePromo);
			//
			// startActivityForResult(intent, CODE_SETTINGS);
			break;

		}

	}

	private void showSettingsDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View alertDialogView = factory.inflate(R.layout.dialog_settings,
				null);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		adb.setView(alertDialogView);
		adb.setTitle("Réglages");
		adb.setIcon(android.R.drawable.ic_menu_manage);
		adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Spinner sp = (Spinner) alertDialogView
						.findViewById(R.id.spinner2);
				thePromo = (Promotion) sp.getSelectedItem();

				restorePromotion();

			}
		});

		adb.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		adb.show();
		Spinner sp = (Spinner) alertDialogView.findViewById(R.id.spinner2);
		ArrayAdapter<Promotion> spinAdapter2 = new ArrayAdapter<Promotion>(
				this, android.R.layout.simple_spinner_item, this.promoList);
		spinAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// TODO: add error msg
		if (this.promoList != null)
			sp.setAdapter(spinAdapter2);

		for (Promotion aPromo : promoList) {
			if (aPromo.equals(this.thePromo)) {
				Log.v("SPIN", "Position :" + spinAdapter2.getPosition(aPromo));
				sp.setSelection(spinAdapter2.getPosition(aPromo));
				break;
			}

		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			Bundle extras = intent.getExtras();
			if (extras.containsKey("promo")) {
				this.thePromo = (Promotion) extras.getSerializable("promo");

				restorePromotion();
			}
		}

	}

	@Override
	protected void onStop() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("code", this.thePromo.getCode());
		editor.putString("name", this.thePromo.getName());

		// Commit the edits!
		editor.commit();
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			showSettingsDialog();

			// Intent intent = new Intent(MainActivity.this,
			// ReglagesActivity.class);
			// intent.putExtra("promo", this.thePromo);
			//
			// startActivityForResult(intent, CODE_SETTINGS);
			return true;

		}
		return false;
	}
}
