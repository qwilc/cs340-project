package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

public class DataPage<T> {
    private List<T> values;
    private boolean hasMorePages;

    public DataPage() {
        setValues(new ArrayList<T>());
        setHasMorePages(false);
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public List<T> getValues() {
        return values;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }
}
