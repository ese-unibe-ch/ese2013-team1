package ch.unibe.sport.main;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MainActivityPagerAdapter extends PagerAdapter {
	public static final String[] TITLES = {"Sports List"/*,"Live"*/,"Favorites"/*,"Friends","Facebook Friends"*/};

	List<IMainTab> pages = null;
	
	public MainActivityPagerAdapter(List<IMainTab> pages) {
		this.pages = pages;
	}

	@Override
	public String getPageTitle(int position){
		return pages.get(position).getTitle();
	}
	
	@Override
    public Object instantiateItem(ViewGroup collection, int position){
        View v = pages.get(position).getView();
        collection.addView(v, 0);
        return v;
    }
    
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view){
        collection.removeView((View) view);
    }
    @Override
    public int getCount(){
        return pages.size();
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object){
        return view.equals(object);
    }

    @Override
    public void finishUpdate(View arg0){
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1){
    }

    @Override
    public Parcelable saveState(){
        return null;
    }

    @Override
    public void startUpdate(View arg0){
    }
}
