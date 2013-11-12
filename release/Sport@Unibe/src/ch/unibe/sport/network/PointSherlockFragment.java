package ch.unibe.sport.network;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.unibe.sport.main.OnFragmentAttachListener;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class PointSherlockFragment extends SherlockFragment implements IPointable {

	private String tag;
	private OnFragmentAttachListener mOnFragmentAttachListener;
	private Point point;
	
	public PointSherlockFragment(String tag){
		this.tag = tag;
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if (mOnFragmentAttachListener != null){
			this.mOnFragmentAttachListener.onAttach(this);
		}
	}
	
	public void setOnFragmentAttachListener(OnFragmentAttachListener l){
		this.mOnFragmentAttachListener = l;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		point = Point.initialize(this);
		if (this.getActivity() instanceof IProxyable){
			point.connect((IProxyable)this.getActivity());
		}
		return onCreated(inflater,container,savedInstanceState);
	}
	
	public abstract View onCreated(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState);
		
	/**
	 * Prevents fragment recreation memory leak.
	 */
	@Override
	public void onDestroyView() {
		point.disconnect();
		super.onDestroyView();
	}
	
	@Override
	public String tag() {
		return tag;
	}
	
	@Override
	public void send(Message message){
		point.send(message);
	}
	
	@Override
	public void connect(IProxyable proxy){
		point.connect(proxy);
	}
}
