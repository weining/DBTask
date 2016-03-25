package com.weining.dbtask;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * 对ORMlite的QueryBuilder封装
 * Created by xueweining on 2015/4/7.
 */
public class DBAsyncQueryBuilder<I, ID> extends DBAsyncBaseBuilder {
    public DBAsyncQueryBuilder(Dao<I, ID> mDao) {
        super(mDao, mDao.queryBuilder());
    }

    @Override
    public QueryBuilder getOrmBuilder() {
        return (QueryBuilder) super.getOrmBuilder();
    }

    public DBAsyncQueryBuilder orderBy(String column, boolean ascending) {
        getOrmBuilder().orderBy(column, ascending);
        return this;
    }

    public DBAsyncQueryBuilder limit(long rowCount) {
        getOrmBuilder().limit(rowCount);
        return this;
    }

    public DBAsyncQueryBuilder offset(long startRow) throws SQLException {
        getOrmBuilder().offset(startRow);
        return this;
    }

    public void countOf(DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        new DBOp<>(getOrmDao(), DBOp.AtomAction.QUERY_COUNT_OF).queryBuilder(getOrmBuilder()).after(afterDoingBackground)
                .postBack(resultCallback).execute();
    }

    public DBOp countOfSync(DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        return new DBOp<>(getOrmDao(), DBOp.AtomAction.QUERY_COUNT_OF).queryBuilder(getOrmBuilder()).executeSync();
    }

    public void queryList(DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        new DBOp<>(getOrmDao(), DBOp.AtomAction.QUERY_BUILDER_LIST).queryBuilder(getOrmBuilder()).after(afterDoingBackground).postBack(resultCallback).execute();
    }

    public DBOp queryListSync() {
        return new DBOp<>(getOrmDao(), DBOp.AtomAction.QUERY_BUILDER_LIST).queryBuilder(getOrmBuilder()).executeSync();
    }

    public void queryForFirst(DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        new DBOp<>(getOrmDao(), DBOp.AtomAction.QUERY_BUILDER_FORFIRST).queryBuilder(getOrmBuilder()).after(afterDoingBackground).postBack(resultCallback).execute();
    }

    public DBOp queryForFirstSync() {
        return new DBOp<>(getOrmDao(), DBOp.AtomAction.QUERY_BUILDER_FORFIRST).queryBuilder(getOrmBuilder()).executeSync();
    }
}
