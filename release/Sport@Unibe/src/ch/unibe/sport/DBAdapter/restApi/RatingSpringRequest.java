package ch.unibe.sport.DBAdapter.restApi;


import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class RatingSpringRequest extends SpringAndroidSpiceRequest<RatingList> {

	private String courseHash;
    public RatingSpringRequest(String courseHash) {
        super(RatingList.class);
        this.courseHash = courseHash;
    }

    @Override
    public RatingList loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject("http://api.unisport.hut.by/action.php?do=getRating&hash="+courseHash, RatingList.class );
    }
}