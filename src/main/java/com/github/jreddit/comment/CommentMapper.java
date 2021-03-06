package com.github.jreddit.comment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.github.jreddit.utils.restclient.JsonUtils.safeJsonToInteger;
import static com.github.jreddit.utils.restclient.JsonUtils.safeJsonToString;

/**
 * Map a JSON response comment to a Comment class
 *
 * @author Raul Rene Lepsa
 */
public class CommentMapper {

    /**
     * Map a JSON object to a <code>Comment</code> class
     *
     * @param obj JSON object to map
     * @return <code>Comment</code> object instance, or NULL if an error occurs during mapping
     */
    public static Comment mapMessage(JsonObject obj) {
        Comment comment = null;

        try {
            comment = new Comment();
            comment.setId(safeJsonToString(obj.get("id")));
            comment.setAuthor(safeJsonToString(obj.get("author")));
            comment.setFullname(safeJsonToString(obj.get("name")));
            comment.setParentId(safeJsonToString(obj.get("parent_id")));
            comment.setBody(safeJsonToString(obj.get("body")));
            comment.setEdited(safeJsonToString(obj.get("edited")));
            comment.setCreated(safeJsonToString(obj.get("created_utc")));
            comment.setReplies( mapReplies( obj.get("replies") )  );
            comment.setUpvotes(safeJsonToInteger(obj.get("ups")));
            comment.setDownvotes(safeJsonToInteger(obj.get("downs")));

        } catch (Exception e) {
            System.err.println("Error mapping JsonObject to Comment");
        }

        return comment;
    }

    public static List<Comment> mapReplies(Object replies )
    {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        if (replies == null)
            return comments;
        try
        {
            JsonObject jsonReplies = (JsonObject) replies;
            JsonObject data = (JsonObject) jsonReplies.get("data");
            JsonArray kids = (JsonArray) data.get("children");
            for (Object o : kids)
            {
                JsonObject o2 = (JsonObject) o;
                Comment c = mapMessage(  (JsonObject) o2.get("data") );
                comments.add(c);
            }
            return comments;
        }
        //If there are no replies, a ClassCastException will occur. Its normal, ignore it
        //and return the empty arrayList.
        catch (ClassCastException e)
        {
            return comments;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return comments;
        }
    }
}
