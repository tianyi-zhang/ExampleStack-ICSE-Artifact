public class foo {
    /**
     * Create and/or open a database that will be used for reading and writing.
     * The first time this is called, the database will be opened and
     * {@link #onCreate}, {@link #onUpgrade} and/or {@link #onOpen} will be
     * called.
     * 
     * <p>
     * Once opened successfully, the database is cached, so you can call this
     * method every time you need to write to the database. (Make sure to call
     * {@link #close} when you no longer need the database.) Errors such as bad
     * permissions or a full disk may cause this method to fail, but future
     * attempts may succeed if the problem is fixed.
     * </p>
     * 
     * <p class="caution">
     * Database upgrade may take a long time, you should not call this method
     * from the application main thread, including from
     * {@link android.content.ContentProvider#onCreate
     * ContentProvider.onCreate()}.
     * 
     * @throws SQLiteException
     *             if the database cannot be opened for writing
     * @return a read/write database object valid until {@link #close} is called
     */
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                // darn! the user closed the database by calling
                // mDatabase.close()
                mDatabase = null;
            } else if (!mDatabase.isReadOnly()) {
                return mDatabase; // The database is already open for business
            }
        }

        if (mIsInitializing) {
            throw new IllegalStateException(
                    "getWritableDatabase called recursively");
        }

        // If we have a read-only database open, someone could be using it
        // (though they shouldn't), which would cause a lock to be held on
        // the file, and our attempts to open the database read-write would
        // fail waiting for the file lock. To prevent that, we acquire the
        // lock on the read-only database, which shuts out other users.

        boolean success = false;
        SQLiteDatabase db = null;
        // NOT AVAILABLE
        // if (mDatabase != null) {
        // mDatabase.lock();
        // }
        try {
            mIsInitializing = true;
            if (mName == null) {
                db = SQLiteDatabase.create(null);
            } else {
                String path = mDir + "/" + mName;
                // db = mContext.openOrCreateDatabase(mName, 0, mFactory,
                // mErrorHandler);
                db = SQLiteDatabase.openDatabase(path, null,
                        SQLiteDatabase.CREATE_IF_NECESSARY);
            }

            int version = db.getVersion();
            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        if (version > mNewVersion) {
                            onDowngrade(db, version, mNewVersion);
                        } else {
                            onUpgrade(db, version, mNewVersion);
                        }
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            onOpen(db);
            success = true;
            return db;
        } finally {
            mIsInitializing = false;
            if (success) {
                if (mDatabase != null) {
                    try {
                        mDatabase.close();
                    } catch (Exception e) {
                        // Do nothing
                    }
                    // NOT AVAILABLE
                    // mDatabase.unlock();
                }
                mDatabase = db;
            } else {
                // NOT AVAILABLE
                // if (mDatabase != null) {
                // mDatabase.unlock();
                // }
                if (db != null)
                    db.close();
            }
        }
    }
}