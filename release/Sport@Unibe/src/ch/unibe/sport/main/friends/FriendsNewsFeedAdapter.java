package ch.unibe.sport.main.friends;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.unibe.sport.R;
import ch.unibe.sport.core.News;
import ch.unibe.sport.event.info.EventInfoActivity;

public class FriendsNewsFeedAdapter extends BaseAdapter {

	private News[] news;
	private RelativeLayout tmpView;
	private NewsViewHolder viewHolder;
	@SuppressLint("SimpleDateFormat")
	private final SimpleDateFormat stampFormat = new SimpleDateFormat("dd LLL yyyy 'at' k:m");
	public FriendsNewsFeedAdapter(News[] news){
		this.news = news;
	}
	
	@Override
	public int getCount() {
		return news.length;
	}

	@Override
	public Object getItem(int position) {
		return news[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean areAllItemsEnabled() {
	    return false;
	}

	@Override
	public boolean isEnabled(int position) {
	  return false;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		tmpView = (RelativeLayout) convertView;
		if (tmpView == null){
			tmpView = (RelativeLayout) View.inflate(parent.getContext(), R.layout.friend_news_entry, null);
			tmpView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,AbsListView.LayoutParams.WRAP_CONTENT));
			viewHolder = new NewsViewHolder(tmpView);
			tmpView.setTag(viewHolder);
		}
		else {
			viewHolder = (NewsViewHolder) tmpView.getTag();
		}
		viewHolder.userName.setText((news[position].getUsername() != null && news[position].getUsername().length() > 0)
				? news[position].getUsername() : news[position].getNickname());
		viewHolder.userName.setTag(Integer.valueOf(news[position].getUserID()));
		viewHolder.userName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FriendInfoActivity.show(v.getContext(), (Integer) v.getTag());
			}
		});
		SpannableString eventName = new SpannableString(news[position].getEventName());
	    eventName.setSpan(new UnderlineSpan(), 0,eventName.length(), 0);
		viewHolder.eventName.setText(eventName);
		viewHolder.eventName.setTag(news[position].getHash());
		viewHolder.eventName.setOnTouchListener(new OnTouchListener() {
			private static final int defaultColor = 0xff222222;
			private static final int clickColor = 0xff0099cc;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView text = (TextView)v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					text.setTextColor(clickColor);
				}
				else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					text.setTextColor(defaultColor);
				}
				else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					text.setTextColor(defaultColor);
				}
				else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					text.setTextColor(clickColor);
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {
					text.setTextColor(defaultColor);
				}
				return v.onTouchEvent(event);
			}
		});
		viewHolder.eventName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				EventInfoActivity.show(v.getContext(), v.getTag().toString());
			}
		});
		viewHolder.eventDate.setText(news[position].getDate().print());
		viewHolder.stamp.setText(stampFormat.format(news[position].getStamp()));
		return tmpView;
	}
	
	private class NewsViewHolder {
		private final TextView userName;
		private final TextView eventName;
		private final TextView eventDate;
		private final TextView stamp;
		
		private NewsViewHolder(ViewGroup view){
			userName = (TextView) view.findViewById(R.id.username);
			eventName = (TextView) view.findViewById(R.id.event_name);
			eventDate = (TextView) view.findViewById(R.id.event_date);
			stamp = (TextView) view.findViewById(R.id.stamp);
		}
	}

}
