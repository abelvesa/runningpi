package com.hackathon.rpi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFrag extends Fragment {

	private WebView mWebView;
	private boolean mIsWebViewAvailable;
	private String mUrl = Constants.STREAM_IDLE_URL;

	/**
	 * Creates a new fragment which loads the supplied url as soon as it can
	 * 
	 * @param url
	 *            the url to load once initialised
	 */
	public WebViewFrag() {
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	/**
	 * Called to instantiate the view. Creates and returns the WebView.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mWebView != null) {
			mWebView.destroy();
		}
		mWebView = new WebView(getActivity());

		final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				getActivity().setProgress(progress * 100);// Make the bar
			}
		});

		mWebView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
					mWebView.goBack();
					return true;
				}
				return false;
			}
		});
		mWebView.setWebViewClient(new InnerWebViewClient()); // forces it to
																// open in app
		mWebView.loadUrl(mUrl);
		mIsWebViewAvailable = true;
		mWebView.setDrawingCacheEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		return mWebView;
	}

	/**
	 * Convenience method for loading a url. Will fail if {@link View} is not
	 * initialised (but won't throw an {@link Exception})
	 * 
	 * @param url
	 */
	public void loadUrl(String url) {
		if (mIsWebViewAvailable)
			getWebView().loadUrl(mUrl = url);
		else
			Log.w("ImprovedWebViewFragment",
					"WebView cannot be found. Check the view and fragment have been loaded.");
	}

	/**
	 * Called when the fragment is visible to the user and actively running.
	 * Resumes the WebView.
	 */
	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	/**
	 * Called when the fragment is no longer resumed. Pauses the WebView.
	 */
	@Override
	public void onResume() {
		mWebView.onResume();
		super.onResume();
	}

	/**
	 * Called when the WebView has been detached from the fragment. The WebView
	 * is no longer available after this time.
	 */
	@Override
	public void onDestroyView() {
		mIsWebViewAvailable = false;
		super.onDestroyView();
	}

	/**
	 * Called when the fragment is no longer in use. Destroys the internal state
	 * of the WebView.
	 */
	@Override
	public void onDestroy() {
		if (mWebView != null) {
			mWebView.destroy();
			mWebView = null;
		}
		super.onDestroy();
	}

	/**
	 * Gets the WebView.
	 */
	public WebView getWebView() {
		return mIsWebViewAvailable ? mWebView : null;
	}

	/* To ensure links open within the application */
	private class InnerWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			/*
			 * view.loadUrl(url); return true;
			 */
			return super.shouldOverrideUrlLoading(view, url);
		}

	}
}