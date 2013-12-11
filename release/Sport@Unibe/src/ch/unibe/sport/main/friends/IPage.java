package ch.unibe.sport.main.friends;

import ch.unibe.sport.network.IPointable;

import com.octo.android.robospice.SpiceManager;

import android.view.View;

public interface IPage {
	public void setOnPageSwitchRequestListener(OnPageSwitchRequestListener l);
	public View getView();
	public void setSpiceManager(SpiceManager spiceManager);
	public void initialize();
	public void connect(IPointable point);
}
