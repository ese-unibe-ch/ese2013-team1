package ch.unibe.sport.widget.layout.list.simple;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;

public abstract class List implements IBackButtonObserver{
	
	private Context context;
	private SubList rootMenu;
	private ArrayList<IBackButtonSubject> subjects;
	
	private boolean invariant(){
		return context != null && rootMenu != null;
	}
		
	public interface OnEntryClickListener{
		public void onEntryClickListener(IEntry entry);
	}
	
	public List(Context context){
		this.context = context;
		subjects = new ArrayList<IBackButtonSubject>();
		assert context != null;
		rootMenu = new SubList(context,"root");
		onCreateList();
		assert invariant();
	}
	
	protected abstract void onCreateList();
	
	public void add(IEntry entry){
		assert entry != null;
		rootMenu.add(entry);
	}
	
	public View getView(){
		return rootMenu.getView();
	}
	
	public Context getContext(){
		return context;
	}
	
	public boolean isExpanded(){
		return rootMenu.isExpanded();
	}

	public void setOnEntryClickListener(OnEntryClickListener l){
		rootMenu.setOnEntryClickListener(l);
	}
	
	@Override
	public void registerSubject(IBackButtonSubject subject) {
		assert subjects != null;
		assert subjects != null;
		assert !subjects.contains(subject);
		subjects.add(subject);
		subject.registerObserver(this);
		rootMenu.registerBackButtonSubject(subject);
	}

	@Override
	public void removeSubject(IBackButtonSubject subject) {
		assert subjects != null;
		assert subjects.contains(subject);
		subjects.remove(subject);
		subject.removeObserver(this);
		rootMenu.removeBackButtonSubject(subject);
	}

	@Override
	public void notifyBackButton() {}
}
