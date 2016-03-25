package com.weining.dbtask;

/**
 * 连续异步DB操作调度器，主要是为DBOp的nextDBOp赋值，来支持若干异步DB操作的串行执行
 * Created by xueweining on 2015/4/7.
 */
public class DBAsyncArranger {
    private DBOp mDBop;
    private DBOp mTailDBOp;

    public DBAsyncArranger(DBOp op) {
        mDBop = op;
        mTailDBOp = op;
    }

    public <ID> DBAsyncArranger queryForId(ID targetId, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpQueryForId(mTailDBOp.getDao(), targetId, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public <ID> DBAsyncArranger queryForId(ID targetId, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return queryForId(targetId, afterDoingBackground, resultCallback, true);
    }

    public DBAsyncArranger queryWithBuilderForList(DBAsyncQueryBuilder mQueryBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpQueryBuilderList(mTailDBOp.getDao(), mQueryBuilder, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public DBAsyncArranger queryWithBuilderForList(DBAsyncQueryBuilder mQueryBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return queryWithBuilderForList(mQueryBuilder, afterDoingBackground, resultCallback, true);
    }

    public DBAsyncArranger queryWithBuilderForFirst(DBAsyncQueryBuilder mQueryBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpQueryBuilderForFirst(mTailDBOp.getDao(), mQueryBuilder, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public DBAsyncArranger queryWithBuilderForFirst(DBAsyncQueryBuilder mQueryBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return queryWithBuilderForFirst(mQueryBuilder, afterDoingBackground, resultCallback, true);
    }

    public DBAsyncArranger queryCountOfWithBuilder(DBAsyncQueryBuilder mQueryBuilder, DBOp
            .IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpQueryCountOf(mTailDBOp.getDao(), mQueryBuilder, afterDoingBackground,
                resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public DBAsyncArranger queryCountOfWithBuilder(DBAsyncQueryBuilder mQueryBuilder, DBOp
            .IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return queryCountOfWithBuilder(mQueryBuilder, afterDoingBackground, resultCallback, true);
    }

    public DBAsyncArranger queryForEq(String column, Object value, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpQueryForEq(mTailDBOp.getDao(), column, value, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public DBAsyncArranger queryForEq(String column, Object value, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return queryForEq(column, value, afterDoingBackground, resultCallback, true);
    }

    public <I> DBAsyncArranger createOrUpdate(I model, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpCreateOrUpdate(mTailDBOp.getDao(), model, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public <I> DBAsyncArranger createOrUpdate(I model, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return createOrUpdate(model, afterDoingBackground, resultCallback, true);
    }

    public <ID> DBAsyncArranger deleteForId(ID targetId, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpCreateOrUpdate(mTailDBOp.getDao(), targetId, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public <ID> DBAsyncArranger deleteForId(ID targetId, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return deleteForId(targetId, afterDoingBackground, resultCallback, true);
    }

    public DBAsyncArranger deleteWithBuilder(DBAsyncDeleteBuilder mDeleteBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpDeleteBuilder(mTailDBOp.getDao(), mDeleteBuilder, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public DBAsyncArranger deleteWithBuilder(DBAsyncDeleteBuilder mDeleteBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return deleteWithBuilder(mDeleteBuilder, afterDoingBackground, resultCallback, true);
    }

    public DBAsyncArranger custom(DBOp.ICustomDBOp customDBOp, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback, boolean continueIfFailed) {
        DBOp tempDBOp = DBOp.createDBOpCustom(mTailDBOp.getDao(), customDBOp, afterDoingBackground, resultCallback);
        mTailDBOp.next(tempDBOp, continueIfFailed);
        mTailDBOp = tempDBOp;
        return this;
    }

    public DBAsyncArranger custom(DBOp.ICustomDBOp customDBOp, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return custom(customDBOp, afterDoingBackground, resultCallback, true);
    }

    public void execute() {
        mDBop.execute();
    }
}
