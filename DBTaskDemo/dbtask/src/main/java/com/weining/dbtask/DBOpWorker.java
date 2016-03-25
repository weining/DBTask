package com.weining.dbtask;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

/**
 * 不同DBAction操作的工作类
 * Created by xueweining on 2015/3/31.
 */
public class DBOpWorker<I, ID> {
    private static final String TAG = "DBOpWorker";
    protected AtomicBoolean mIsCanceled = null;
    private DBOp<I, ID> mOp = null;

    /**
     *
     */
    public void cancel() {
        mIsCanceled.set(true);
    }

    /**
     * @return
     */
    public boolean IsCanceled() {
        return mIsCanceled.get();
    }

    /**
     * @param dbOp
     */
    public DBOpWorker(DBOp dbOp) {
        if (dbOp == null || dbOp.getAction() == null) {
            throw new InvalidParameterException("DBOp Parameter is null");
        }
        mIsCanceled = new AtomicBoolean(false);
        mOp = dbOp;
    }

    public void execute() {
        switch (mOp.getAction()) {
            case QUERY_FORID:
                queryForId();
                break;
            case QUERY_BUILDER_LIST:
                queryBuilderList();
                break;
            case QUERY_FOREQ:
                queryForEq();
                break;
            case QUERY_BUILDER_FORFIRST:
                queryBuilderForFirst();
                break;
            case CREATE_OR_UPDATE:
                createOrUpdate();
                break;
            case DELETE_FORID:
                deleteForId();
                break;
            case DELETE_BUILDER:
                deleteBuilder();
                break;
            case CUSTOM:
                custom();
                break;
            case CUSTOM_BATCH:
                customBatch();
                break;
            case QUERY_COUNT_OF:
                countOf();
                break;
        }
        DBOp.IAfterDoingBackground afterDoingBackground = mOp.getAfterDoingBackground();
        if (afterDoingBackground != null) {
            afterDoingBackground.onAfterDoingBackground(mOp.isSuccess(), mOp.getResult(), mOp);
        }
    }

    private void queryForId() {
        if (mIsCanceled.get())
            return;
        if (mOp.getTargetId() == null) {
            throw new InvalidParameterException("DBQueryId parameter null");
        }
        try {
            mOp.setResult(mOp.getDao().queryForId(mOp.getTargetId()));
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void queryBuilderForFirst() {
        if (mIsCanceled.get())
            return;
        if (mOp.getQueryBuilder() == null) {
            throw new InvalidParameterException("DBQueryBuilder parameter null");
        }
        try {
            mOp.setResult(mOp.getQueryBuilder().queryForFirst());
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void queryBuilderList() {
        if (mIsCanceled.get())
            return;
        if (mOp.getQueryBuilder() == null) {
            throw new InvalidParameterException("DBQueryBuilder parameter null");
        }
        try {
            mOp.setResult(mOp.getQueryBuilder().query());
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void queryForEq() {
        if (mIsCanceled.get())
            return;
        if (mOp.getQueryBuilder() == null || mOp.getEqPair() == null) {
            throw new InvalidParameterException("DBQueryBuilder parameter null");
        }
        try {
            mOp.setResult(mOp.getDao().queryForEq(mOp.getEqPair().first, mOp.getEqPair().second));
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void createOrUpdate() {
        if (mIsCanceled.get())
            return;
        if (mOp.getCreateOrUpdateModel() == null) {
            throw new InvalidParameterException("DBCreateOrUpdateModel parameter null");
        }
        try {
            mOp.setResult(mOp.getDao().createOrUpdate(mOp.getCreateOrUpdateModel()));
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void deleteForId() {
        if (mIsCanceled.get())
            return;
        if (mOp.getTargetId() == null) {
            throw new InvalidParameterException("DBDeleteId parameter null");
        }
        try {
            mOp.setResult(mOp.getDao().deleteById(mOp.getTargetId()));
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void countOf() {
        if (mIsCanceled.get())
            return;
        if (mOp.getQueryBuilder() == null) {
            throw new InvalidParameterException("DBQueryBuilder parameter null");
        }
        try {
            mOp.setResult(mOp.getDao().countOf());
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void deleteBuilder() {
        if (mIsCanceled.get())
            return;
        if (mOp.getDeleteBuilder() == null) {
            throw new InvalidParameterException("DBDeleteBuilder parameter null");
        }
        try {
            mOp.setResult(mOp.getDeleteBuilder().delete());
            mOp.setSuccess(true);
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void custom() {
        if (mIsCanceled.get())
            return;
        if (mOp.getCustomOp() == null) {
            throw new InvalidParameterException("DBCustomOp parameter null");
        }
        mOp.setSuccess(mOp.getCustomOp().customDBOp(mOp));
    }

    public void customBatch() {
        if (mIsCanceled.get())
            return;
        if (mOp.getCustomOp() == null) {
            throw new InvalidParameterException("DBCustomOp parameter null");
        }
        try {
            mOp.getDao().callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    mOp.getCustomOp().customDBOp(mOp);
                    return null;
                }
            });
            mOp.setSuccess(true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            mOp.setSuccess(false);
        }
    }
}
