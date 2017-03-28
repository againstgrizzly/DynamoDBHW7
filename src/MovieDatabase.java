
class MovieDatabase {

    private int total;
    private Movie movies[];
    private MovieDatabaseLinks links;
    private String link_template;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Movie[] getMovies() {
        return movies;
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
    }

    public MovieDatabaseLinks getLinks() {
        return links;
    }

    public void setLinks(MovieDatabaseLinks links) {
        this.links = links;
    }

    public String getLink_template() {
        return link_template;
    }

    public void setLink_template(String link_template) {
        this.link_template = link_template;
    }
}
