package com.github.jreddit.submissions;

import com.github.jreddit.user.User;
import com.github.jreddit.utils.ApiEndpointUtils;
import com.github.jreddit.utils.PaginationResult;
import com.github.jreddit.utils.restclient.RestClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.github.jreddit.utils.restclient.JsonUtils.safeJsonToString;

/**
 * This class offers some submission utilities.
 *
 * @author <a href="http://www.omrlnr.com">Omer Elnour</a>
 */
public class Submissions {

    private final RestClient restClient;

    public enum Popularity {
        HOT, NEW
    }

    public enum Page {
        FRONTPAGE
    }

    public Submissions(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * This function returns a list containing the submissions on a given
     * subreddit and page. (in progress)
     *
     * @param redditName The subreddit's name
     * @param type       HOT or NEW and some others to come
     * @param frontpage       TODO this
     * @param user       The user
     * @return The list containing submissions
     * @throws IOException    If connection fails
     * @throws JsonParseException If JSON parsing fails
     */
    public List<Submission> getSubmissions(String redditName,
                       Popularity type, Page frontpage, User user) throws IOException, JsonParseException {

        LinkedList<Submission> submissions = new LinkedList<Submission>();
        String urlString = "/r/" + redditName;

        switch (type) {
            case NEW:
                urlString += "/new";
                break;
		default:
			break;
        }

        //TODO Implement Pages

        urlString += ".json";

        JsonObject object = (JsonObject)  restClient.get(urlString, user.getCookie()).getResponseObject();
        JsonArray array = (JsonArray) ((JsonObject) object.get("data")).get("children");

        JsonObject data;
        Submission submission;
        for (Object anArray : array) {
            data = (JsonObject) anArray;
            data = ((JsonObject) data.get("data"));
            submission = new Submission(data);
            submission.setUser(user);
            submissions.add(submission);

        }

        return submissions;
    }
    
    /**
     * Returns a list of submissions from a subreddit.
     *
     * @param subreddit The subreddit at which submissions you want to retrieve submissions.
     * @return <code>PaginationResult<Submission></Submission></code> of submissions on the subreddit.
     */
    public PaginationResult<Submission> getSubmissions(String subreddit, String after) {
        PaginationResult<Submission> result = new PaginationResult();
        List<Submission> submissions = new ArrayList<Submission>(500);
        try {
            // Send GET request to get the account overview
            String endPoint = String.format(ApiEndpointUtils.SUBMISSIONS, subreddit);
            if (after != null)
                endPoint += "?after=" + after;

            JsonObject object = (JsonObject) restClient.get(endPoint, null).getResponseObject();
            JsonObject data = (JsonObject) object.get("data");
            JsonArray children = (JsonArray) data.get("children");

            result.setAfter( safeJsonToString( data.get("after") ) );

            JsonObject obj;

            for (Object aChildren : children) {
                // Get the object containing the comment
                obj = (JsonObject) aChildren;
                obj = (JsonObject) obj.get("data");
                //add a new Submission to the list
                submissions.add(new Submission(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the submissions
        result.setResults(submissions);
        return result;
    }
}
