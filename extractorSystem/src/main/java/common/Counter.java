package common;

/**
 * Created by binbin on 15/12/1.
 */
public class Counter {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Counter(int count){
        this.count = count;
    }

    public void Des(){
        count--;
    }

    public void Sub(int j){
        count = count - j;
    }

}
