package ch.unibe.sport.main;

public interface IFilterable {
	public boolean isFilterExists();
	public void filter(String prefix);
}
