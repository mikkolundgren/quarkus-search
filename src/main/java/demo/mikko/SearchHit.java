package demo.mikko;

public class SearchHit {
    
    float weight;
    String path;


    public SearchHit(float weight, String path) {
        this.weight = weight;
        this.path = path;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
