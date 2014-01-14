package com.example.rssReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ruslan
 * Date: 14.01.14
 * Time: 0:19
 */
public class Settings extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Intent intent = getIntent();
        final String url = intent.getStringExtra("link");
        final EditText editText = (EditText) findViewById(R.id.editText);
        if (url != null) {
            editText.setText(url);
        }
        Button addButton = (Button) findViewById(R.id.buttonadd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUrl = editText.getText().toString();
                if (!newUrl.isEmpty()) {
                    SQLRequest sqlRequest = new SQLRequest(Settings.this);
                    sqlRequest.openDB();
                    if (url != null) {
                        sqlRequest.delete(url);
                    }
                    sqlRequest.addLink(newUrl);
                    RssFeed feed = new RssFeed(newUrl);
                    feed.execute();
                    try {
                        ArrayList<Map<String, Object>> data = feed.get();
                        for(int i = 0; i < data.size(); i++){
                            String description = (String)data.get(i).get("description");
                            String link = (String)data.get(i).get("link");
                            String pudDate = (String)data.get(i).get("pubDate");
                            String title = (String)data.get(i).get("title");
                            sqlRequest.addFeed(newUrl, title, description, pudDate, link);
                        }
                    } catch (Exception e){}
                    sqlRequest.closeDB();
                    Intent intent = new Intent(Settings.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}