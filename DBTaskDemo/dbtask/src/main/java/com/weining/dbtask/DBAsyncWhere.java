package com.weining.dbtask;

import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

/**
 * 对ORMlite的Where封装
 * Created by xueweining on 2015/4/7.
 */
public class DBAsyncWhere<I, ID> {
    private Where<I, ID> mWhere;
    public DBAsyncWhere(Where<I, ID> where) {
        mWhere = where;
    }

    public DBAsyncWhere<I, ID> eq(String column, Object value) throws SQLException{
        mWhere.eq(column, value);
        return this;
    }

    public DBAsyncWhere<I, ID> lt(String column, Object value) throws SQLException{
        mWhere.lt(column, value);
        return this;
    }

    public DBAsyncWhere<I, ID> gt(String column, Object value) throws SQLException{
        mWhere.gt(column, value);
        return this;
    }

    public DBAsyncWhere<I, ID> ge(String column, Object value) throws SQLException{
        mWhere.ge(column, value);
        return this;
    }

    public DBAsyncWhere<I, ID> le(String column, Object value) throws SQLException{
        mWhere.le(column, value);
        return this;
    }

    public DBAsyncWhere<I, ID> like(String column, Object value) throws SQLException{
        mWhere.like(column, value);
        return this;
    }

    public DBAsyncWhere<I, ID> or() throws SQLException{
        mWhere.or();
        return this;
    }

    public DBAsyncWhere<I, ID> and() throws SQLException{
        mWhere.and();
        return this;
    }

    public DBAsyncWhere<I, ID> between(String column, Object low, Object high) throws SQLException {
        mWhere.between(column, low, high);
        return this;
    }
}
