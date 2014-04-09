package nz.co.springload.pdfviewer;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.net.Uri;
import android.webkit.CookieManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Downloader extends AsyncTask<String, Void, Void>
{
	public static abstract class Callback
	{
		public abstract void onDownloadComplete(Downloader downloader);
	}

	public Uri downloadUri;
	public String downloadError;

	private String url;
	private Callback callback;

	public Downloader(String url, Callback callback)
	{
		this.url = url;
		this.callback = callback;
		execute(url);
	}

	@Override
	protected Void doInBackground(String... url)
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url[0]);

		String cookies = CookieManager.getInstance().getCookie(url[0]);
		if (cookies != null)
			httpGet.setHeader("Cookie", cookies);

		try
		{
			File file = null;
			FileOutputStream outputStream = null;

			try {
				file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.pdf");
				outputStream = new FileOutputStream(file);
			}
			catch (IOException e)
			{
				downloadError = "storageFail";
				return null;
			}

			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			int totalBytes = 0;
			if (statusCode == 200)
			{
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();

				int bytes = 0;
                byte buf[] = new byte[1024];
				while ((bytes = content.read(buf, 0, 1024)) >= 0)
				{
					outputStream.write(buf, 0, bytes);
					totalBytes += bytes;
				}

				downloadUri = Uri.fromFile(file);
				Log.v("PDF", "Downloaded " + url[0] + " - " + totalBytes + " bytes to " + downloadUri.toString());
			}
			else
			{
				downloadError = "downloadFail";
			}
		}
		catch (Exception e)
		{
			downloadError = "downloadFail";
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void buffer)
	{
		callback.onDownloadComplete(this);
	}
}
