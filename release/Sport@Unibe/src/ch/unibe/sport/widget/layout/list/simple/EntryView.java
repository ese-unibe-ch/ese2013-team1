package ch.unibe.sport.widget.layout.list.simple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class EntryView extends FrameLayout implements IEntryView {

	private IEntry entry;
	private TextView nameView;
	private ImageView infoView;
		
	public EntryView(Context context, IEntry entry) {
		super(context);
		assert context != null;
		assert entry != null;
		this.entry = entry;
	}
	
	public EntryView(Context context, IEntry entry, boolean custom) {
		super(context);
		assert context != null;
		assert entry != null;
		this.entry = entry;
		if (!custom){
			useSimpleView();
		}
	}
	
	@Override
	public void useSimpleView(){
		setLayout(android.R.layout.simple_list_item_2);
        nameView = (TextView) this.findViewById(android.R.id.text1);
        this.setName(entry.getName());
	}
	
	protected void setLayout(int resID){
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resID, this);
        inflater = null;
	}
		
	protected void setInfoImage(int resourceID){
		if (infoView != null)infoView.setImageResource(resourceID);
	}
	
	
	@Override
	public void setOnClickListener(OnClickListener l){
		super.setOnClickListener(l);
	}

	@Override
	public View view() {
		return this;
	}

	private void setName(String str) {
		assert nameView != null;
		nameView.setText(str);
	}
	
	protected IEntry getEntry(){
		assert entry != null;
		return entry;
	}

	@Override
	public void setView(IEntryView entryView) {
		assert entryView != null;
		this.removeAllViews();
		addView(entryView.view());
	}

	@Override
	public void clear() {
		this.removeAllViews();
	}
	
	
}
