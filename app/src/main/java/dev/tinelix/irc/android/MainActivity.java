package dev.tinelix.irc.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] mainMenu = getResources().getStringArray(R.array.main_menu_array);
        ListView mainMenuList = (ListView) findViewById(R.id.mainmenu);

        ArrayAdapter<String> mainMenuAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mainMenu);

        mainMenuList.setAdapter(mainMenuAdapter);

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    showConnectionManager();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void showConnectionManager() {
        Intent intent = new Intent(this, ConnectionManagerActivity.class);
        startActivity(intent);
    }
}