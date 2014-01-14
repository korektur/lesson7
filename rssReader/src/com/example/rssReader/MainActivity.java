package com.example.rssReader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    Button addButton;
    ListView listView;
    SQLRequest sqlRequest;
    ArrayList<Map<String, Object>> data;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        addButton = (Button) findViewById(R.id.addButton);
        listView = (ListView) findViewById(R.id.listView1);
        sqlRequest = new SQLRequest(this);
        sqlRequest.openDB();
        Cursor cursor = sqlRequest.getAllLinks();
        data = new ArrayList<Map<String, Object>>();
        int size = cursor.getCount();
        for (int i = 0; i < size; ++i) {
            cursor.moveToNext();
            data.add(new HashMap<String, Object>());
            String s = cursor.getString(cursor.getColumnIndex(SQLRequest.keyUrl));
            s = s.substring(1, s.length() - 1);
            data.get(i).put("link", s);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.linklayout, new String[]{"link"}, new int[]{R.id.linktext});
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = (String)data.get(position).get("link");
                Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                intent.putExtra("link", url);
                startActivity(intent);
            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 0, "delete");
        menu.add(0, 2, 1, "edit");
        menu.add(0, 3, 2, "cancel");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String url = (String) data.get(info.position).get("link");
        if (item.getItemId() == 1) {
            sqlRequest.openDB();
            sqlRequest.delete(url);
            sqlRequest.closeDB();

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == 2) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            intent.putExtra("link", url);
            startActivity(intent);
        }
        return super.onContextItemSelected(item);
    }
}
