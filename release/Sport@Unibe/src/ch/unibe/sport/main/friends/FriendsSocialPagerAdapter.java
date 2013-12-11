package ch.unibe.sport.main.friends;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class FriendsSocialPagerAdapter extends PagerAdapter {

	private ArrayList<IFriendsSocialTab> tabs;
	
	public FriendsSocialPagerAdapter(){
		tabs = new ArrayList<IFriendsSocialTab>();
	}
	
	@Override
	public int getCount() {
		return tabs.size();
	}
	
	public void addTab(IFriendsSocialTab tab){
		this.tabs.add(tab);
	}
	
	public IFriendsSocialTab getTab(int index){
		return tabs.get(index);
	}
	
	@Override
    public Object instantiateItem(ViewGroup collection, int position){
        View v = tabs.get(position).getView();
        collection.addView(v, 0);
        return v;
    }
    
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view){
        collection.removeView((View) view);
    }

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

}
