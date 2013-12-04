package ch.unibe.sport.utils.bulker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BulkTable {

	String value();

}
