package com.weining.dbtaskdemo;
//add your package name here example: package com.example.dbm;

//all required import files

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class DebugDBViewActivity extends Activity implements OnItemClickListener {

    //a static class to save cursor,table values etc which is used by functions to share data in the program.
    class IndexInfo {
        public int index = 10;
        public int numberOfPages = 0;
        public int currentPage = 0;
        public String tableName = "";
        public Cursor mainCursor;
        public int cursorPostion = 0;
        public ArrayList<String> valueString;
        public ArrayList<String> emptyTableColumnNames;
        public boolean isEmpty;
        public boolean isCustomQuery;
    }

    // all global variables

    //in the below line Change the text 'yourCustomSqlHelper' with your custom sqlitehelper class name.
    //Do not change the variable name dbm
    TableLayout mTableLayout;
    LayoutParams mTableRowParams;
    HorizontalScrollView mHorizontalScrollView;
    ScrollView mMainScrollView;
    LinearLayout mMainLayout;
    TextView mMessageTV;
    Button mPreviousBtn;
    Button mNextBtn;
    Spinner mSelectTable;
    TextView mCountTv;
    Activity mActivity;

    IndexInfo mIndexInfo = new IndexInfo();
    DebugDBHelper mDebugDBHelper;
    Cursor mFirstCursor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeMainCursor();
        closeCursor(mFirstCursor);
    }

    private void closeMainCursor() {
        closeCursor(mIndexInfo.mainCursor);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mDebugDBHelper = new DebugDBHelper(this, getIntent().getStringExtra("DatabaseName"));
        //in the below line Change the text 'yourCustomSqlHelper' with your custom sqlitehelper class name

        mMainScrollView = new ScrollView(DebugDBViewActivity.this);

        //the main linear layout to which all tables spinners etc will be added.In this activity every element is
        // created dynamically  to avoid using xml file
        mMainLayout = new LinearLayout(DebugDBViewActivity.this);
        mMainLayout.setOrientation(LinearLayout.VERTICAL);
        mMainLayout.setBackgroundColor(Color.WHITE);
        mMainLayout.setScrollContainer(true);
        mMainScrollView.addView(mMainLayout);

        //all required layouts are created dynamically and added to the main scrollview
        setContentView(mMainScrollView);

        //the first row of layout which has a text view and spinner
        final LinearLayout firstrow = new LinearLayout(DebugDBViewActivity.this);
        firstrow.setPadding(0, 10, 0, 20);
        LinearLayout.LayoutParams firstrowlp = new LinearLayout.LayoutParams(0, 150);
        firstrowlp.weight = 1;

        TextView maintext = new TextView(DebugDBViewActivity.this);
        maintext.setText("Select Table");
        maintext.setTextSize(22);
        maintext.setLayoutParams(firstrowlp);
        mSelectTable = new Spinner(DebugDBViewActivity.this);
        mSelectTable.setLayoutParams(firstrowlp);

        firstrow.addView(maintext);
        firstrow.addView(mSelectTable);
        mMainLayout.addView(firstrow);

        ArrayList<Cursor> alc;

        //the horizontal scroll view for table if the table content doesnot fit into screen
        mHorizontalScrollView = new HorizontalScrollView(mActivity);

        //the main table layout where the content of the sql tables will be displayed when user selects a table
        mTableLayout = new TableLayout(mActivity);
        mTableLayout.setHorizontalScrollBarEnabled(true);
        mHorizontalScrollView.addView(mTableLayout);

        //the second row of the layout which shows number of records in the table selected by user
        final LinearLayout secondrow = new LinearLayout(mActivity);
        secondrow.setPadding(0, 20, 0, 10);
        LinearLayout.LayoutParams secondrowlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        secondrowlp.weight = 1;
        TextView secondrowtext = new TextView(mActivity);
        secondrowtext.setText("No. Of Records : ");
        secondrowtext.setTextSize(20);
        secondrowtext.setLayoutParams(secondrowlp);
        mCountTv = new TextView(mActivity);
        mCountTv.setTextSize(20);
        mCountTv.setLayoutParams(secondrowlp);
        secondrow.addView(secondrowtext);
        secondrow.addView(mCountTv);
        mMainLayout.addView(secondrow);
        //A button which generates a text view from which user can write custome queries
        final EditText customquerytext = new EditText(this);
        customquerytext.setVisibility(View.GONE);
        customquerytext.setHint(
                "Enter Your Query here and Click on Submit Query Button .Results will be displayed below");
        mMainLayout.addView(customquerytext);

        final Button submitQuery = new Button(mActivity);
        submitQuery.setVisibility(View.GONE);
        submitQuery.setText("Submit Query");

        submitQuery.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mMainLayout.addView(submitQuery);

        final TextView help = new TextView(mActivity);
        help.setText("Click on the row below to update values or delete the tuple");
        help.setPadding(0, 5, 0, 5);

        // the spinner which gives user a option to add new row , drop or delete table
        final Spinner spinnertable = new Spinner(mActivity);
        mMainLayout.addView(spinnertable);
        mMainLayout.addView(help);
        mHorizontalScrollView.setPadding(0, 10, 0, 10);
        mHorizontalScrollView.setScrollbarFadingEnabled(false);
        mHorizontalScrollView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        mMainLayout.addView(mHorizontalScrollView);
        //the third layout which has buttons for the pagination of content from database
        final LinearLayout thirdrow = new LinearLayout(mActivity);
        mPreviousBtn = new Button(mActivity);
        mPreviousBtn.setText("Previous");

        mPreviousBtn.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mPreviousBtn.setLayoutParams(secondrowlp);
        mNextBtn = new Button(mActivity);
        mNextBtn.setText("Next");
        mNextBtn.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mNextBtn.setLayoutParams(secondrowlp);
        TextView tvblank = new TextView(this);
        tvblank.setLayoutParams(secondrowlp);
        thirdrow.setPadding(0, 10, 0, 10);
        thirdrow.addView(mPreviousBtn);
        thirdrow.addView(tvblank);
        thirdrow.addView(mNextBtn);
        mMainLayout.addView(thirdrow);

        //the text view at the bottom of the screen which displays error or success messages after a query is executed
        mMessageTV = new TextView(mActivity);

        mMessageTV.setText("Error Messages will be displayed here");
        String Query = "SELECT name _id FROM sqlite_master WHERE type ='table'";
        mMessageTV.setTextSize(18);
        mMainLayout.addView(mMessageTV);

        final Button customQuery = new Button(mActivity);
        customQuery.setText("Custom Query");
        customQuery.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mMainLayout.addView(customQuery);
        customQuery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //set drop down to custom Query
                mIndexInfo.isCustomQuery = true;
                secondrow.setVisibility(View.GONE);
                spinnertable.setVisibility(View.GONE);
                help.setVisibility(View.GONE);
                customquerytext.setVisibility(View.VISIBLE);
                submitQuery.setVisibility(View.VISIBLE);
                mSelectTable.setSelection(0);
                customQuery.setVisibility(View.GONE);
            }
        });

        //when user enter a custom query in text view and clicks on submit query button
        //display results in tablelayout
        submitQuery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mTableLayout.removeAllViews();
                customQuery.setVisibility(View.GONE);

                ArrayList<Cursor> alc2;
                String Query10 = customquerytext.getText().toString();
                Log.d("query", Query10);
                //pass the query to getdata method and get results
                alc2 = mDebugDBHelper.getData(Query10);
                final Cursor c4 = alc2.get(0);
                Cursor Message2 = alc2.get(1);
                Message2.moveToLast();

                //if the query returns results display the results in table layout
                if (Message2.getString(0).equalsIgnoreCase("Success")) {

                    mMessageTV.setBackgroundColor(Color.parseColor("#2ecc71"));
                    if (c4 != null) {
                        mMessageTV.setText("Queru Executed successfully.Number of rows returned :" + c4.getCount());
                        if (c4.getCount() > 0) {
                            if (mIndexInfo.mainCursor != c4) {
                                closeMainCursor();
                            }
                            mIndexInfo.mainCursor = c4;
                            refreshTable(1);
                        }
                    } else {
                        mMessageTV.setText("Queru Executed successfully");
                        refreshTable(1);
                    }

                } else {
                    //if there is any error we displayed the error message at the bottom of the screen
                    mMessageTV.setBackgroundColor(Color.parseColor("#e74c3c"));
                    mMessageTV.setText("Error:" + Message2.getString(0));

                }
                closeCursor(Message2);
            }
        });
        //layout parameters for each row in the table
        mTableRowParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTableRowParams.setMargins(0, 0, 2, 0);

        // a query which returns a cursor with the list of tables in the database.We use this cursor to populate
        // spinner in the first row
        alc = mDebugDBHelper.getData(Query);

        //the first cursor has reults of the query
        mFirstCursor = alc.get(0);

        //the second cursor has error messages
        Cursor Message = alc.get(1);

        Message.moveToLast();
        String msg = Message.getString(0);
        closeCursor(Message);
        Log.d("Message from sql = ", msg);

        ArrayList<String> tablenames = new ArrayList<String>();

        if (mFirstCursor != null) {

            mFirstCursor.moveToFirst();
            tablenames.add("click here");
            do {
                //add names of the table to tablenames array list
                tablenames.add(mFirstCursor.getString(0));
            } while (mFirstCursor.moveToNext());
        }
        //an array adapter with above created arraylist
        ArrayAdapter<String> tablenamesadapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, tablenames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);
                TextView adap = (TextView) v;
                adap.setTextSize(20);

                return adap;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);

                return v;
            }
        };

        tablenamesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (tablenamesadapter != null) {
            //set the adpater to mSelectTable spinner
            mSelectTable.setAdapter(tablenamesadapter);
        }

        // when a table names is selecte display the table contents
        mSelectTable.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                if (pos == 0 && !mIndexInfo.isCustomQuery) {
                    secondrow.setVisibility(View.GONE);
                    mHorizontalScrollView.setVisibility(View.GONE);
                    thirdrow.setVisibility(View.GONE);
                    spinnertable.setVisibility(View.GONE);
                    help.setVisibility(View.GONE);
                    mMessageTV.setVisibility(View.GONE);
                    customquerytext.setVisibility(View.GONE);
                    submitQuery.setVisibility(View.GONE);
                    customQuery.setVisibility(View.GONE);
                }
                if (pos != 0) {
                    secondrow.setVisibility(View.VISIBLE);
                    spinnertable.setVisibility(View.VISIBLE);
                    help.setVisibility(View.VISIBLE);
                    customquerytext.setVisibility(View.GONE);
                    submitQuery.setVisibility(View.GONE);
                    customQuery.setVisibility(View.VISIBLE);
                    mHorizontalScrollView.setVisibility(View.VISIBLE);

                    mMessageTV.setVisibility(View.VISIBLE);

                    thirdrow.setVisibility(View.VISIBLE);
                    mFirstCursor.moveToPosition(pos - 1);
                    mIndexInfo.cursorPostion = pos - 1;
                    //displaying the content of the table which is selected in the mSelectTable spinner
                    Log.d("selected table name is", "" + mFirstCursor.getString(0));
                    mIndexInfo.tableName = mFirstCursor.getString(0);
                    mMessageTV.setText("Error Messages will be displayed here");
                    mMessageTV.setBackgroundColor(Color.WHITE);

                    //removes any data if present in the table layout
                    mTableLayout.removeAllViews();
                    ArrayList<String> spinnertablevalues = new ArrayList<String>();
                    spinnertablevalues.add("Click here to change this table");
                    spinnertablevalues.add("Add row to this table");
                    spinnertablevalues.add("Delete this table");
                    spinnertablevalues.add("Drop this table");
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item, spinnertablevalues);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

                    // a array adapter which add values to the spinner which helps in user making changes to the table
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                            android.R.layout.simple_spinner_item, spinnertablevalues) {

                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);

                            v.setBackgroundColor(Color.WHITE);
                            TextView adap = (TextView) v;
                            adap.setTextSize(20);

                            return adap;
                        }

                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);

                            v.setBackgroundColor(Color.WHITE);

                            return v;
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnertable.setAdapter(adapter);
                    String Query2 = "select * from " + mFirstCursor.getString(0);
                    Log.d("", "" + Query2);

                    //getting contents of the table which user selected from the mSelectTable spinner
                    ArrayList<Cursor> alc2 = mDebugDBHelper.getData(Query2);
                    closeCursor(alc2.get(1));
                    final Cursor c2 = alc2.get(0);
                    //saving cursor to the static indexinfo class which can be resued by the other functions
                    if (mIndexInfo.mainCursor != c2) {
                        closeMainCursor();
                    }
                    mIndexInfo.mainCursor = c2;

                    // if the cursor returned form the database is not null we display the data in table layout
                    if (c2 != null) {
                        int counts = c2.getCount();
                        mIndexInfo.isEmpty = false;
                        Log.d("counts", "" + counts);
                        mCountTv.setText("" + counts);

                        //the spinnertable has the 3 items to drop , delete , add row to the table selected by the user
                        //here we handle the 3 operations.
                        spinnertable.setOnItemSelectedListener((new OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position,
                                                       long id) {

                                ((TextView) parentView.getChildAt(0)).setTextColor(Color.rgb(0, 0, 0));
                                //when user selects to drop the table the below code in if block will be executed
                                if (spinnertable.getSelectedItem().toString().equals("Drop this table")) {
                                    // an alert dialog to confirm user selection
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {

                                                new AlertDialog.Builder(mActivity)
                                                        .setTitle("Are you sure ?")
                                                        .setMessage("Pressing yes will remove " + mIndexInfo.tableName
                                                                + " table from database")
                                                        .setPositiveButton("yes",
                                                                new DialogInterface.OnClickListener() {
                                                                    // when user confirms by clicking on yes we drop
                                                                    // the table by executing drop table query
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int which) {

                                                                        String Query6 =
                                                                                "Drop table " + mIndexInfo.tableName;
                                                                        ArrayList<Cursor> aldropt =
                                                                                mDebugDBHelper.getData(Query6);
                                                                        closeCursor(aldropt.get(0));
                                                                        Cursor tempc = aldropt.get(1);
                                                                        tempc.moveToLast();
                                                                        Log.d("Drop table Mesage", tempc.getString(0));
                                                                        if (tempc.getString(0).equalsIgnoreCase(
                                                                                "Success")) {
                                                                            mMessageTV.setBackgroundColor(
                                                                                    Color.parseColor("#2ecc71"));
                                                                            mMessageTV.setText(mIndexInfo.tableName
                                                                                    + "Dropped successfully");
                                                                            refreshactivity();
                                                                        } else {
                                                                            //if there is any error we displayd the
                                                                            // error message at the bottom of the screen
                                                                            mMessageTV.setBackgroundColor(
                                                                                    Color.parseColor("#e74c3c"));
                                                                            mMessageTV.setText(
                                                                                    "Error:" + tempc.getString(0));
                                                                            spinnertable.setSelection(0);
                                                                        }
                                                                        closeCursor(tempc);
                                                                    }
                                                                })
                                                        .setNegativeButton("No",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int which) {
                                                                        spinnertable.setSelection(0);
                                                                    }
                                                                })
                                                        .create().show();
                                            }
                                        }
                                    });

                                }
                                //when user selects to drop the table the below code in if block will be executed
                                if (spinnertable.getSelectedItem().toString().equals(
                                        "Delete this table")) {    // an alert dialog to confirm user selection
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {

                                                new AlertDialog.Builder(mActivity)
                                                        .setTitle("Are you sure?")
                                                        .setMessage("Clicking on yes will delete all the contents of "
                                                                + mIndexInfo.tableName + " table from database")
                                                        .setPositiveButton("yes",
                                                                new DialogInterface.OnClickListener() {

                                                                    // when user confirms by clicking on yes we drop
                                                                    // the table by executing delete table query
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int which) {
                                                                        String Query7 =
                                                                                "Delete  from " + mIndexInfo.tableName;
                                                                        Log.d("delete table query", Query7);
                                                                        ArrayList<Cursor> aldeletet =
                                                                                mDebugDBHelper.getData(
                                                                                        Query7);
                                                                        closeCursor(aldeletet.get(0));
                                                                        Cursor tempc = aldeletet.get(1);
                                                                        tempc.moveToLast();
                                                                        Log.d("Delete table Mesage", tempc.getString(
                                                                                0));
                                                                        if (tempc.getString(0).equalsIgnoreCase(
                                                                                "Success")) {
                                                                            mMessageTV.setBackgroundColor(
                                                                                    Color.parseColor("#2ecc71"));
                                                                            mMessageTV.setText(mIndexInfo.tableName
                                                                                    + " table content deleted "
                                                                                    + "successfully");
                                                                            mIndexInfo.isEmpty = true;
                                                                            refreshTable(0);
                                                                        } else {
                                                                            mMessageTV.setBackgroundColor(
                                                                                    Color.parseColor("#e74c3c"));
                                                                            mMessageTV.setText(
                                                                                    "Error:" + tempc.getString(0));
                                                                            spinnertable.setSelection(0);
                                                                        }
                                                                        closeCursor(tempc);
                                                                    }
                                                                })
                                                        .setNegativeButton("No",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int which) {
                                                                        spinnertable.setSelection(0);
                                                                    }
                                                                })
                                                        .create().show();
                                            }
                                        }
                                    });

                                }

                                //when user selects to add row to the table the below code in if block will be executed
                                if (spinnertable.getSelectedItem().toString().equals("Add row to this table")) {
                                    //we create a layout which has textviews with column names of the table and
                                    // edittexts where
                                    //user can enter value which will be inserted into the datbase.
                                    final LinkedList<TextView> addnewrownames = new LinkedList<TextView>();
                                    final LinkedList<EditText> addnewrowvalues = new LinkedList<EditText>();
                                    final ScrollView addrowsv = new ScrollView(mActivity);
                                    Cursor c4 = mIndexInfo.mainCursor;
                                    if (mIndexInfo.isEmpty) {
                                        getcolumnnames();
                                        for (int i = 0; i < mIndexInfo.emptyTableColumnNames.size(); i++) {
                                            String cname = mIndexInfo.emptyTableColumnNames.get(i);
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText(cname);
                                            addnewrownames.add(tv);

                                        }
                                        for (int i = 0; i < addnewrownames.size(); i++) {
                                            EditText et = new EditText(getApplicationContext());

                                            addnewrowvalues.add(et);
                                        }

                                    } else {
                                        for (int i = 0; i < c4.getColumnCount(); i++) {
                                            String cname = c4.getColumnName(i);
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText(cname);
                                            addnewrownames.add(tv);

                                        }
                                        for (int i = 0; i < addnewrownames.size(); i++) {
                                            EditText et = new EditText(getApplicationContext());

                                            addnewrowvalues.add(et);
                                        }
                                    }
                                    final RelativeLayout addnewlayout = new RelativeLayout(mActivity);
                                    RelativeLayout.LayoutParams addnewparams = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    addnewparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                    for (int i = 0; i < addnewrownames.size(); i++) {
                                        TextView tv = addnewrownames.get(i);
                                        EditText et = addnewrowvalues.get(i);
                                        int t = i + 400;
                                        int k = i + 500;
                                        int lid = i + 600;

                                        tv.setId(t);
                                        tv.setTextColor(Color.parseColor("#000000"));
                                        et.setBackgroundColor(Color.parseColor("#F2F2F2"));
                                        et.setTextColor(Color.parseColor("#000000"));
                                        et.setId(k);
                                        final LinearLayout ll = new LinearLayout(mActivity);
                                        LinearLayout.LayoutParams tvl = new LinearLayout.LayoutParams(0, 100);
                                        tvl.weight = 1;
                                        ll.addView(tv, tvl);
                                        ll.addView(et, tvl);
                                        ll.setId(lid);

                                        Log.d("Edit Text Value", "" + et.getText().toString());

                                        RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        rll.addRule(RelativeLayout.BELOW, ll.getId() - 1);
                                        rll.setMargins(0, 20, 0, 0);
                                        addnewlayout.addView(ll, rll);

                                    }
                                    addnewlayout.setBackgroundColor(Color.WHITE);
                                    addrowsv.addView(addnewlayout);
                                    Log.d("Button Clicked", "");
                                    //the above form layout which we have created above will be displayed in an alert
                                    // dialog
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {
                                                new AlertDialog.Builder(mActivity)
                                                        .setTitle("values")
                                                        .setCancelable(false)
                                                        .setView(addrowsv)
                                                        .setPositiveButton("Add",
                                                                new DialogInterface.OnClickListener() {
                                                                    // after entering values if user clicks on add we
                                                                    // take the values and run a insert query
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int which) {

                                                                        mIndexInfo.index = 10;
                                                                        //mTableLayout.removeAllViews();
                                                                        //trigger select table listener to be triggerd
                                                                        String Query4 =
                                                                                "Insert into " + mIndexInfo.tableName
                                                                                        + " (";
                                                                        for (int i = 0; i < addnewrownames.size();
                                                                             i++) {

                                                                            TextView tv = addnewrownames.get(i);
                                                                            tv.getText().toString();
                                                                            if (i == addnewrownames.size() - 1) {

                                                                                Query4 = Query4 + tv.getText()
                                                                                        .toString();

                                                                            } else {
                                                                                Query4 =
                                                                                        Query4 + tv.getText().toString()
                                                                                                + ", ";
                                                                            }
                                                                        }
                                                                        Query4 = Query4 + " ) VALUES ( ";
                                                                        for (int i = 0; i < addnewrownames.size();
                                                                             i++) {
                                                                            EditText et = addnewrowvalues.get(i);
                                                                            et.getText().toString();

                                                                            if (i == addnewrownames.size() - 1) {

                                                                                Query4 = Query4 + "'" + et.getText()
                                                                                        .toString() + "' ) ";
                                                                            } else {
                                                                                Query4 = Query4 + "'" + et.getText()
                                                                                        .toString() + "' , ";
                                                                            }

                                                                        }
                                                                        //this is the insert query which has been
                                                                        // generated
                                                                        Log.d("Insert Query", Query4);
                                                                        ArrayList<Cursor> altc = mDebugDBHelper.getData(
                                                                                Query4);
                                                                        closeCursor(altc.get(0));
                                                                        Cursor tempc = altc.get(1);
                                                                        tempc.moveToLast();
                                                                        Log.d("Add New Row", tempc.getString(0));
                                                                        if (tempc.getString(0).equalsIgnoreCase(
                                                                                "Success")) {
                                                                            mMessageTV.setBackgroundColor(
                                                                                    Color.parseColor("#2ecc71"));
                                                                            mMessageTV.setText(
                                                                                    "New Row added succesfully to "
                                                                                            + mIndexInfo.tableName);
                                                                            refreshTable(0);
                                                                        } else {
                                                                            mMessageTV.setBackgroundColor(
                                                                                    Color.parseColor("#e74c3c"));
                                                                            mMessageTV.setText(
                                                                                    "Error:" + tempc.getString(0));
                                                                            spinnertable.setSelection(0);
                                                                        }
                                                                        closeCursor(tempc);
                                                                    }
                                                                })
                                                        .setNegativeButton("close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int which) {
                                                                        spinnertable.setSelection(0);
                                                                    }
                                                                })
                                                        .create().show();
                                            }
                                        }
                                    });
                                }
                            }

                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        }));

                        //display the first row of the table with column names of the table selected by the user
                        TableRow tableheader = new TableRow(getApplicationContext());

                        tableheader.setBackgroundColor(Color.BLACK);
                        tableheader.setPadding(0, 2, 0, 2);
                        for (int k = 0; k < c2.getColumnCount(); k++) {
                            LinearLayout cell = new LinearLayout(mActivity);
                            cell.setBackgroundColor(Color.WHITE);
                            cell.setLayoutParams(mTableRowParams);
                            final TextView tableheadercolums = new TextView(getApplicationContext());
                            // tableheadercolums.setBackgroundDrawable(gd);
                            tableheadercolums.setPadding(0, 0, 4, 3);
                            tableheadercolums.setText("" + c2.getColumnName(k));
                            tableheadercolums.setTextColor(Color.parseColor("#000000"));

                            //columsView.setLayoutParams(mTableRowParams);
                            cell.addView(tableheadercolums);
                            tableheader.addView(cell);

                        }
                        mTableLayout.addView(tableheader);
                        c2.moveToFirst();

                        //after displaying columnnames in the first row  we display data in the remaining columns
                        //the below paginatetbale function will display the first 10 tuples of the tables
                        //the remaining tuples can be viewed by clicking on the mNextBtn button
                        paginatetable(c2.getCount());

                    } else {
                        //if the cursor returned from the database is empty we show that table is empty
                        help.setVisibility(View.GONE);
                        mTableLayout.removeAllViews();
                        getcolumnnames();
                        TableRow tableheader2 = new TableRow(getApplicationContext());
                        tableheader2.setBackgroundColor(Color.BLACK);
                        tableheader2.setPadding(0, 2, 0, 2);

                        LinearLayout cell = new LinearLayout(mActivity);
                        cell.setBackgroundColor(Color.WHITE);
                        cell.setLayoutParams(mTableRowParams);
                        final TextView tableheadercolums = new TextView(getApplicationContext());

                        tableheadercolums.setPadding(0, 0, 4, 3);
                        tableheadercolums.setText("   Table   Is   Empty   ");
                        tableheadercolums.setTextSize(30);
                        tableheadercolums.setTextColor(Color.RED);

                        cell.addView(tableheadercolums);
                        tableheader2.addView(cell);

                        mTableLayout.addView(tableheader2);

                        mCountTv.setText("" + 0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    //get columnnames of the empty tables and save them in a array list
    public void getcolumnnames() {
        ArrayList<Cursor> alc3 = mDebugDBHelper.getData("PRAGMA table_info(" + mIndexInfo.tableName + ")");
        closeCursor(alc3.get(1));
        Cursor c5 = alc3.get(0);
        mIndexInfo.isEmpty = true;
        if (c5 != null) {
            mIndexInfo.isEmpty = true;

            ArrayList<String> emptytablecolumnnames = new ArrayList<String>();
            c5.moveToFirst();
            do {
                emptytablecolumnnames.add(c5.getString(1));
            } while (c5.moveToNext());
            mIndexInfo.emptyTableColumnNames = emptytablecolumnnames;
        }
        closeCursor(c5);
    }

    //displays alert dialog from which use can update or delete a row
    public void updateDeletePopup(int row) {
        Cursor c2 = mIndexInfo.mainCursor;
        // a spinner which gives options to update or delete the row which user has selected
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Click Here to Change this row");
        spinnerArray.add("Update this row");
        spinnerArray.add("Delete this row");

        //create a layout with text values which has the column names and
        //edit texts which has the values of the row which user has selected
        final ArrayList<String> value_string = mIndexInfo.valueString;
        final LinkedList<TextView> columnames = new LinkedList<TextView>();
        final LinkedList<EditText> columvalues = new LinkedList<EditText>();

        for (int i = 0; i < c2.getColumnCount(); i++) {
            String cname = c2.getColumnName(i);
            TextView tv = new TextView(getApplicationContext());
            tv.setText(cname);
            columnames.add(tv);

        }
        for (int i = 0; i < columnames.size(); i++) {
            String cv = value_string.get(i);
            EditText et = new EditText(getApplicationContext());
            value_string.add(cv);
            et.setText(cv);
            columvalues.add(et);
        }

        int lastrid = 0;
        // all text views , edit texts are added to this relative layout lp
        final RelativeLayout lp = new RelativeLayout(mActivity);
        lp.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        final ScrollView updaterowsv = new ScrollView(mActivity);
        LinearLayout lcrud = new LinearLayout(mActivity);

        LinearLayout.LayoutParams paramcrudtext = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        paramcrudtext.setMargins(0, 20, 0, 0);

        //spinner which displays update , delete options
        final Spinner crud_dropdown = new Spinner(getApplicationContext());

        ArrayAdapter<String> crudadapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, spinnerArray) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);
                TextView adap = (TextView) v;
                adap.setTextSize(20);

                return adap;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                v.setBackgroundColor(Color.WHITE);

                return v;
            }
        };

        crudadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        crud_dropdown.setAdapter(crudadapter);
        lcrud.setId(R.id.debug_db_ll);
        lcrud.addView(crud_dropdown, paramcrudtext);

        RelativeLayout.LayoutParams rlcrudparam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlcrudparam.addRule(RelativeLayout.BELOW, lastrid);

        lp.addView(lcrud, rlcrudparam);
        for (int i = 0; i < columnames.size(); i++) {
            TextView tv = columnames.get(i);
            EditText et = columvalues.get(i);
            int t = i + 100;
            int k = i + 200;
            int lid = i + 300;

            tv.setId(t);
            tv.setTextColor(Color.parseColor("#000000"));
            et.setBackgroundColor(Color.parseColor("#F2F2F2"));

            et.setTextColor(Color.parseColor("#000000"));
            et.setId(k);
            Log.d("text View Value", "" + tv.getText().toString());
            final LinearLayout ll = new LinearLayout(mActivity);
            ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ll.setId(lid);
            LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(0, 100);
            lpp.weight = 1;
            tv.setLayoutParams(lpp);
            et.setLayoutParams(lpp);
            ll.addView(tv);
            ll.addView(et);

            Log.d("Edit Text Value", "" + et.getText().toString());

            RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rll.addRule(RelativeLayout.BELOW, ll.getId() - 1);
            rll.setMargins(0, 20, 0, 0);
            lastrid = ll.getId();
            lp.addView(ll, rll);

        }

        updaterowsv.addView(lp);
        //after the layout has been created display it in a alert dialog
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("values")
                            .setView(updaterowsv)
                            .setCancelable(false)
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        //this code will be executed when user changes values of edit text or spinner
                                        // and clicks on ok button
                                        public void onClick(DialogInterface dialog, int which) {

                                            //get spinner value
                                            String spinner_value = crud_dropdown.getSelectedItem().toString();

                                            //it he spinner value is update this row get the values from
                                            //edit text fields generate a update query and execute it
                                            if (spinner_value.equalsIgnoreCase("Update this row")) {
                                                mIndexInfo.index = 10;
                                                String Query3 = "UPDATE " + mIndexInfo.tableName + " SET ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    EditText etc = columvalues.get(i);

                                                    if (!etc.getText().toString().equals("null")) {

                                                        Query3 = Query3 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {

                                                            Query3 = Query3 + "'" + etc.getText().toString() + "'";

                                                        } else {

                                                            Query3 = Query3 + "'" + etc.getText().toString() + "' , ";

                                                        }
                                                    }

                                                }
                                                Query3 = Query3 + " where ";
                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    if (!value_string.get(i).equals("null")) {

                                                        Query3 = Query3 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {

                                                            Query3 = Query3 + "'" + value_string.get(i) + "' ";

                                                        } else {
                                                            Query3 = Query3 + "'" + value_string.get(i) + "' and ";
                                                        }

                                                    }
                                                }
                                                Log.d("Update Query", Query3);
                                                //dbm.getData(Query3);
                                                ArrayList<Cursor> aluc = mDebugDBHelper.getData(Query3);
                                                closeCursor(aluc.get(0));
                                                Cursor tempc = aluc.get(1);
                                                tempc.moveToLast();
                                                Log.d("Update Mesage", tempc.getString(0));

                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    mMessageTV.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    mMessageTV.setText(
                                                            mIndexInfo.tableName + " table Updated Successfully");
                                                    refreshTable(0);
                                                } else {
                                                    mMessageTV.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    mMessageTV.setText("Error:" + tempc.getString(0));
                                                }
                                                closeCursor(tempc);
                                            }
                                            //it he spinner value is delete this row get the values from
                                            //edit text fields generate a delete query and execute it

                                            if (spinner_value.equalsIgnoreCase("Delete this row")) {

                                                mIndexInfo.index = 10;
                                                String Query5 = "DELETE FROM " + mIndexInfo.tableName + " WHERE ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    if (!value_string.get(i).equals("null")) {

                                                        Query5 = Query5 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {

                                                            Query5 = Query5 + "'" + value_string.get(i) + "' ";

                                                        } else {
                                                            Query5 = Query5 + "'" + value_string.get(i) + "' and ";
                                                        }

                                                    }
                                                }
                                                Log.d("Delete Query", Query5);

                                                mDebugDBHelper.getData(Query5);

                                                ArrayList<Cursor> aldc = mDebugDBHelper.getData(Query5);
                                                closeCursor(aldc.get(0));
                                                Cursor tempc = aldc.get(1);
                                                tempc.moveToLast();
                                                Log.d("Update Mesage", tempc.getString(0));

                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    mMessageTV.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    mMessageTV.setText(
                                                            "Row deleted from " + mIndexInfo.tableName + " table");
                                                    refreshTable(0);
                                                } else {
                                                    mMessageTV.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    mMessageTV.setText("Error:" + tempc.getString(0));
                                                }
                                                closeCursor(tempc);
                                            }
                                        }

                                    })
                            .setNegativeButton("close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                            .create().show();
                }
            }
        });
    }

    public void refreshactivity() {

        finish();
        startActivity(getIntent());
    }

    public void refreshTable(int d) {
        Cursor c3 = null;
        mTableLayout.removeAllViews();
        if (d == 0) {
            String Query8 = "select * from " + mIndexInfo.tableName;
            ArrayList<Cursor> alc3 = mDebugDBHelper.getData(Query8);
            c3 = alc3.get(0);
            if (alc3.get(1) != null && !alc3.get(1).isClosed()) {
                alc3.get(1).close();
            }
            //saving cursor to the static indexinfo class which can be resued by the other functions
            if (c3 != mIndexInfo.mainCursor) {
                closeMainCursor();
            }
            mIndexInfo.mainCursor = c3;
        }
        if (d == 1) {
            c3 = mIndexInfo.mainCursor;
        }
        // if the cursor returened form tha database is not null we display the data in table layout
        if (c3 != null) {
            int counts = c3.getCount();

            Log.d("counts", "" + counts);
            mCountTv.setText("" + counts);
            TableRow tableheader = new TableRow(getApplicationContext());

            tableheader.setBackgroundColor(Color.BLACK);
            tableheader.setPadding(0, 2, 0, 2);
            for (int k = 0; k < c3.getColumnCount(); k++) {
                LinearLayout cell = new LinearLayout(mActivity);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(mTableRowParams);
                final TextView tableheadercolums = new TextView(getApplicationContext());
                tableheadercolums.setPadding(0, 0, 4, 3);
                tableheadercolums.setText("" + c3.getColumnName(k));
                tableheadercolums.setTextColor(Color.parseColor("#000000"));
                cell.addView(tableheadercolums);
                tableheader.addView(cell);

            }
            mTableLayout.addView(tableheader);
            c3.moveToFirst();

            //after displaying column names in the first row  we display data in the remaining columns
            //the below paginate table function will display the first 10 tuples of the tables
            //the remaining tuples can be viewed by clicking on the mNextBtn button
            paginatetable(c3.getCount());
        } else {

            TableRow tableheader2 = new TableRow(getApplicationContext());
            tableheader2.setBackgroundColor(Color.BLACK);
            tableheader2.setPadding(0, 2, 0, 2);

            LinearLayout cell = new LinearLayout(mActivity);
            cell.setBackgroundColor(Color.WHITE);
            cell.setLayoutParams(mTableRowParams);

            final TextView tableheadercolums = new TextView(getApplicationContext());
            tableheadercolums.setPadding(0, 0, 4, 3);
            tableheadercolums.setText("   Table   Is   Empty   ");
            tableheadercolums.setTextSize(30);
            tableheadercolums.setTextColor(Color.RED);

            cell.addView(tableheadercolums);
            tableheader2.addView(cell);

            mTableLayout.addView(tableheader2);

            mCountTv.setText("" + 0);
        }

    }

    //the function which displays tuples from database in a table layout
    public void paginatetable(final int number) {

        final Cursor c3 = mIndexInfo.mainCursor;
        mIndexInfo.numberOfPages = (c3.getCount() / 10) + 1;
        mIndexInfo.currentPage = 1;
        c3.moveToFirst();
        int currentrow = 0;

        //display the first 10 tuples of the table selected by user
        do {

            final TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setBackgroundColor(Color.BLACK);
            tableRow.setPadding(0, 2, 0, 2);

            for (int j = 0; j < c3.getColumnCount(); j++) {
                LinearLayout cell = new LinearLayout(this);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(mTableRowParams);
                final TextView columsView = new TextView(getApplicationContext());
                String column_data = "";
                try {
                    column_data = c3.getString(j);
                } catch (Exception e) {
                    // Column data is not a string , do not display it
                }
                columsView.setText(column_data);
                columsView.setTextColor(Color.parseColor("#000000"));
                columsView.setPadding(0, 0, 4, 3);
                cell.addView(columsView);
                tableRow.addView(cell);

            }

            tableRow.setVisibility(View.VISIBLE);
            currentrow = currentrow + 1;
            //we create listener for each table row when clicked a alert dialog will be displayed
            //from where user can update or delete the row
            tableRow.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {

                    final ArrayList<String> value_string = new ArrayList<String>();
                    for (int i = 0; i < c3.getColumnCount(); i++) {
                        LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(i);
                        TextView tc = (TextView) llcolumn.getChildAt(0);

                        String cv = tc.getText().toString();
                        value_string.add(cv);

                    }
                    mIndexInfo.valueString = value_string;
                    //the below function will display the alert dialog
                    updateDeletePopup(0);
                }
            });
            mTableLayout.addView(tableRow);

        } while (c3.moveToNext() && currentrow < 10);

        mIndexInfo.index = currentrow;

        // when user clicks on the mPreviousBtn button update the table with the mPreviousBtn 10 tuples from the
        // database
        mPreviousBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int tobestartindex = (mIndexInfo.currentPage - 2) * 10;

                //if the tbale layout has the first 10 tuples then toast that this is the first page
                if (mIndexInfo.currentPage == 1) {
                    Toast.makeText(getApplicationContext(), "This is the first page", Toast.LENGTH_LONG).show();
                } else {
                    mIndexInfo.currentPage = mIndexInfo.currentPage - 1;
                    c3.moveToPosition(tobestartindex);

                    boolean decider = true;
                    for (int i = 1; i < mTableLayout.getChildCount(); i++) {
                        TableRow tableRow = (TableRow) mTableLayout.getChildAt(i);

                        if (decider) {
                            tableRow.setVisibility(View.VISIBLE);
                            for (int j = 0; j < tableRow.getChildCount(); j++) {
                                LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(j);
                                TextView columsView = (TextView) llcolumn.getChildAt(0);

                                columsView.setText("" + c3.getString(j));

                            }
                            decider = !c3.isLast();
                            if (!c3.isLast()) {
                                c3.moveToNext();
                            }
                        } else {
                            tableRow.setVisibility(View.GONE);
                        }

                    }

                    mIndexInfo.index = tobestartindex;

                    Log.d("index =", "" + mIndexInfo.index);
                }
            }
        });

        // when user clicks on the mNextBtn button update the table with the mNextBtn 10 tuples from the database
        mNextBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //if there are no tuples to be shown toast that this the last page
                if (mIndexInfo.currentPage >= mIndexInfo.numberOfPages) {
                    Toast.makeText(getApplicationContext(), "This is the last page", Toast.LENGTH_LONG).show();
                } else {
                    mIndexInfo.currentPage = mIndexInfo.currentPage + 1;
                    boolean decider = true;

                    for (int i = 1; i < mTableLayout.getChildCount(); i++) {
                        TableRow tableRow = (TableRow) mTableLayout.getChildAt(i);

                        if (decider) {
                            tableRow.setVisibility(View.VISIBLE);
                            for (int j = 0; j < tableRow.getChildCount(); j++) {
                                LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(j);
                                TextView columsView = (TextView) llcolumn.getChildAt(0);

                                columsView.setText("" + c3.getString(j));

                            }
                            decider = !c3.isLast();
                            if (!c3.isLast()) {
                                c3.moveToNext();
                            }
                        } else {
                            tableRow.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

}