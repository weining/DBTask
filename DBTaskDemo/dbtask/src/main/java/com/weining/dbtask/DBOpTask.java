package com.weining.dbtask;

import java.security.InvalidParameterException;

import com.weining.asynctask.CommonAsyncTask;

/**
 * DB操作的Task执行体
 * Created by xueweining on 2015/3/30.
 */
public class DBOpTask extends CommonAsyncTask<DBOp, Integer, DBOp> {
    private volatile DBOpWorker mWorker = null;
    private DBOp mOp = null;

    public DBOpTask(DBOp atom) {
        if (atom == null) {
            throw new InvalidParameterException("DBOpTask parameter null");
        }
        mOp = atom;
    }

    @Override
    protected DBOp doInBackground(DBOp... params) {
        mWorker = new DBOpWorker(mOp);
        mWorker.execute();
        return mOp;
    }

    @Override
    protected void onPostExecute(DBOp result) {
        super.onPostExecute(result);
        mOp.onResponse();
    }

    @Override
    protected void onPreCancel() {
        super.onPreCancel();
        mOp.onResponse();
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mWorker != null) {
            mWorker.cancel();
        }
    }
}
