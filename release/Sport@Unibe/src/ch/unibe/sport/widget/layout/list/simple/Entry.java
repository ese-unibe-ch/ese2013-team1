package ch.unibe.sport.widget.layout.list.simple;

import java.util.ArrayList;

import ch.unibe.sport.widget.layout.list.simple.List.OnEntryClickListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class Entry implements IEntry {

	private ArrayList<IMenuObserver> observers;
	private Context context;
	private String name;
	private IEntryView headerView;
	private IEntryView view;
	
	private OnEntryClickListener mOnEntryClickListener;
	
	private boolean invariant(){
		return context != null;
	}
	
	public Entry(Context context,String name){
		this.name = name;
		this.context = context;
		initView();
		assert invariant();
	}
	
	public Entry(Context context){
		this.context = context;
		assert invariant();
	}
	
	private void initView(){
		headerView = new EntryView(context, this,false);
	}
	
	@Override
	public void setView(IEntryView view){
		assert view != null;
		this.view = view;
	}
	
	protected void setHeaderView(IEntryView headerView){
		assert headerView != null;
		this.headerView = headerView;
	}
	
	@Override
	public void add(IEntry entry) {}

	@Override
	public boolean isLeaf() {
		assert invariant();
		return true;
	}

	@Override
	public boolean isComposite() {
		assert invariant();
		return false;
	}

	@Override
	public View getHeaderView() {
		assert headerView != null;
		assert invariant();
		return this.headerView.view();
	}

	@Override
	public View getView() {
		assert view != null;
		assert invariant();
		return this.view.view();
	}

	@Override
	public String getName(){
		assert invariant();
		return this.name;
	}

	@Override
	public void setOnClickListener(final OnClickListener l) {
		this.headerView.setOnClickListener(l);
	}
	
	@Override
	public void setOnEntryClickListener(OnEntryClickListener l){
		mOnEntryClickListener = l;
		this.headerView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (mOnEntryClickListener != null){
					mOnEntryClickListener.onEntryClickListener(Entry.this);
				}
			}
		});
	}

	@Override
	public void registerObserver(IMenuObserver observer) {
		assert observers != null;
		assert observer != null;
		assert !observers.contains(observer);
		observers.add(observer);
	}

	@Override
	public void removeObserver(IMenuObserver observer) {
		assert observers != null;
		assert observers.contains(observer);
		observers.remove(observer);
	}


	@Override
	public boolean isExpanded() {
		return false;
	}
	
	@Override
	public void notifyObserversEntryAdd(IEntry entry) {}

	@Override
	public void notifyObserversHeaderClicked(IEntryView entryView) {}

	@Override
	public void notifyObserversToCollapse() {}

	@Override
	public void registerBackButtonSubject(IBackButtonSubject subject) {}

	@Override
	public void removeBackButtonSubject(IBackButtonSubject subject) {}
	
}
