package com.weining.dbtask;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

/**
 * 对ORMlite的DeleteBuilder封装
 * Created by xueweining on 2015/4/7.
 */
public class DBAsyncDeleteBuilder<I, ID> extends DBAsyncBaseBuilder {
    public DBAsyncDeleteBuilder(Dao<I, ID> mDao) {
        super(mDao, mDao.deleteBuilder());
    }

    @Override
    public DeleteBuilder<I, ID> getOrmBuilder() {
        return (DeleteBuilder) super.getOrmBuilder();
    }

    public void delete(DBOp.IAfterDoingBackground afterDoingBackground, DBOp.IOnPostExecute resultCallback) {
        new DBOp<>(getOrmDao(), DBOp.AtomAction.DELETE_BUILDER).deleteBuilder(getOrmBuilder()).after(afterDoingBackground).postBack(resultCallback).execute();
    }

    public DBOp<I, ID> deleteSync() {
        return new DBOp<>(getOrmDao(), DBOp.AtomAction.DELETE_BUILDER).deleteBuilder(getOrmBuilder()).executeSync();
    }
}