package com.example.android.googlebooks;

import android.content.Context;
import android.content.AsyncTaskLoader;
import java.util.List;

/**
 * Created by chase on 6/28/2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    //tag for log messages
    private static final String LOG_TAG = BookLoader.class.getName();

    //query URL
    private String mUrl;

    //construct a new BookLoader
    //takes in context of the activity and the url to load data from

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //this is on a background thread
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        //perform the network request, parse the response, and extract the list of books
        return QueryUtils.fetchBookData(mUrl);
    }
}
