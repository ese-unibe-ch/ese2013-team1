package ch.unibe.sport.main.initialization;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import ch.unibe.sport.R;
import ch.unibe.sport.config.Config;


public class InitializationViewController {
	
	private static InitializationViewHolder holder;
	private static InitializationActivity mInit;
	
	private final static float SB_SHADOW_RADIUS = 3f;
	private final static float SB_SHADOW_DX = 2f;
	private final static float SB_SHADOW_DY = 2f;
	
	private LoadingButtonUpdaterTask loadingUpdaterTask;
	private Timer loadingUpdaterTimer;
	
	public InitializationViewController(InitializationActivity init){
		mInit = init;
		holder = new  InitializationViewHolder(mInit);
	}
	
	
	public void setStatusButtonCompleted(){
		loadingUpdaterTimer.cancel();
		holder.loadingStatusButton.setText(R.string.initialization_status_button_completed);
		holder.loadingStatusButtonHelper.setVisibility(View.GONE);
		holder.loadingStatusButtonContainer.setBackgroundResource(R.drawable.gradient_green_simple);
		holder.loadingStatusButton.setShadowLayer(SB_SHADOW_RADIUS, SB_SHADOW_DX, SB_SHADOW_DY, 0xff7da600);
		holder.loadingStatusButtonContainer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mInit.finish();
			}
		});
	}
	
	public void setStatusButtonLoading(){
		holder.loadingStatusButtonContainer.setOnClickListener(null);
		holder.loadingStatusButton.setText(R.string.initialization_status_button_loading);
		holder.loadingStatusButtonHelper.setVisibility(View.VISIBLE);
		holder.loadingStatusButtonContainer.setBackgroundResource(R.drawable.gradient_orange_simple);
		holder.loadingStatusButton.setShadowLayer(SB_SHADOW_RADIUS, SB_SHADOW_DX, SB_SHADOW_DY, 0xffed9e00);
		holder.loadingStatusButtonHelper.setShadowLayer(SB_SHADOW_RADIUS, SB_SHADOW_DX, SB_SHADOW_DY, 0xffed9e00);
		initLoadingButtonTimer();
	}
	
	public void setStatusButtonError(){
		loadingUpdaterTimer.cancel();
		holder.loadingStatusButton.setText(R.string.initialization_status_button_error);
		holder.loadingStatusButtonHelper.setVisibility(View.GONE);
		holder.loadingStatusButtonContainer.setBackgroundResource(R.drawable.gradient_red_simple);
		holder.loadingStatusButton.setShadowLayer(SB_SHADOW_RADIUS, SB_SHADOW_DX, SB_SHADOW_DY, 0xffde0000);
		holder.loadingStatusButtonContainer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Config.INST.finishApp(mInit);
			}
		});
	}
	
	private void initLoadingButtonTimer(){
		loadingUpdaterTimer = new Timer();
        loadingUpdaterTask = new LoadingButtonUpdaterTask();
        loadingUpdaterTimer.schedule(loadingUpdaterTask, 0, 750);
    }

    private class LoadingButtonUpdaterTask extends TimerTask {
        @Override
        public void run() {
        	/* if acivity is destroyed cancel task */
        	if (mInit == null) {
        		loadingUpdaterTimer.cancel();
        	}
            LoadingUpdater.sendEmptyMessage(0);
        }
    }

    private static Handler LoadingUpdater = new Handler(){
    	private int counter = 0;
    	private final String[] dots = new String[]{"",".","..","..."};
    	
        @Override
        public void handleMessage(Message msg) {
            if (counter > 3) counter = 0;
            holder.loadingStatusButtonHelper.setText(dots[counter]);
            counter++;
        }
    };
}
