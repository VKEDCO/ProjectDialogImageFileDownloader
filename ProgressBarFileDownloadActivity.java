package org.vkedco.mobaddpev.progressbardemo_network;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ProgressBarFileDownloadActivity extends Activity implements OnClickListener {

	Button mBtnStartProgress;
	ImageView mImageView;
	ProgressDialog mProgressBar;
	int mProgressBarStatus=0;
	Handler mProgressBarHandler=new Handler();
	long mFileSize=0;
	static final String PROGRESS_MESSAGE="File Downloading ..."; 
	static final String COMPLETION_MESSAGE="Download Progress ..."; 
	static final String FILE_URL = "http://www.ibiblio.org/wm/paint/auth/hiroshige/dyers.jpg";
	static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg"; 
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressbar_file_download_layout);
		
		mBtnStartProgress=(Button)findViewById(R.id.btnStartProgress);
		mImageView=(ImageView)findViewById(R.id.FileDownloadimageView);
		mBtnStartProgress.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.progress_bar_demo, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		
		//Prepare the progressbar
		mProgressBar=new ProgressDialog(v.getContext());
		mProgressBar.setCancelable(true);
		mProgressBar.setMessage(PROGRESS_MESSAGE);
		mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressBar.setProgress(0);
		mProgressBar.setMax(100);
		mProgressBar.show();
		
		//reset status
		mProgressBarStatus=0;
		
		//reset fileSize
		mFileSize=0;
		
		Thread thread = new Thread(){
		    public void run(){
		      updateProgressBar();
		    }
		  };
		 
		  thread.start();
		
	}
	
	public void updateProgressBar()
	{
		while(mProgressBarStatus!=100)
		{
			
			int count;
			try{
				URL url = new URL(FILE_URL);
		        URLConnection conection = url.openConnection();
		        conection.connect();
		        // getting file length
		        int lenghtOfFile = conection.getContentLength();

		        // input stream to read file - with 8k buffer
		        InputStream input = new BufferedInputStream(url.openStream(), 8192);

		        // Output stream to write file
		        OutputStream output = new FileOutputStream(FILE_PATH);

		        byte data[] = new byte[1024];

		        long total = 0;

		        while ((count = input.read(data)) != -1) {
		            total += count;

		            // writing data to file
		            output.write(data, 0, count);
		            // publishing the progress....
		            // After this onProgressUpdate will be called
		           mProgressBarStatus= (int)((total*100)/lenghtOfFile);
		           
		         //update the progress bar
					mProgressBarHandler.post(new Runnable() {
						
						@Override
						public void run() {
							
							mProgressBar.setProgress(mProgressBarStatus);
							if(mProgressBarStatus==100)
								{								
									mProgressBar.setMessage(COMPLETION_MESSAGE);
									mImageView.setImageDrawable(Drawable.createFromPath(FILE_PATH));									
								}
						}
					});
		        }

		        // flushing output
		        output.flush();

		        // closing streams
		        output.close();
		        input.close();

		    } catch (Exception e) {
		        Log.e("Error: ", e.getMessage());
		    }

			
			
		}		
		//after Download is complete
		if(mProgressBarStatus>=100)
		{			
			// sleep 2 seconds, so that you can see the 100%
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			mProgressBar.dismiss();
		}
	}

	

}
