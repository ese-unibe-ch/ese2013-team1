package ch.unibe.sport.widget.layout;

import android.view.View;

/**
 * Interface for all views, that should be used with CollapsableLayout
 * to hide or show.
 * 
 * @version 1.0 2013-08-27
 * @author Aliaksei Syrel
 */
public interface Hiddable {
	public void hide();
	public void show();
	public boolean isLocked();
	public boolean isVisible();
	public View getView();
	public void setInitInBackground(boolean background);
	public void setProgressBarShown(boolean shown);
}
