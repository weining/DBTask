package com.weining.dbtaskdemo;

import java.sql.SQLException;

import com.weining.dbtask.DBAsyncDao;
import com.weining.dbtask.DBOp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button mViewDbBtn;
    Button mAddModelBtn;
    DBAsyncDao<User, String> mUserDao;
    int mTestBackgroundNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewDbBtn = (Button) findViewById(R.id.view_db_btn);
        mAddModelBtn = (Button) findViewById(R.id.add_model_btn);
        mViewDbBtn.setOnClickListener(this);
        mAddModelBtn.setOnClickListener(this);
        try {
            mUserDao = UserDataHelper.getHelper(this).getUserDao();
        }
        catch (SQLException e) {
            Log.e("SQLErr", e.getMessage(), e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view_db_btn) {
            final String[] list = databaseList();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择数据库");
            builder.setItems(list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, DebugDBViewActivity.class);
                    intent.putExtra("DatabaseName", list[which]);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else if (id == R.id.add_model_btn){
            User user = new User();
            user.userName = "shit";
            user.avatar = "avatar";
            User user2 = new User();
            user2.userName = "shit2";
            user2.avatar = "avatar2";
            mUserDao.createOrUpdateAsync(user, new DBOp.IAfterDoingBackground() {
                @Override
                public void onAfterDoingBackground(boolean isSuccessFul, Object o, DBOp op) {
                    // background thread
                    if (isSuccessFul) {
                        mTestBackgroundNum++;
                    }
                }
            }, new DBOp.IOnPostExecute() {
                @Override
                public void onPostExecute(boolean isSuccessFul, Object o) {
                    // UI thread
                    if (isSuccessFul) {
                        Toast.makeText(MainActivity.this, "create user1 success testnum: " + mTestBackgroundNum, Toast
                                .LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "create user2 failed testnum: " + mTestBackgroundNum, Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }).createOrUpdate(user2, new DBOp.IAfterDoingBackground() {
                @Override
                public void onAfterDoingBackground(boolean isSuccessFul, Object o, DBOp op) {
                    // background thread
                    if (isSuccessFul) {
                        mTestBackgroundNum++;
                    }
                }
            }, new DBOp.IOnPostExecute() {
                @Override
                public void onPostExecute(boolean isSuccessFul, Object o) {
                    // UI thread
                    if (isSuccessFul) {
                        Toast.makeText(MainActivity.this, "create user2 success testnum: " + mTestBackgroundNum, Toast
                                .LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "create user2 failed testnum: " + mTestBackgroundNum, Toast
                                .LENGTH_SHORT).show();
                    }
                }
            }).execute();
        }
    }
}
