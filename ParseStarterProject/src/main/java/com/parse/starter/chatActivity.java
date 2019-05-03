package com.parse.starter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
public class chatActivity extends AppCompatActivity {

    String activeUser = "";
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    public void sendchat(View view) {
        final EditText chatedittext = (EditText) findViewById(R.id.chatEditText);
        ParseObject message = new ParseObject("Message");
        final String messageContent = chatedittext.getText().toString();
        message.put("sender", ParseUser.getCurrentUser().getUsername());
        message.put("recipient", activeUser);
        message.put("message", messageContent);
        chatedittext.setText("");
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    messages.add(messageContent);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateChat() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent i = getIntent();
        activeUser = i.getStringExtra("username");
        setTitle(activeUser);

        setTitle("Chat with" + activeUser);

        ListView chatlistview = (ListView) findViewById(R.id.chatListView);

        chatlistview.setAdapter(arrayAdapter);
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender", ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient", activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("recipient", ParseUser.getCurrentUser().getUsername());
        query2.whereEqualTo("sender", activeUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        messages.clear();
                        for (ParseObject message : objects) {
                            String messageContent = message.getString("message");
                            if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                messageContent = "> " + messageContent;
                            }
                            messages.add(messageContent);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }
}