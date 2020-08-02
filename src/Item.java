public class Item {
    // declare all fields for an item
    private String name;
    private String color;
    private String price;
    private String image;

    // constructor
    public Item(String name, String color, String price, String image) {
        this.name = name;
        this.color = color;
        this.price = price;
        this.image = image;
    }

    // default constructor
    public Item() {
        this(null, null, "$0", null);
    }

    // Accessor for each field
    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public String getPrice() {
        return this.price;
    }
    public String getImage() {
    	return this.image;
    }

    // Mutator for each field
    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    
    public void setImage(String image) {
    	this.image = image;
    }
}
