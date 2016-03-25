package com.weining.dbtask;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.StatementBuilder;

/**
 * 对ORMlite的Builder的基类封装
 * Created by xueweining on 2015/4/8.
 */
abstract class DBAsyncBaseBuilder<I, ID> {
    private StatementBuilder<I, ID> mStatementBuilder;
    private Dao<I, ID> mDao;
    DBAsyncBaseBuilder(Dao<I, ID> dao, StatementBuilder<I, ID> statementBuilder) {
        mDao = dao;
        mStatementBuilder = statementBuilder;
    }
    StatementBuilder<I, ID> getOrmBuilder() {
        return mStatementBuilder;
    }
    Dao<I, ID> getOrmDao() {
        return mDao;
    }

    public DBAsyncWhere<I, ID> where() {
        return new DBAsyncWhere(getOrmBuilder().where());
    }
}
