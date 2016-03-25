package com.weining.dbtaskdemo;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.weining.dbtask.DBAsyncDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by weining on 16/3/25.
 */
public class UserDataHelper extends OrmLiteSqliteOpenHelper {
    private static volatile UserDataHelper sHelper = null;
    private static final String DATABASE_NAME = "User";
    private static final int DATABASE_VERSION = 1;
    private DBAsyncDao<User, String> mUserDao = null;

    private UserDataHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    public static synchronized UserDataHelper getHelper(Context context) {
        if (sHelper == null) {
            sHelper = new UserDataHelper(context, DATABASE_NAME, DATABASE_VERSION);
        }
        return sHelper;
    }

    public DBAsyncDao<User, String> getUserDao() throws SQLException {
        if (mUserDao == null) {
            synchronized (this) {
                if (mUserDao == null) {
                    Dao<User, String> dao = sHelper.getDao(User.class);
                    mUserDao = new DBAsyncDao<>(dao);
                }
            }
        }
        return mUserDao;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
    }
}
