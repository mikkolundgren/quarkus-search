package demo.mikko;

public class IndexResult {

    int total;
    long elapsed;


    public IndexResult(int total, long elapsed) {
        this.total = total;
        this.elapsed = elapsed;
    }


    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getElapsed() {
        return this.elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    
}
