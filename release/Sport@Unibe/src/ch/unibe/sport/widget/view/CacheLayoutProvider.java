package ch.unibe.sport.widget.view;

import java.util.HashMap;
import java.util.Map;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;

public class CacheLayoutProvider implements LayoutProvider {

	private Map<CharSequence, Layout> layoutMap = new HashMap<CharSequence, Layout>();

	private int oldWidth = -2;

	public CacheLayoutProvider() {}

	@Override
	public Layout getLayout(CharSequence text, int width, TextPaint paint) {
		Layout layout = this.layoutMap.get(text);
		if (width != this.oldWidth) {
			layout = null;
			this.oldWidth = width;
			layout = new StaticLayout(text, paint, width, Alignment.ALIGN_CENTER, 1,0, false);
			this.layoutMap.put(text, layout);
		}
		return layout;
	}
}
