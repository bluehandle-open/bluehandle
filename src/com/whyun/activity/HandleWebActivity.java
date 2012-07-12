package com.whyun.activity;

import com.whyun.IBlueToothConst;
import com.whyun.bluetooth.R;

import com.whyun.communication.bluetooth.ServerSocketThread;
//import com.sunny.message.key.HandleKeys;

import android.app.Activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.DisplayMetrics; 
import android.util.Log;

public class HandleWebActivity extends Activity implements IBlueToothConst {
//	private static float MIN_WEB_WIDTH = 708;//�������С�Ŀ��
	private static final String LOG_TAG = "js";
	private WebView browser = null;
	private float width;
	private float height;
	private ServerSocketThread serverThread = ServerSocketThread.getInstance();

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
		 		WindowManager.LayoutParams. FLAG_FULLSCREEN);
		setContentView(R.layout.web);
		 
		browser = (WebView)findViewById(R.id.webview);		
		
		WebSettings settings = browser.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(false);//���
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		DisplayMetrics dm = new DisplayMetrics();
 		
 		if (dm.densityDpi == DisplayMetrics.DENSITY_HIGH) {
 			//settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
 			System.out.println("-------the desity is high.--------");
 		} else if (dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
 			//settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
 			System.out.println("-------the desity is medium.--------");
 		} else {
 			//settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
 			System.out.println("-------the desity is low.--------");
 		}
 			
 		
 		browser.setWebViewClient(new WebViewClient() { 			
 			 public boolean shouldOverrideUrlLoading(WebView view, String url) {
 				System.out.println("the url now is " + url);
 				view.loadUrl(url);
 				return true;
 			}			 
 		});	
 		browser.setWebChromeClient(new MyWebChromeClient());
 		
 		getWindowManager().getDefaultDisplay().getMetrics(dm);
// 		float density  = dm.density;
// 		width = dm.widthPixels/density;
// 		height = dm.heightPixels/density;
 		width = dm.widthPixels;
 		height = dm.heightPixels;
// 		System.out.println("the dm is " + dm.widthPixels + "*" + dm.heightPixels); 		
 		System.out.println("---------------width:-------------------" + width);
 		System.out.println("---------------height:------------------" + height);
 		browser.addJavascriptInterface(new HandleJsInterface(), "handle");
 		
 		try {			
 			browser.loadUrl("file:///android_asset/index_new.html"); 			
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
    }

    final class HandleJsInterface {
    	
    	public float getWidth() {
    		return width;
    	}
    	public float getHeight() {
    		return height;
    	}
    	public String press(String buttonId) {
    		String result = "error";
    		Log.i(LOG_TAG,"press " + buttonId);
    		serverThread.sendMsg(buttonId, toPress);
    		return result;
    	}
    	public String release(String buttonId) {
    		String result = "error";
    		Log.i(LOG_TAG,"release " + buttonId);
    		serverThread.sendMsg(buttonId,toRelease);
    		return result;
    	}
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		serverThread.close();
		browser = null;
	}

	final class MyWebChromeClient extends WebChromeClient {
		public boolean onJsAlert(WebView view, String url, String message,
				 final android.webkit.JsResult result) {
			Log.d(LOG_TAG, message);
			result.confirm();

			return false;
		}
		
    }
}