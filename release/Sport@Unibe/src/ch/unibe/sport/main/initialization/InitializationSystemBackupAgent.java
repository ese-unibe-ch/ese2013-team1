package ch.unibe.sport.main.initialization;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import ch.unibe.sport.config.System;
import ch.unibe.sport.utils.Print;

public class InitializationSystemBackupAgent extends BackupAgentHelper {

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "initialization";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
    	Print.log("Backup agent onCreate");
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, System.INVARIANT_PREFERENCES_NAME);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}