package nz.co.springload.pdfviewer;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
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
									Intent target = new Intent(Intent.ACTION_VIEW);
									target.setDataAndType(downloader.downloadUri, "application/pdf");


									Intent intent = Intent.createChooser(target, "Open File");

									activity.startActivity(intent);
									callbackContext.success();
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

