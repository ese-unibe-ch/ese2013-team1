package ch.unibe.sport.widget.layout.list.simple;

import android.view.View;
import android.view.View.OnClickListener;

public interface IEntryView {
	public void setOnClickListener(OnClickListener l);
	public View view();
	public void setView(IEntryView entryView);
	public void clear();
	public void useSimpleView();
}
