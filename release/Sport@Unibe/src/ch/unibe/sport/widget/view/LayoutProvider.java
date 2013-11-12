package ch.unibe.sport.widget.view;

import android.text.Layout;
import android.text.TextPaint;

public interface LayoutProvider {
	Layout getLayout(CharSequence text, int width, TextPaint textPaint);
}
