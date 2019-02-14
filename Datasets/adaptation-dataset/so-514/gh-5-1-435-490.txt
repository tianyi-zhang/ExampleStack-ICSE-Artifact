/*
    Copyright (C) 2015 Myra Fuchs, Linda Spindler, Clemens Hlawacek, Ebenezer Bonney Ussher,
    Martin Bayerl, Christoph Krasa

    This file is part of PicFrame.

    PicFrame is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    PicFrame is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with PicFrame.  If not, see <http://www.gnu.org/licenses/>.
*/

package picframe.at.picframe.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import picframe.at.picframe.helper.Keys;
import picframe.at.picframe.R;
import picframe.at.picframe.helper.GlobalPhoneFuncs;
import picframe.at.picframe.helper.alarm.AlarmScheduler;
import picframe.at.picframe.service.connectionChecker.ConnectionCheck_OC;
import picframe.at.picframe.settings.AppData;
import picframe.at.picframe.settings.SettingsDefaults;
import picframe.at.picframe.settings.detailsPrefScreen.DetailsPreferenceScreen;
import picframe.at.picframe.settings.MySwitchPref;
import picframe.at.picframe.settings.detailsPrefScreen.ExtSdPrefs;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private PreferenceCategory myCat2;
    private AlarmScheduler alarmScheduler;
    private SharedPreferences mPrefs;
    private ArrayList<String> editableTitleFields = new ArrayList<>();
    private ArrayList<String> fieldsToRemove = new ArrayList<>();
    private StatusReceiver receiver;
    private LocalBroadcastManager broadcastManager;
    private DetailsPreferenceScreen detailsPrefScreenToAdd;
    private final static boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(AppData.mySettingsFilename);
        prefMgr.setSharedPreferencesMode(MODE_PRIVATE);
        mPrefs = AppData.getSharedPreferences();
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        if (DEBUG)  printAllPreferences();
        addPreferencesFromResource(R.xml.settings);
        myCat2 = (PreferenceCategory) findPreference(getString(R.string.sett_key_cat2));

        populateEditableFieldsList();
        populateFieldsToRemove();
        createCat2Fields();
        updateAllFieldTitles();

        // set all missing fields

