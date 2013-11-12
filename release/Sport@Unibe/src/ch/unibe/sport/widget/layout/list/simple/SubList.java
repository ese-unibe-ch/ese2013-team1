package ch.unibe.sport.widget.layout.list.simple;

import java.util.ArrayList;

import ch.unibe.sport.widget.layout.list.simple.List.OnEntryClickListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class SubList implements IEntry,IMenuObserver, IBackButtonObserver {

	private ArrayList<IEntry> entries;
	private ArrayList<IMenuObserver> observers;
	private ArrayList<IBackButtonSubject> subjects;
	private IEntryView headerView;
	private IEntryView view;
	
	private String name;
	private Context context;
	
	private boolean expanded = false;
	
	private int size = 0;
	
	private boolean invariant(){
		return entries != null
				&& size == entries.size()
				&& name != null
				&& name.length() > 0;
	}
	
	public SubList(Context context){
		entries = new ArrayList<IEntry>();
		observers = new ArrayList<IMenuObserver>();
		subjects = new ArrayList<IBackButtonSubject>();
		this.context = context;
		assert invariant();
	}
	
	public SubList(Context context,String name){
		entries = new ArrayList<IEntry>();
		observers = new ArrayList<IMenuObserver>();
		subjects = new ArrayList<IBackButtonSubject>();
		this.context = context;
		this.name = name;
		buildView();
		assert invariant();
	}
	
	private void buildView(){
		view = new SubListView(context,this,false);
		headerView = new EntryView(context,this,false);
		headerView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				notifyObserversHeaderClicked(view);
			}
		});
	}
	
	@Override
	public void add(IEntry entry) {
		assert invariant();
		initEntry(entry);
		assert invariant();
		notifyObserversEntryAdd(entry);
		assert subjects != null;
		for (IBackButtonSubject subject : subjects){
			entry.registerBackButtonSubject(subject);
		}	
	}

	private void initEntry(IEntry entry){
		entries.add(entry);
		if (entry.isComposite()){
			entry.registerObserver(this);
		}
		size++;
	}

	private void refreshView(){
		assert view != null;
		assert entries != null;
		assert expanded;
		view.clear();
		for (IEntry entry : entries){
			notifyObserversEntryAdd(entry);
		}
		expanded = false;
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isComposite() {
		return true;
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
	public View getView() {
		assert view != null;
		return this.view.view();
	}

	@Override
	public View getHeaderView() {
		assert headerView != null;
		return this.headerView.view();
	}

	@Override
	public void notifyObserversEntryAdd(IEntry entry) {
		assert observers != null;
		for (IMenuObserver observer : observers){
			observer.notifyEntryAdd(entry);
		}
	}
	
	@Override
	public String getName() {
		assert invariant();
		return this.name;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.headerView.setOnClickListener(l);
	}
	

	@Override
	public void setOnEntryClickListener(OnEntryClickListener l) {
		for (IEntry entry : entries){
			entry.setOnEntryClickListener(l);
		}
	}

	@Override
	public void setView(IEntryView entryView) {
		assert entryView != null;
		view.setView(entryView);
	}

	@Override
	public void notifyEntryHeaderClicked(IEntryView entryView) {
		assert !expanded;
		this.setView(entryView);
		expanded = true;
	}

	@Override
	public void notifyObserversHeaderClicked(IEntryView entryView) {
		assert observers != null;
		for (IMenuObserver observer : observers){
			observer.notifyEntryHeaderClicked(entryView);
		}
	}
	
	@Override
	public void notifyEntryAdd(IEntry entry) {}

	@Override
	public void notifyToCollapse() {
		assert expanded;
		this.refreshView();
	}

	@Override
	public void notifyObserversToCollapse() {
		assert observers != null;
		if (!expanded) {
			for (IMenuObserver observer : observers){
				observer.notifyToCollapse();
			}
		}
	}

	@Override
	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void registerSubject(IBackButtonSubject subject) {
		assert subjects != null;
		assert subjects != null;
		assert !subjects.contains(subject);
		subjects.add(subject);
		subject.registerObserver(this);
		for (IEntry entry : entries){
			entry.registerBackButtonSubject(subject);
		}
	}

	@Override
	public void removeSubject(IBackButtonSubject subject) {
		assert subjects != null;
		assert subjects.contains(subject);
		subjects.remove(subject);
		subject.removeObserver(this);
		for (IEntry entry : entries){
			entry.removeBackButtonSubject(subject);
		}
	}

	@Override
	public void notifyBackButton() {
		notifyObserversToCollapse();
	}

	@Override
	public void registerBackButtonSubject(IBackButtonSubject subject) {
		this.registerSubject(subject);
	}

	@Override
	public void removeBackButtonSubject(IBackButtonSubject subject) {
		this.removeSubject(subject);
	}
}
