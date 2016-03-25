package com.weining.dbtask;

import com.j256.ormlite.dao.Dao;

/**
 * 对ORMlite的DAO封装
 * Created by xueweining on 2015/3/30.
 */
public class DBAsyncDao<I, ID> {
    private Dao<I, ID> mDao;

    public DBAsyncDao(Dao<I, ID> dao) {
        mDao = dao;
    }

    public DBAsyncQueryBuilder<I, ID> queryBuilder() {
        return new DBAsyncQueryBuilder<I, ID>(mDao);
    }

    public DBAsyncDeleteBuilder<I, ID> deleteBuilder() {
        return new DBAsyncDeleteBuilder<I, ID>(mDao);
    }

    public DBAsyncArranger queryForIdAsync(ID targetId, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return new DBAsyncArranger((DBOp.createDBOpQueryForId(mDao, targetId, afterDoingBackground, resultCallback)));
    }

    public DBOp queryForIdSync(ID targetId) {
        return DBOp.createDBOpQueryForId(mDao, targetId, null, null).executeSync();
    }

    public DBAsyncArranger queryWithBuilderForListAsync(DBAsyncQueryBuilder mQueryBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return new DBAsyncArranger((DBOp.createDBOpQueryBuilderList(mDao, mQueryBuilder, afterDoingBackground, resultCallback)));
    }

    public DBOp queryWithBuilderForList(DBAsyncQueryBuilder mQueryBuilder) {
        return DBOp.createDBOpQueryBuilderList(mDao, mQueryBuilder, null, null).executeSync();
    }

    public DBAsyncArranger queryWithBuilderForFirstAsync(DBAsyncQueryBuilder mQueryBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return new DBAsyncArranger((DBOp.createDBOpQueryBuilderForFirst(mDao, mQueryBuilder, afterDoingBackground, resultCallback)));
    }

    public DBOp queryWithBuilderForFirst(DBAsyncQueryBuilder mQueryBuilder) {
        return DBOp.createDBOpQueryBuilderForFirst(mDao, mQueryBuilder, null, null).executeSync();
    }

    public DBAsyncArranger queryForEqAsync(String column, Object value, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return new DBAsyncArranger(DBOp.createDBOpQueryForEq(mDao, column, value, afterDoingBackground, resultCallback));
    }

    public DBOp queryForEq(String column, Object value) {
        return DBOp.createDBOpQueryForEq(mDao, column, value, null, null).executeSync();
    }

    public DBAsyncArranger createOrUpdateAsync(I model, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return new DBAsyncArranger(DBOp.createDBOpCreateOrUpdate(mDao, model, afterDoingBackground, resultCallback));
    }

    public DBOp createOrUpdate(I model) {
        return DBOp.createDBOpCreateOrUpdate(mDao, model, null, null).executeSync();
    }

    public DBAsyncArranger deleteForIdAsync(ID targetId, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return new DBAsyncArranger(DBOp.createDBOpDeleteForId(mDao, targetId, afterDoingBackground, resultCallback));
    }

    public DBOp deleteForId(ID targetId) {
        return DBOp.createDBOpDeleteForId(mDao, targetId, null, null).executeSync();
    }

    public DBAsyncArranger deleteWithBuilderAsync(DBAsyncDeleteBuilder mDeleteBuilder, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return new DBAsyncArranger(DBOp.createDBOpDeleteBuilder(mDao, mDeleteBuilder, afterDoingBackground, resultCallback));
    }

    public DBOp deleteWithBuilder(DBAsyncDeleteBuilder mDeleteBuilder) {
        return DBOp.createDBOpDeleteBuilder(mDao, mDeleteBuilder, null, null).executeSync();
    }

    public DBAsyncArranger customAsync(DBOp.ICustomDBOp customDBOp, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return new DBAsyncArranger(DBOp.createDBOpCustom(mDao, customDBOp, afterDoingBackground, resultCallback));
    }

    public DBOp custom(DBOp.ICustomDBOp customDBOp) {
        return DBOp.createDBOpCustom(mDao, customDBOp, null, null).executeSync();
    }

    public DBAsyncArranger customBatchAsync(DBOp.ICustomDBOp customDBOp, DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute<Integer> resultCallback) {
        return new DBAsyncArranger(DBOp.createDBOpCustom(mDao, customDBOp, afterDoingBackground, resultCallback));
    }

    public DBOp customBatch(DBOp.ICustomDBOp customDBOp) {
        return DBOp.createDBOpCustom(mDao, customDBOp, null, null).executeSync();
    }

    public DBAsyncArranger countOfAsync(DBAsyncQueryBuilder mQueryBuilder,
                                        DBOp.IAfterDoingBackground afterDoingBackground,
                                        DBOp.IOnPostExecute<Integer> resultCallback) {
        return new DBAsyncArranger(
                DBOp.createDBOpQueryCountOf(mDao, mQueryBuilder, afterDoingBackground, resultCallback));
    }

    public DBOp countOf(DBAsyncQueryBuilder mQueryBuilder) {
        return DBOp.createDBOpQueryCountOf(mDao, mQueryBuilder, null, null).executeSync();
    }
}
