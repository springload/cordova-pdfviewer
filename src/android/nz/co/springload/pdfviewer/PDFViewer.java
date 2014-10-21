package nz.co.springload.pdfviewer;

import android.os.Bundle;
import android.util.Log;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import org.json.*;
import org.apache.cordova.*;


public class PDFViewer extends CordovaPlugin
{
	private Downloader activeDownload;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView)
	{
		super.initialize(cordova, webView);
	}

	public static boolean canDisplayPdf(Context context) {
		PackageManager packageManager = context.getPackageManager();
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		testIntent.setType("application/pdf");
		if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		if (action.equals("load")) {

			activeDownload = new Downloader((String)args.get(0), new Downloader.Callback() {

				@Override
				public void onDownloadComplete(final Downloader downloader)
				{
					if (downloader.downloadUri != null)
					{
						final Activity activity = cordova.getActivity();
						activity.runOnUiThread(new Runnable()
						{
							public void run()
							{
								try
								{

									if (canDisplayPdf(cordova.getActivity())) {

										Intent target = new Intent(Intent.ACTION_VIEW);
										target.setDataAndType(downloader.downloadUri, "application/pdf");

										Intent intent = Intent.createChooser(target, "Open File");
										activity.startActivity(intent);
										callbackContext.success();
									}
									else {
										callbackContext.error("noApplication");
									}
								}
								catch (ActivityNotFoundException e)
								{
									// Instruct the user to install a PDF reader here, or something
									callbackContext.error("noApplication");
								}
							}
						});
					}
					else
					{
						callbackContext.error(downloader.downloadError);
					}
				}
			});

			return true;
		}
		return false;
	}
}
