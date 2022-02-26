package dev.tinelix.irc.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
                } else if(i == 1) {
                    showAboutApplication();
                } else if(i == 2) {
                    finish();
                    System.exit(0);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_item) {
            showAboutApplication();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutApplication() {
        Intent intent = new Intent(this, AboutApplicationActivity.class);
        startActivity(intent);
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