//        setCorrectSrcPathField();
//        updateTitlePrefsWithValues(mPrefs, "all");
        //System.out.println(AppData.toString());
    }

    private void printAllPreferences() {
        Map<String, ?> keyMap = mPrefs.getAll();
        for (String e : keyMap.keySet()) {
            debug("DUMP| Key: " + e + " ++ Value: " + keyMap.get(e));
        }

        alarmScheduler = new AlarmScheduler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastManager == null) {
            broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        }
        if (receiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Keys.ACTION_LOGINSTATUSSUCCESS);
            filter.addAction(Keys.ACTION_LOGINSTATUSFAILURE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new StatusReceiver();
            broadcastManager.registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }
            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void populateEditableFieldsList() {
        editableTitleFields.add(getString(R.string.sett_key_displaytime));
        editableTitleFields.add(getString(R.string.sett_key_transition));
        editableTitleFields.add(getString(R.string.sett_key_srctype));
        editableTitleFields.add(getString(R.string.sett_key_srcpath_sd));
        editableTitleFields.add(getString(R.string.sett_key_username));
        editableTitleFields.add(getString(R.string.sett_key_password));
        editableTitleFields.add(getString(R.string.sett_key_srcpath_owncloud));
        editableTitleFields.add(getString(R.string.sett_key_downloadInterval));
        editableTitleFields.add(getString(R.string.sett_key_loginCheckButton));
    }

    private void populateFieldsToRemove() {
        fieldsToRemove.add(getString(R.string.sett_key_recursiveSearch));
        fieldsToRemove.add(getString(R.string.sett_key_deleteData));
        fieldsToRemove.add(getString(R.string.sett_key_srcpath_sd));
        fieldsToRemove.add(getString(R.string.sett_key_restoreDefaults));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences != null && key != null) {
            debug("CHANGED| Key:" + key + " ++ Value: " + sharedPreferences.getAll().get(key));
        }
        if (editableTitleFields.contains(key)) {
            //update display/transition title
            updateFieldTitle(key);
            if (getString(R.string.sett_key_srctype).equals(key)) {
                createCat2Fields();
                alarmScheduler.scheduleAlarm();
            } else if (getString(R.string.sett_key_username).equals(key)  ||
                        getString(R.string.sett_key_password).equals(key) ||
                        getString(R.string.sett_key_srcpath_owncloud).equals(key)) {
                setLoginStatus(false);
                if (!AppData.getUserName().equals("") &&
                        !AppData.getUserPassword().equals("") &&
                        !AppData.getSourcePath().equals("") &&
                        !AppData.getSourcePath().equals(SettingsDefaults
                                .getDefaultValueForKey(R.string.sett_key_srcpath_owncloud))) {
                    new Handler().post(new ConnectionCheck_OC());
                }
            } else if (getString(R.string.sett_key_loginCheckButton).equals(key) ||
                    getString(R.string.sett_key_downloadInterval).equals(key)) {
                alarmScheduler.scheduleAlarm();
            }
        }
    }

    private void setLoginStatus(boolean status) {
        AppData.setLoginSuccessful(status);
        // TODO: change layout (status view)
    }

    public void updateAllFieldTitles() {
        for (String prefKey : editableTitleFields) {
            updateFieldTitle(prefKey);
        }
    }

    public void updateFieldTitle(String key) {
        Preference mPref = findPreference(key);
        String mPrefTitle = "";
        String mPrefValue = "";

        if (mPref != null) {
            if (mPref instanceof ListPreference) {
//                mPrefValue = ((ListPreference)mPref).getEntry() == null ? "" :  ((ListPreference)mPref).getEntry().toString();
                mPrefValue = (String) mPrefs.getAll().get(key);
                if (mPrefValue != ((ListPreference) mPref).getValue()) {
                    ((ListPreference) mPref).setValue(mPrefValue);
                }
                int index = ((ListPreference) mPref).findIndexOfValue(mPrefValue);
                mPrefValue = (String)((ListPreference) mPref).getEntries()[index];
            } else if (mPref instanceof EditTextPreference) {
                mPrefValue = (String) mPrefs.getAll().get(key);
//                mPrefValue = ((EditTextPreference)mPref).getText();
                if (getString(R.string.sett_key_password).equals(key) && mPrefValue != null && !mPrefValue.equals("")) {
                    //noinspection ReplaceAllDot
                    mPrefValue = mPrefValue.replaceAll(".", "*");
                }
            } else {
                if (getString(R.string.sett_key_srcpath_sd).equals(mPref.getKey()) &&
                        AppData.sourceTypes.ExternalSD.equals(AppData.getSourceType())) {
                    mPrefValue = AppData.getSourcePath();
                } else if (getString(R.string.sett_key_loginCheckButton).equals(mPref.getKey())) {
                    mPrefValue = AppData.getLoginSuccessful() ? getString(R.string.sett_loginCheck_success) : getString(R.string.sett_loginCheck_failure);
                }
            }
            if (getString(R.string.sett_key_displaytime).equals(key)) {
                mPrefTitle = getString(R.string.sett_displayTime);
            } else if (getString(R.string.sett_key_transition).equals(key)) {
                mPrefTitle = getString(R.string.sett_transition);
            } else if (getString(R.string.sett_key_srctype).equals(key)) {
                mPrefTitle = getString(R.string.sett_srcType);
            } else if (getString(R.string.sett_key_srcpath_sd).equals(key)) {
                mPrefTitle = getString(R.string.sett_srcPath_externalSD);
            } else if (getString(R.string.sett_key_username).equals(key)) {
                mPrefTitle = getString(R.string.sett_username);
            } else if (getString(R.string.sett_key_password).equals(key)) {
                mPrefTitle = getString(R.string.sett_password);
            } else if (getString(R.string.sett_key_srcpath_owncloud).equals(key)) {
                mPrefTitle = getString(R.string.sett_srcPath_OwnCloud);
            } else if (getString(R.string.sett_key_downloadInterval).equals(key)) {
                mPrefTitle = getString(R.string.sett_downloadInterval);
            } else if (getString(R.string.sett_key_loginCheckButton).equals(key)) {
                mPrefTitle = getString(R.string.sett_loginCheck);
            }
            mPref.setTitle(mPrefTitle + ": " + mPrefValue);
        }
    }

    public void createCat2Fields() {
        removeCat2Fields();
        // add PreferenceScreens depending on which sourceType
        setDetailsPrefScreen();
        setIncludeSubdirsSwitchPref();
        setDeleteDataButton();
        setResetToDefaultButton();
    }

    private void removeCat2Fields() {
        // remove the preference screen, before adding it again
        PreferenceScreen removeScreen = (PreferenceScreen) findPreference(getString(R.string.sett_key_prefScreenDetails));
        if (removeScreen != null) {
            myCat2.removePreference(removeScreen);
            debug("removed old Pref screen");
        }
        // remove the fields, before adding them again
        Preference removePref;
        for (String path : fieldsToRemove) {
            removePref = findPreference(path);
            if (removePref != null) {
                myCat2.removePreference(removePref);
                debug("removed:" + removePref.getTitle().toString());
            }
        }
    }

    public void setDetailsPrefScreen() {
        if (AppData.sourceTypes.OwnCloud.equals(AppData.getSourceType())) {
            detailsPrefScreenToAdd = new DetailsPreferenceScreen(
                    AppData.getSrcTypeInt(),
                    getPreferenceManager().createPreferenceScreen(this),
                    SettingsActivity.this);
            //detailsPrefScreenToAdd.set*Resource
            if (myCat2 != null && detailsPrefScreenToAdd.getPreferenceScreen() != null) {
                myCat2.addPreference(detailsPrefScreenToAdd.getPreferenceScreen());
            }
        } else if (AppData.sourceTypes.ExternalSD.equals(AppData.getSourceType())) {
            Preference pref = new ExtSdPrefs(this).getFolderPicker();
            if (myCat2 != null && pref != null) {
                myCat2.addPreference(pref);
            }
        }

    }

    private void setIncludeSubdirsSwitchPref() {
        Preference myRecCheckbox;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            myRecCheckbox = new CheckBoxPreference(this);
            ((CheckBoxPreference)myRecCheckbox).setSummaryOff(R.string.sett_recursiveSearchSummOff);
            ((CheckBoxPreference)myRecCheckbox).setSummaryOn(R.string.sett_recursiveSearchSummOn);
        } else {
            myRecCheckbox = new MySwitchPref(this);
            ((MySwitchPref)myRecCheckbox).setSummaryOff(R.string.sett_recursiveSearchSummOff);
            ((MySwitchPref)myRecCheckbox).setSummaryOn(R.string.sett_recursiveSearchSummOn);
        }
        myRecCheckbox.setTitle(R.string.sett_recursiveSearch);
        myRecCheckbox.setSummary(R.string.sett_recursiveSearchSumm);
        myRecCheckbox.setDefaultValue(true);
        myRecCheckbox.setKey(getString(R.string.sett_key_recursiveSearch));
        if (myCat2 != null) {
            myCat2.addPreference(myRecCheckbox);
        }
    }

    private void setDeleteDataButton() {
        Preference myDelDataButton = new Preference(this);
        myDelDataButton.setTitle(R.string.sett_deleteData);
        myDelDataButton.setSummary(R.string.sett_deleteDataSumm);
        myDelDataButton.setKey(getString(R.string.sett_key_deleteData));
        myDelDataButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder ensureDialogB = new AlertDialog.Builder(SettingsActivity.this);
                ensureDialogB
                        .setCancelable(false)
                        .setMessage(R.string.sett_deleteDataDialog_msg)
                        .setNegativeButton(R.string.sett_deleteDataDialog_negBtn, null)
                        .setPositiveButton(R.string.sett_deleteDataDialog_posBtn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SettingsActivity.this, R.string.sett_toast_delFiles, Toast.LENGTH_SHORT).show();
                                GlobalPhoneFuncs.recursiveDeletionInBackgroundThread(
                                        new File(AppData.getExtFolderAppRoot()),
                                        false);
                            }
                        });
                ensureDialogB.show();
                return true;
            }
        });
        if (myCat2 != null) {
            myCat2.addPreference(myDelDataButton);
        }
    }

    public void setResetToDefaultButton() {
        Preference myResetButton = new Preference(this);
        myResetButton.setTitle(R.string.sett_resetTitle);
        myResetButton.setSummary(R.string.sett_resetSummary);
        myResetButton.setKey(getString(R.string.sett_key_restoreDefaults));
        myResetButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder ensureDialogB = new AlertDialog.Builder(SettingsActivity.this);
                ensureDialogB
                        .setCancelable(false)
                        .setMessage(R.string.sett_confirmationDialogMessage)
                        .setNegativeButton(R.string.sett_no, null)
                        .setPositiveButton(R.string.sett_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                debug("in click on yes!");
                                resetSettingsToDefault();
                                Toast.makeText(SettingsActivity.this, R.string.sett_toast_reset, Toast.LENGTH_SHORT).show();
                                updateAllFieldTitles();
                            }
                        });
                ensureDialogB.show();
                return true;
            }
        });
        if (myCat2 != null) {
            myCat2.addPreference(myResetButton);
        }

    }

    public void resetSettingsToDefault() {
        AppData.resetSettings();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        // If the user has clicked on a preference screen, set up the screen
        if (preference instanceof PreferenceScreen) {
            setUpNestedScreen((PreferenceScreen) preference);
        }
        updateAllFieldTitles();
        return false;
    }

    public ViewGroup getStatusView () {
        return detailsPrefScreenToAdd.getStatusViewGroup();
    }

    /************************************************************************************
    *   needed because else the nested preference screen don't have a actionbar/toolbar *
    *   see the fix and the given problem here: http://stackoverflow.com/a/27455363     *
    ************************************************************************************/
    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        //ViewGroup list;
        Toolbar bar;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //list = (ViewGroup) dialog.findViewById(android.R.id.list);
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            //list = content;
            root.removeAllViews();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }
        //list.addView(detailsPrefScreenToAdd.getStatusViewGroup(), 1); //TODO
        bar.setTitle(preferenceScreen.getTitle());
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogI) {
                if (AppData.getLoginSuccessful()) {
                    dialogI.dismiss();
                } else {
                    showNotConnectedDialog(dialog);
                }
            }
        });
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppData.getLoginSuccessful()) {
                    dialog.dismiss();
                } else {
                    showNotConnectedDialog(dialog);
                }
            }
        });
    }

    private void showNotConnectedDialog(final Dialog dialog) {
        AlertDialog notConnectedAlert = new AlertDialog.Builder(SettingsActivity.this)
                .setMessage(R.string.sett_dialog_notConnected_message)
                .setPositiveButton(R.string.sett_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                                dialogInterface.dismiss();
                            }
                        })
                .setNegativeButton(R.string.sett_no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                dialog.show();
                            }
                        })
                .create();
        notConnectedAlert.getWindow().setGravity(Gravity.CENTER);
        notConnectedAlert.setCancelable(false);
        notConnectedAlert.show();
    }

    private void debug(String msg) { if (DEBUG) { Log.d(TAG, msg); } }

    private class StatusReceiver extends BroadcastReceiver {
        private StatusReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (Keys.ACTION_LOGINSTATUSSUCCESS.equals(intent.getAction())) {
                    debug("received 'loginSuccess' action via broadcast");
                    Toast.makeText(getApplicationContext(), R.string.sett_toast_loginSuccess, Toast.LENGTH_SHORT).show();
//TODO
                } else if (Keys.ACTION_LOGINSTATUSFAILURE.equals(intent.getAction())) {
                    debug("received 'loginFailure' action via boadcast");
                    Toast.makeText(getApplicationContext(), R.string.sett_toast_loginFailure, Toast.LENGTH_SHORT).show();
//TODO
                }
            }
        }
    }
}