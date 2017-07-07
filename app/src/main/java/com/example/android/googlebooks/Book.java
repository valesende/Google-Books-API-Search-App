package com.example.android.googlebooks;

/**
 * Created by chase on 6/26/2017.
 */

//Books object contains information related to a single book
public class Book {

    //title of the book
    private String mTitle;

    //author of the book
    private String mAuthor;

    //publication date of the book
    private String mDate;

    //link to thumbnail of the book cover
    private String mThumbnailUrl;

    //link to the google play listing for the book
    private String mUrl;

    //constructing a new com.example.android.googlebooks.Book object
    public Book(String title, String author, String date, String url, String thumbnailUrl) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
    }

    //returns the book title
    public String getTitle() {
        return mTitle;
    }

    //returns the book author
    public String getAuthor() {
        return mAuthor;
    }

    //returns the publication date
    public String getDate() {
        return mDate;
    }

    //returns the thumbnail cover link
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    //returns the google play store link to the book
    public String getUrl() {
        return mUrl;
    }
}
