package com.example.android.googlebooks;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import static com.example.android.googlebooks.R.id.cover;

/**
 * Created by chase on 6/26/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    //creates a new book adapter
    //books is the list of books
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);

    }

    ///returns a list item view that displays information about the book at the
    //given position in the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        //find the book at the given position in the list of books
        Book currentBook = getItem(position);

        //find the textview with the view ID author
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_name);

        //get the author name and store it in a string
        String authorText = currentBook.getAuthor();
        //set the author text to the corresponding text View
        authorView.setText(authorText);

        //find the textview with the view id title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);

        //get the title and store it in a string
        String titleText = currentBook.getTitle();
        //set the title text to the corresponding text View
        titleView.setText(titleText);

        //find the textview with the view id publication date
        TextView dateView = (TextView) listItemView.findViewById(R.id.publication_date);

        //get the date and store it in a string
        String dateText = currentBook.getDate();
        //set the date text to the corresponding text View
        dateView.setText(dateText);

        ImageView coverView = (ImageView) listItemView.findViewById(cover);
        //get the thumbnail cover image and assign them to the appropriate view
        Uri uri = Uri.parse(currentBook.getThumbnailUrl());
        if (currentBook.getThumbnailUrl() != null && !currentBook.getThumbnailUrl().isEmpty()) {

            Picasso.with(getContext()).load(uri).into(coverView);
        } else {
            coverView.setImageResource(R.drawable.pug);
        }

        return listItemView;
    }
}

