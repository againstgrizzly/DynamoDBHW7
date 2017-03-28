import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Movies")
class Movie{

    private String id;
    private String title;
    private int year;
    private String mpaa_rating;
    private int runtime;
    private String critics_consensus;
    private Release_Dates release_dates;
    private Ratings ratings;
    private String synopsis;
    private Posters posters;
    private Abridged_Cast abridged_cast[];
    private MovieLinks links;

    @DynamoDBHashKey(attributeName="id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBRangeKey(attributeName="title")
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "year")
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    @DynamoDBAttribute(attributeName = "mpaa_rating")
    public String getMpaa_rating() {
        return mpaa_rating;
    }
    public void setMpaa_rating(String mpaa_rating) {
        this.mpaa_rating = mpaa_rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getCritics_consensus() {
        return critics_consensus;
    }

    public void setCritics_consensus(String critics_consensus) {
        this.critics_consensus = critics_consensus;
    }

    public Release_Dates getRelease_dates() {
        return release_dates;
    }

    public void setRelease_dates(Release_Dates release_dates) {
        this.release_dates = release_dates;
    }

    public Ratings getRatings() {
        return ratings;
    }

    public void setRatings(Ratings ratings) {
        this.ratings = ratings;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Posters getPosters() {
        return posters;
    }

    public void setPosters(Posters posters) {
        this.posters = posters;
    }

    public Abridged_Cast[] getAbridged_cast() {
        return abridged_cast;
    }

    public void setAbridged_cast(Abridged_Cast[] abridged_cast) {
        this.abridged_cast = abridged_cast;
    }

    public MovieLinks getLinks() {
        return links;
    }

    public void setLinks(MovieLinks links) {
        this.links = links;
    }
}