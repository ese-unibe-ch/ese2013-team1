package ch.unibe.sport.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.unibe.sport.DBAdapter.tables.Sports;
import ch.unibe.sport.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class SportsListAdapter extends ArrayAdapter<String> {
	private final Object lock = new Object();
	
	private String[][] sportsData;
	private String[] sportNames;
	
	private ArrayList<String> originalSportIDs;
	private ArrayList<String> sportIDs;
	private SportsListFilter filter;
	
	public SportsListAdapter(Context context, String[][] sportsData) {
		super(context, android.R.layout.simple_list_item_activated_1,
				new ArrayList<String>(Arrays.asList(Utils.getRow(Utils.transpose(sportsData),Sports.SPORT))));
		this.sportsData = sportsData;
		this.sportNames = Utils.getRow(Utils.transpose(sportsData),Sports.SPORT);
		this.sportIDs = new ArrayList<String>(Arrays.asList(Utils.getRow(Utils.transpose(sportsData),Sports.SID)));
		this.originalSportIDs = new ArrayList<String>(Arrays.asList(Utils.getRow(Utils.transpose(sportsData),Sports.SID)));
	}
	
	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new SportsListFilter();
		}
		return filter;
	}

	public int getIndex(int position){
		return originalSportIDs.indexOf(this.sportIDs.get(position));
	}
	
	/**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    @SuppressLint("DefaultLocale")
    private class SportsListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list;
                ArrayList<String> ids;
                synchronized (lock) {
                    list = new ArrayList<String>(Arrays.asList(sportNames));
                    ids = new ArrayList<String>(Arrays.asList(Utils.getRow(Utils.transpose(sportsData),Sports.SID)));
                }
                sportIDs = ids;
                results.values = list;
                results.count = list.size();
            }
            else {
                String prefixString = prefix.toString().toLowerCase(Locale.getDefault());

                ArrayList<String> values;
                ArrayList<String> ids = new ArrayList<String>();
                synchronized (lock) {
                    values = new ArrayList<String>(Arrays.asList(sportNames));
                }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<String>();

                for (int i = 0; i < count; i++) {
                	final String value = values.get(i);
                    final String valueText = value.toString().toLowerCase();
                    
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                        ids.add(sportsData[i][Sports.SID]);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                ids.add(sportsData[i][Sports.SID]);
                                break;
                            }
                        }
                    }
                }

                sportIDs = ids;
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
		@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        	clear();
            addAll((List<String>) results.values);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

}
