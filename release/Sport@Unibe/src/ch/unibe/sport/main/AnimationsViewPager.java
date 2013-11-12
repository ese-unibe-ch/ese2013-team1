package ch.unibe.sport.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class AnimationsViewPager {
	
	public static ViewPager.PageTransformer getAnimation(Context ctx,int value){
		switch (value) {
		case 0:
			return new AnimationsViewPager.CardTransformer();
		case 1:
			return new AnimationsViewPager.ZoomOutPageTransformer();
		case 2:
			return new AnimationsViewPager.RotationTransformer();
		case 3:
			return new AnimationsViewPager.AccordionTransformer();
		case 4:
			return new AnimationsViewPager.XTransformer();
		case 5:
			return new AnimationsViewPager.YTransformer();
		case 6:
			return new AnimationsViewPager.DepthPageTransformer();
		case 7:
			return new AnimationsViewPager.ScaleFadePageTransformer(ctx);
		case 8:
			return new AnimationsViewPager.AlphaPageTransformer();
		case 9:
			return new AnimationsViewPager.TabletPageTransformer();
		default:
			return null;
		}
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DepthPageTransformer implements ViewPager.PageTransformer {
	    private float MIN_SCALE = 0.75f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
	            view.setAlpha(1);
	            view.setTranslationX(0);
	            view.setScaleX(1);
	            view.setScaleY(1);

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	            view.setAlpha(1 - position);

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);

	            // Scale the page down (between MIN_SCALE and 1)
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class TabletPageTransformer implements ViewPager.PageTransformer {
	    private static final float TRANSITION_SCREEN_ROTATION = 8.5f;
	    private final Matrix mMatrix = new Matrix();
	    private final Camera mCamera = new Camera();
	    private final float mTempFloat2[] = new float[2];
	    protected final static float mLayoutScale = 1.0f;
	    protected final static int mPageSpacing = 10;
	    
	    private final static int mMinimumWidth = 0;
	    
		public void transformPage(View view, float scrollProgress) {
			//float scrollProgress = getScrollProgress((int)scrollProgress, view, 0);
            float rotation = TRANSITION_SCREEN_ROTATION * -scrollProgress;
            float translationX = getOffsetXForRotation(rotation, view.getWidth(), view.getHeight());

            view.setTranslationX(translationX);
            view.setRotationY(rotation);
            if (true) {
                float alpha = 1 - Math.abs(scrollProgress)/2;
                view.setAlpha(alpha);
            }
	    }
	    
	    protected float getOffsetXForRotation(float degrees, int width, int height) {
	        mMatrix.reset();
	        mCamera.save();
	        //mCamera.setLocation(0, -2, 50);
	        mCamera.rotateY(Math.abs(degrees));
	        mCamera.getMatrix(mMatrix);
	        mCamera.restore();

	        mMatrix.preTranslate(-width * 0.5f, -height * 0.5f);
	        mMatrix.postTranslate(width * 0.5f, height * 0.5f);
	        mTempFloat2[0] = width;
	        mTempFloat2[1] = height;
	        mMatrix.mapPoints(mTempFloat2);
	        return (width - mTempFloat2[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
	    }
	    
	    protected float getScrollProgress(int screenScroll, View v, int page) {
	        final int halfScreenSize = v.getMeasuredWidth() / 2;
	        int screenCenter = screenScroll + v.getMeasuredWidth() / 2;

	        int totalDistance = getScaledMeasuredWidth(v) + mPageSpacing;
	        int delta = screenCenter - (0 - 0 + halfScreenSize);

	        float scrollProgress = delta / (totalDistance * 1.0f);
	        scrollProgress = Math.min(scrollProgress, 1.0f);
	        scrollProgress = Math.max(scrollProgress, -1.0f);
	        return scrollProgress;
	    }
	    
	    protected int getScaledMeasuredWidth(View child) {
	        // This functions are called enough times that it actually makes a difference in the
	        // profiler -- so just inline the max() here
	        final int measuredWidth = child.getMeasuredWidth();
	        final int minWidth = mMinimumWidth;
	        final int maxWidth = (minWidth > measuredWidth) ? minWidth : measuredWidth;
	        return (int) (maxWidth * mLayoutScale + 0.5f);
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
	    private static float MIN_SCALE = 0.85f;
	    private static float MIN_ALPHA = 0.5f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();
	        int pageHeight = view.getHeight();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 1) { // [-1,1]
	            // Modify the default slide transition to shrink the page as well
	            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
	            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
	            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
	            if (position < 0) {
	                view.setTranslationX(horzMargin - vertMargin / 2);
	            } else {
	                view.setTranslationX(-horzMargin + vertMargin / 2);
	            }

	            // Scale the page down (between MIN_SCALE and 1)
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	            // Fade the page relative to its size.
	            view.setAlpha(MIN_ALPHA +
	                    (scaleFactor - MIN_SCALE) /
	                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class ScaleFadePageTransformer implements ViewPager.PageTransformer {
		 
	    private int mScreenXOffset;
	    
	    @SuppressWarnings("deprecation")
		public ScaleFadePageTransformer(Context context) {
	        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
	        mScreenXOffset = display.getWidth()/2;
	    }
	    
	    @Override
	    public void transformPage(View page, float position) {
	        final float transformValue = Math.abs(Math.abs(position) - 1);
	        // apply fade effect
	        page.setAlpha(transformValue);
	        if (position > 0) {
	            // apply zoom effect only for pages to the right
	            page.setScaleX(transformValue);
	            page.setScaleY(transformValue);
	            page.setPivotX(0.5f);
	            final float translateValue = position * -mScreenXOffset;
	            if (translateValue > -mScreenXOffset) {
	                page.setTranslationX(translateValue);
	            } else {
	                page.setTranslationX(0);
	            }
	        }
	    }	    
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class AlphaPageTransformer implements ViewPager.PageTransformer {


        @Override
        public void transformPage(View v, float pos)
        {
            final float invt = Math.abs(Math.abs(pos) - 1);
            v.setAlpha(invt);
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class CardTransformer implements ViewPager.PageTransformer {
		 
		private final float scalingStart;
 
		public CardTransformer() {
			super();
			this.scalingStart = 1 - 0.7f;
		}
 
		@Override
		public void transformPage(View page, float position) {
			
			if (position >= 1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
 
			if ((position >= 0)&&(position < 1)) {
				final int w = page.getWidth();
				float scaleFactor = 1 - scalingStart * position;
 
				page.setAlpha(1 - position);
				page.setScaleX(scaleFactor);
				page.setScaleY(scaleFactor);
				page.setTranslationX(w * (1 - position) - w);
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class RotationTransformer implements ViewPager.PageTransformer {
		 
		private final float scalingStart;
 
		public RotationTransformer() {
			super();
			this.scalingStart = 1 - 0.7f;
		}
 
		@Override
		public void transformPage(View page, float position) {
			
			if (position >= 1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
 
			if ((position >= 0)&&(position < 1)) {
				final int w = page.getWidth();
				float scaleFactor = 1 - scalingStart * position;
 
				page.setAlpha(1 - position);
				page.setScaleX(scaleFactor);
				page.setScaleY(scaleFactor);
				page.setRotationY(360*(position));
				page.setTranslationX(w * (1 - position) - w);
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class AccordionTransformer implements ViewPager.PageTransformer {
		  
		public AccordionTransformer() {
			super();
		}
 
		@Override
		public void transformPage(View page, float position) {
			
			if (position >= 1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
 
			if ((position >= 0)&&(position < 1)) {
				final int w = page.getWidth();
				page.setScaleX(1);
				page.setAlpha(1);
//				page.setAlpha(1 - position);
//				page.setScaleX(1 - position);
				page.setScaleY(1 - position);
//				page.setRotationY(180*(position));
				page.setTranslationX(w * (1 - position) - w);
				
			}
			if (position < 0){
				page.setScaleX(1);
				page.setAlpha(1);
				final int w = page.getWidth();
				page.setScaleY(1 - Math.abs(position));
				page.setTranslationX(w * (1 - position) - w);
			}
			if (position < -1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class XTransformer implements ViewPager.PageTransformer {
		  
		public XTransformer() {
			super();
		}
 
		@Override
		public void transformPage(View page, float position) {
			
			if (position >= 1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
 
			if ((position >= 0)&&(position < 1)) {
				final int w = page.getWidth();
				page.setScaleX(1);				
				page.setScaleY(1);
				page.setAlpha(1-position);
				page.setRotationY(180*position);
				page.setTranslationX(w * (1 - position) - w);
				
			}
			if (position < 0){
				page.setScaleX(1);				
				page.setScaleY(1);
				page.setAlpha(1-Math.abs(position));				
				final int w = page.getWidth();
				page.setRotationY(180*position);
				page.setTranslationX(w * (1 - position) - w);
			}
			if (position < -1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class YTransformer implements ViewPager.PageTransformer {
		  
		public YTransformer() {
			super();
		}
 
		@Override
		public void transformPage(View page, float position) {
			
			if (position >= 1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
 
			if ((position >= 0)&&(position < 1)) {
				final int w = page.getWidth();
				page.setScaleX(1);				
				page.setScaleY(1);
				page.setAlpha(1-position);
				page.setRotationX(180*position);
				page.setTranslationX(w * (1 - position) - w);
				
			}
			if (position < 0){
				page.setScaleX(1);				
				page.setScaleY(1);
				page.setAlpha(1-Math.abs(position));				
				final int w = page.getWidth();
				page.setRotationX(180*position);
				page.setTranslationX(w * (1 - position) - w);
			}
			if (position < -1){
				page.setAlpha(0);
				page.setScaleX(0);
				page.setScaleY(0);
			}
		}
	}

}
