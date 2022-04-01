package demo.mikko;

import java.util.List;

public class SearchResult {
    
    int totalHits;
    int max;
    int offset;

    List<SearchHit> searchHits;

    public int getTotalHits() {
        return this.totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }


    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<SearchHit> getSearchHits() {
        return this.searchHits;
    }

    public void setSearchHits(List<SearchHit> searchHits) {
        this.searchHits = searchHits;
    }

}
