package dev.tinelix.irc.android;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CustomNicknamesActivity extends Activity {

    List<String> nicknamesArray = new ArrayList<String>();
    String nicknamesString;
    String old_profile_name;
    public String current_parameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_nicknames_activity);
        getActionBar().setHomeButtonEnabled(true);
        current_parameter = "creating_nickname";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                old_profile_name = null;
            } else {
                old_profile_name = extras.getString("profile_name");
            }
        } else {
            old_profile_name = (String) savedInstanceState.getSerializable("profile_name");
        };
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
        nicknamesArray = new LinkedList<String>(Arrays.asList(prefs.getString("nicknames", "").split(", ")));
        if(nicknamesArray.size() == 1 && nicknamesArray.get(0).length() == 0) {
            nicknamesArray.remove(0);
        }
        ListView nicknamesList = findViewById(R.id.nicknames_list);
        ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, nicknamesArray);

        nicknamesList.setAdapter(nicknamesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nicknames_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.add_nickname_item) {
            showEnterTextDialog();
        } else if (id == R.id.clear_nicknames_item) {
            nicknamesArray.clear();
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nicknames", "");
            editor.commit();
            ListView nicknamesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, nicknamesArray);
            nicknamesList.setAdapter(nicknamesAdapter);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreatingNicknames(String parameter, String value) {
        if(parameter == "creating_nickname") {
            StringBuilder nicknames_sb = new StringBuilder();
            if(value.length() > 0) {

                nicknamesArray.add(value);
            };
            for (int i = 0; i < nicknamesArray.size(); i++) {
                if(i < nicknamesArray.size() - 1 && nicknamesArray.get(i).length() > 0) {
                    nicknames_sb.append(nicknamesArray.get(i)).append(", ");
                } else if(nicknamesArray.get(i).length() > 0) {
                    nicknames_sb.append(nicknamesArray.get(i));
                }
            };
            ListView nicknamesList = findViewById(R.id.nicknames_list);
            ArrayAdapter<String> nicknamesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, nicknamesArray);
            nicknamesList.setAdapter(nicknamesAdapter);
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(old_profile_name, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nicknames", nicknames_sb.toString());
            editor.commit();
        }
    }

    public String getCurrentParameter() {
        return current_parameter;
    }

    public boolean showEnterTextDialog() {
        DialogFragment enterTextDialogFragm = new EnterTextDialogFragm3();
        enterTextDialogFragm.show(getFragmentManager(), "enter_text_dlg");
        return false;
    };
}
