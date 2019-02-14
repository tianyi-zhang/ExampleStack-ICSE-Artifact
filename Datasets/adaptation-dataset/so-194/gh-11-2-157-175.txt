package com.thetorine.android.samaritan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.thetorine.android.samaritan.utilities.DialogAdd;
import com.thetorine.samaritan.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class InputActivity extends ActionBarActivity {
    private static String[] ALL_PHRASES = new String[] {
        "Calculating Response",
        "What are your commands?",
        "Investigation ongoing",
        "I will protect you now",
        "Find the Machine",
    };
    
    public static List<String> EXTRA_PHRASES = new ArrayList<String>();
    public static SharedPreferences sharedPref;
    public static Context classContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        classContext = this;
        
        for(String s : ALL_PHRASES) {
        	if(!EXTRA_PHRASES.contains(s)) {
        		EXTRA_PHRASES.add(s);
        	}
        }
        
        loadData();
        populateList();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.displayButton: {
                EditText editText = (EditText)findViewById(R.id.inputText);
                Editable text = editText.getText();

                if(text.length() > 0) {
                    Intent intent = new Intent(this, TextActivity.class);
                    intent.putExtra("text", text.toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Nothing to Display.", Toast.LENGTH_SHORT).show();;
                }

                break;
            }
            case R.id.clearButton: {
                EditText editText = (EditText)findViewById(R.id.inputText);
                editText.setText("");
                break;
            }   
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.contextmenu, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.settings: {
    			Intent i = new Intent(this, PreferencesActivity.class);
    			startActivity(i);
    			break;
    		}
    		case R.id.add_phrase: {
    			DialogAdd dialog = new DialogAdd();
    			dialog.show(getFragmentManager(), "DialogAdd");
    			break;
    		}
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.delete_phrase: {
    			ListView listView = (ListView)findViewById(R.id.phrases);
				AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
				String s = listView.getItemAtPosition(menuInfo.position).toString();
				
				for(String s1 : ALL_PHRASES) {
					if(s1.contentEquals(s)) {
						Toast t = Toast.makeText(this, "Built-In Phrases cannot be deleted!", Toast.LENGTH_SHORT);
						t.show();
						return super.onContextItemSelected(item);
					}
				}
				EXTRA_PHRASES.remove(s);
				((ArrayAdapter<String>)listView.getAdapter()).notifyDataSetChanged();
				setListViewHeightBasedOnChildren(listView);
				break;
    		}
    	}
    	return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	saveData();
    }
    
    //http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing
    public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) return;

		int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem instanceof ViewGroup) {
				listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
    
    private void populateList() {
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, EXTRA_PHRASES);
        final ListView listView = (ListView)findViewById(R.id.phrases);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = listView.getItemAtPosition(position).toString();
                EditText editText = (EditText)findViewById(R.id.inputText);
                CheckBox checkBox = (CheckBox)findViewById(R.id.selectLoop);
                if(checkBox.isChecked()) {
                	String text = (editText.getText().length() > 0 ? editText.getText().toString() + "-" + s : s);
                	editText.setText(text);
                } else {
                	editText.setText(s);
                	Button b = (Button)findViewById(R.id.displayButton);
                    onClick(b);
                }
            }
        });
        setListViewHeightBasedOnChildren(listView);
        registerForContextMenu(listView);
    }
    private void loadData() {
    	try {
    		FileInputStream fis = openFileInput("samaritan_phrases");
    		BufferedReader reader = new BufferedReader(new FileReader(fis.getFD()));
    		while(reader.ready()) {
    			EXTRA_PHRASES.add(reader.readLine());
    		}
    		reader.close();
    		fis.close();
    	} catch(Exception e) { 
    		e.printStackTrace(); 
    	}
    }
    
    private void saveData() {
    	try {
    		FileOutputStream fos = openFileOutput("samaritan_phrases", Context.MODE_PRIVATE);
			FileWriter writer = new FileWriter(fos.getFD());
    		for(int i = ALL_PHRASES.length; i < EXTRA_PHRASES.size(); i++) {
        		writer.write(EXTRA_PHRASES.get(i)+"\n");
        	}
    		writer.close();
    		fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    public void addPhrase(String s) {
    	ListView listView = (ListView)findViewById(R.id.phrases);
    	if(!EXTRA_PHRASES.contains(s)) {
    		EXTRA_PHRASES.add(s);
    	}
    	((ArrayAdapter<String>)listView.getAdapter()).notifyDataSetChanged();
    	setListViewHeightBasedOnChildren(listView);
    }
}