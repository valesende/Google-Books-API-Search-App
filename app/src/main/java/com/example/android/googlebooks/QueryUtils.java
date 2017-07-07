package com.example.android.googlebooks;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.Charset;


public final class QueryUtils {

    //tag for log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }

    /**
     * query the data set and return a list of Book objects
     */
    public static List<Book> fetchBookData(String requestUrl) {

        //create url object
        URL url = createUrl(requestUrl);

        //perform HTTP request to the url and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //extract relevant field from the JSON response and create a list of Books

        List<Book> books = extractItemsFromJson(jsonResponse);

        // Return the list of books
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    //make the HTTP request to the URL and return a string as a response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //if the URL is null, return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if the request was succesfful (response code 200), then read the
            //input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                //closing the input stream may throw an IO exception which is why
                // the makeHTTprequest method signature specifies that an IOexception
                //could be thrown
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //convert the inputstream into a string which contains the entire Json response form the server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

//return a list of Book objects taht has been created from parsing the JSON response

    private static List<Book> extractItemsFromJson(String bookJSON) {
        //if the JSON string is empty or null, return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        //create an empty Array list so we can add books to it
        List<Book> books = new ArrayList<>();

        //try to parse the JSON response string. if there's a problem with the way
        //the JSON is formatted, a JSONException object will be thrown.
        //the following is used to catch the exception so the app doesn't crash
        try {
            //create a jsonobject from the json response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            //check if "items" exists for a particular book,
            // if so, extract the JSONArray associated with the key called "items',
            //which represents a list of items (books)
            JSONArray bookArray = new JSONArray();
            if (baseJsonResponse.has("items"))
             bookArray = baseJsonResponse.getJSONArray("items");

            //for each book in the bookArray, create a Book object
            for (int i = 0; i < bookArray.length(); i++) {

                //get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                //for a given book, extract the JSONObject associated with the key called
                //"volume info", which lists the information associated with a certain book
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                //extract the vlaue for the key called "title"
                String title = volumeInfo.getString("title");

                String author = "";
                if (volumeInfo.has("authors")) {
                    //extract the value for the key called "authors"
                    author = volumeInfo.getJSONArray("authors").get(0).toString();
                }

                String date = "";
                if (volumeInfo.has("publishedDate")) {
                    //extract the value for the key called "publishedDate"
                    date = volumeInfo.getString("publishedDate");
                }

                //extract the link to the book in the google play store
                //(key called "previewLink")
                String url = volumeInfo.getString("previewLink");


                //extract the link to the thumbnail image to display in the UI
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String thumbnailUrl = imageLinks.getString("thumbnail");

                //create a new Book object with the author, title, publication date, link to
                //book, and link to thumbnail image of cover
                Book book = new Book(title, author, date, url, thumbnailUrl);

                //add the new Book to the list of books
                books.add(book);
            }
        } catch (JSONException e) {
            //if an error is thrown while executing any of the above statemetns in the try bvlock,
            //catch the exception here, so the app doesn't crash. Print a log message
            //with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        //return the list of books
        return books;
    }
}