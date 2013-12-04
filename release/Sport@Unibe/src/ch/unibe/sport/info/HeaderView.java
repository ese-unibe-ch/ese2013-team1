package ch.unibe.sport.info;

import ch.unibe.sport.R;
import ch.unibe.sport.core.Event;
import ch.unibe.sport.utils.Print;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Team 1 2013
 */
public class HeaderView extends LinearLayout {

	private final static int borderWidth = 3;
	private final static float radius = 12f;
	private final static int borderColor = 0xffc9c7c7;
	
	protected HeaderViewHolder holder;
	
	public HeaderView(Context context) {
		super(context);
		init();
	}
	
	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		View.inflate(getContext(), R.layout.sport_info_entry,this);
		holder = new HeaderViewHolder(this);
	}
	
	/*
	 * Drawing background for entry. Uses 2 RoundRectShapes for border and background
	 */
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	private void initBackground(Event event) {
		final int backgroundColor = event.isFavorite() ? event.getBackground() : Color.WHITE;
		
		RoundRectShape borderRect = new RoundRectShape(
				new float[] {
						radius, radius,
						radius, radius,
						radius, radius,
						radius, radius
				},	
				new RectF(borderWidth,borderWidth,borderWidth,borderWidth),
				new float[] {
					radius, radius,
					radius, radius,
					radius, radius,
					radius, radius});
		
		RoundRectShape backgroundRect = new RoundRectShape(
				new float[] {
						radius, radius,
						radius, radius,
						radius, radius,
						radius, radius}
				, null, null);
		
		ShapeDrawable border = new ShapeDrawable(borderRect);
		ShapeDrawable background = new ShapeDrawable(backgroundRect);


		
		background.setShaderFactory(new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0, 0, 0, height,
						new int[] {backgroundColor, backgroundColor, backgroundColor, backgroundColor },
						new float[] {0, 0.50f, 0.49f, 1},
						Shader.TileMode.REPEAT);
				return lg;
			}
		});
		
		border.setShaderFactory(new ShapeDrawable.ShaderFactory() {

			@Override
			public Shader resize(int width, int height) {
				LinearGradient lg = new LinearGradient(0, 0, 0, height,
						new int[] {borderColor,borderColor,borderColor,borderColor},
						new float[] { 0, 0.5f, 0.49f, 1 },
						Shader.TileMode.REPEAT);
				return lg;
			}
		});

		LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] {background,border});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			this.findViewById(R.id.root).setBackground(layerDrawable);
		}
		else {
			this.findViewById(R.id.root).setBackgroundDrawable(layerDrawable);
		}
	}

	public void setEvent(Event event){
		if (event == null) {
			Print.err("SportEntryHeaderView","Course is null");
			return;
		}
		holder.course.setText(event.getEventName());
		holder.day.setText(event.getDaysOfWeekString());
		holder.time.setText(event.getTimeString());
		holder.place.setText(event.getPlace());

		initBackground(event);
	}
	
	/**
	 * 
	 * @author Team 1 2013
	 */
	protected class HeaderViewHolder {
		
		protected final TextView course;
		protected final TextView day;
		protected final TextView time;
		protected final TextView place;
		
		private HeaderViewHolder(HeaderView header){
			course = (TextView) header.findViewById(R.id.course);
			day = (TextView) header.findViewById(R.id.day);
			time = (TextView) header.findViewById(R.id.time);
			place = (TextView) header.findViewById(R.id.place);
		}
	}
}