package model;

public class ListItem {
    private String name;
    private String description;
    private String rating;

    public ListItem(String name, String description, String rating) {
        this.name = name;
        this.description = description;
        this.rating = rating;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return this.rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


}
