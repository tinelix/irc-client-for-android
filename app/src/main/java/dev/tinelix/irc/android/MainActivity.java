package dev.tinelix.irc.android;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

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
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ImageButton menu_button = findViewById(R.id.menu_button);
            menu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionsMenu();
                }
            });
        }

    }

    private void showAboutApplication() {
        Intent intent = new Intent(this, AboutApplicationActivity.class);
        startActivity(intent);
    }

    private void showConnectionManager() {
        Intent intent = new Intent(this, ConnectionManagerActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_application_item) {
            showAboutApplication();
        }

        return super.onOptionsItemSelected(item);
    }


}
