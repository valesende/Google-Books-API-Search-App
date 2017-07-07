package com.example.android.googlebooks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = BookActivity.class.getName();

    //Constant value for the book loader ID. only used if there are multiple loaders
    private static final int BOOK_LOADER_ID = 1;

    public String searchQuery = "";

    public String query = "";

    private static final String JSON_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    public boolean searching = false;

    //adapter for book list
    private BookAdapter mAdapter;

    //textView displayed when the list is empty
    public TextView mEmptyStateTextView;

    //a function that checks for network connectivity. boolean: is the user online or not?
    public boolean userIsOnline() {
        //gets reference to connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_main);

        //find a reference to the ListView in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        //create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        //set the adapter on the ListView so the list can be populated
        bookListView.setAdapter(mAdapter);

        //set an item click listener on the ListView, which sends an intent to a browser to open
        //a website with more information on the clicked book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                //convert the string url into a uri object to pass in to the intent constructor
                Uri bookUri = Uri.parse(currentBook.getUrl());

                //create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                //send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

//        //get a reference to the ConnectivityManager to check state of network connectivity
//        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        //get details on the currently active default data network
//        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        //fetch results of default query if an internet connection is available
        if (userIsOnline()) {
            query = JSON_URL + "android&orderBy=newest&maxResults=40";
            getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);

        } else {
            //hide the loading indicator so we can see the error message
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            //if no connection, update the empty view with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);

        }

        //this section handles user search query
        final SearchView searchView = (SearchView) findViewById(R.id.search_bar);

        //uses a setOnQueryTextListener to determine what user typed in search box
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (userIsOnline()) {
                    //when on query text is submitted, the following boolean allows the user's search
                    //term to be concatenated into the query URL
                    searching = true;

                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    //sets loading indicator to visible to let the user know data will eventually
                    //be displayed
                    loadingIndicator.setVisibility(View.VISIBLE);

                    //sends the user's query to a string
                    String userQuery = searchView.getQuery().toString();

                    //replaces spaces with plus signs in the user's search term
                    userQuery = userQuery.replace(" ", "+");

                    //concatenates the user's search term into the JSON URL query
                    searchQuery = "https://www.googleapis.com/books/v1/volumes?q=" +
                            userQuery.trim() + "&orderBy=relevance&maxResults=40&startIndex=0&endIndex=40";

                    Log.v(LOG_TAG, userQuery);

                    //restarts the loader34
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this).forceLoad();
                    searchView.clearFocus();
                } else {
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                    mAdapter.clear();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle args) {
        if (searching) {
            // Create a new loader for the given URL
            return new BookLoader(this, searchQuery);
        }
        return new BookLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        //hide the loading indicator after the books is loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //set the text on the empty text view to display no books found
        mEmptyStateTextView.setText(R.string.no_books);

        //clear the adapter of previous book data
        mAdapter.clear();

        //if there is a valid list of Book's then add them to the adapter's books set
        //this causes the ListView to update
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText(R.string.no_books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        //loader resets and clears the existing data
        mEmptyStateTextView.setVisibility(View.GONE);
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);

    }
}








