package ch.unibe.sport.main.search;

import ch.unibe.sport.core.Time;
import ch.unibe.sport.widget.view.SeekBarAdapter;

public class TimeSeekBarAdapter implements SeekBarAdapter<Time> {

	private final Time[] times;
	
	public TimeSeekBarAdapter(int hourFrom, int hourTo){
		times = new Time[Math.abs(hourTo-hourFrom+1)];
		Time tmp = new Time(hourFrom,0);
		for (int i = 0, length = times.length;i < length;i++){
			times[i] = tmp.copy();
			tmp.nextHour();
		}
	}
	
	@Override
	public int getCount() {
		return times.length;
	}

	@Override
	public String getStringValue(int index) {
		return times[index].toString();
	}

	@Override
	public boolean isValueDisplayed(int index) {
		return (index+1) % 3 == 0;
	}

	@Override
	public Time getValue(int index) {
		return times[index];
	}

}
