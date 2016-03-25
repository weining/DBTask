package com.weining.dbtask;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import android.util.Pair;

/**
 * 数据库操作单位封装
 * Created by xueweining on 2015/3/31.
 */
public class DBOp<I, ID> {
    private static final String TAG = "DBOp";

    public static enum AtomAction {
        //根据主键id来Query
        QUERY_FORID,
        //根据某列的相等值来Query，返回list
        QUERY_FOREQ,
        //根据QueryBuilder来Query，返回list
        QUERY_BUILDER_LIST,
        //根据QueryBuilder来Query，返回第一个
        QUERY_BUILDER_FORFIRST,
        //插入model，如果主键一致则更新
        CREATE_OR_UPDATE,
        //根据主键id删除
        DELETE_FORID,
        //根据DeleteBuilder删除
        DELETE_BUILDER,
        //自定义操作
        CUSTOM,
        //自定义批量操作
        CUSTOM_BATCH,
        //返回countof
        QUERY_COUNT_OF
    }

    protected AtomAction mAction = AtomAction.QUERY_FORID;
    private Dao<I, ID> mDao;

    //request params
    private QueryBuilder<I, ID> mQueryBuilder;
    private DeleteBuilder<I, ID> mDeleteBuilder;
    private ID mTargetId;
    private I mCreateOrUpdateModel;
    private ICustomDBOp mCustomOp;
    private DBOp mNextDBOp;
    private Pair<String, Object> mEqPair;
    private boolean mContinueIfFailed = true;
    private boolean mIsAsync = true;

    private Object mResult;
    private IOnPostExecute postExecute;
    private IAfterDoingBackground mAfterDoingBackground;

    ICustomDBOp getCustomOp() {
        return mCustomOp;
    }

    /**
     * 自定义操作
     * @param mCustomOp
     * @return
     */
    DBOp op(ICustomDBOp mCustomOp) {
        this.mCustomOp = mCustomOp;
        return this;
    }

    IAfterDoingBackground getAfterDoingBackground() {
        return mAfterDoingBackground;
    }

    /**
     * 在DB操作之后进行，与DB操作在同一个Thread
     * @param afterDoingBackground
     * @return
     */
    DBOp after(IAfterDoingBackground afterDoingBackground) {
        this.mAfterDoingBackground = afterDoingBackground;
        return this;
    }

    /**
     * DB操作结束后，回调回主线程执行
     * @param postExecute
     * @return
     */
    DBOp<I, ID> postBack(IOnPostExecute postExecute) {
        this.postExecute = postExecute;
        return this;
    }

    Pair<String, Object> getEqPair() {
        return mEqPair;
    }

    DBOp eqPair(Pair<String, Object> mEqPair) {
        this.mEqPair = mEqPair;
        return this;
    }

    private volatile boolean mIsSuccess = false;

    Dao<I, ID> getDao() {
        return mDao;
    }

    DeleteBuilder<I, ID> getDeleteBuilder() {
        return mDeleteBuilder;
    }

    DBOp deleteBuilder(DeleteBuilder mDeleteBuilder) {
        this.mDeleteBuilder = mDeleteBuilder;
        return this;
    }

    I getCreateOrUpdateModel() {
        return mCreateOrUpdateModel;
    }

    DBOp createOrUpdateModel(I mCreateOrUpdateModel) {
        this.mCreateOrUpdateModel = mCreateOrUpdateModel;
        return this;
    }

    public Object getResult() {
        return mResult;
    }

    public void setResult(Object mResult) {
        this.mResult = mResult;
    }

    QueryBuilder<I, ID> getQueryBuilder() {
        return mQueryBuilder;
    }

    DBOp queryBuilder(QueryBuilder<I, ID> mBuilder) {
        this.mQueryBuilder = mBuilder;
        return this;
    }

    ID getTargetId() {
        return mTargetId;
    }

    DBOp targetId(ID mQueryId) {
        this.mTargetId = mQueryId;
        return this;
    }

    DBOp(Dao<I, ID> dao, AtomAction dbAtomAction) {
        mDao = dao;
        mAction = dbAtomAction;
    }

    boolean hasNext() {
        return !(mNextDBOp == null);
    }

    DBOp getNext() {
        return mNextDBOp;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return mIsSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
    }

    /**
     * @return
     */
    AtomAction getAction() {
        return mAction;
    }

    void onResponse() {
        if (postExecute != null) {
            postExecute.onPostExecute(isSuccess(), getResult());
        }
        if (mIsAsync && mNextDBOp != null && (mContinueIfFailed || isSuccess())) {
            mNextDBOp.execute();
        }
    }

    /**
     * 设定下一步DBOp，串行执行
     * @param nextDBOp
     * @param continueIfFailed 如果当前操作不成功，是否继续执行下一步DBOp
     * @return 当前DBOp
     */
    DBOp next(DBOp nextDBOp, boolean continueIfFailed) {
        mNextDBOp = nextDBOp;
        mContinueIfFailed = continueIfFailed;
        return this;
    }

    /**
     * 同步执行DBOp，结果直接返回
     * @return
     */
    DBOp executeSync() {
        mIsAsync = false;
        return DBTaskManager.getInstance().execute(this);
    }

    /**
     * 异步执行DBOp，结果通过IOnPostExecute返回主线程
     * @return
     */
    boolean execute() {
        mIsAsync = true;
        return DBTaskManager.getInstance().executeAsync(this);
    }

    public interface IAfterDoingBackground<TypeResult> {
        /**
         * 在DB操作之后运行，与DB操作在同一后台线程
         * @param isSuccessFul DB操作执行是否成功
         * @param result DB操作返回结果
         * @param op DB运行单位，若在本函数中对返回结果有修改，可以执行op.setSuccess和op.setResult，结果会返回到onPostExecute
         */
        public void onAfterDoingBackground(boolean isSuccessFul, TypeResult result, DBOp op);
    }

    public interface IOnPostExecute<TypeResult> {
        /**
         * 在DB操作之后运行，运行在主线程
         * @param isSuccessFul DB操作是否成功（在afterDoingBackground之后）
         * @param result DB操作返回结果
         */
        public void onPostExecute(boolean isSuccessFul, TypeResult result);
    }

    public interface ICustomDBOp {
        /**
         * 自定义操作
         * @param op DB操作单元，在本函数中通过执行op.setResult，来控制返回结果
         * @return DB操作是否成功
         */
        public boolean customDBOp(DBOp op);
    }

    static DBOp createDBOpQueryForEq(Dao mDao, String column, Object value, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, DBOp.AtomAction.QUERY_FOREQ).eqPair(new Pair<String, Object>(column, value)).after(afterDoingBackground).postBack(resultCallback);
    }

    static <TargetId> DBOp createDBOpQueryForId(Dao mDao, TargetId id, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.QUERY_FORID).targetId(id).after(afterDoingBackground).postBack(resultCallback);
    }

    static DBOp createDBOpQueryBuilderList(Dao mDao, DBAsyncQueryBuilder mQueryBuilder, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.QUERY_BUILDER_LIST).queryBuilder(mQueryBuilder.getOrmBuilder()).after(afterDoingBackground).postBack(resultCallback);
    }

    static DBOp createDBOpQueryCountOf(Dao mDao, DBAsyncQueryBuilder mQueryBuilder, IAfterDoingBackground
            afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.QUERY_COUNT_OF).queryBuilder(mQueryBuilder.getOrmBuilder()).after(afterDoingBackground)
                .postBack(resultCallback);
    }

    static DBOp createDBOpQueryBuilderForFirst(Dao mDao, DBAsyncQueryBuilder mQueryBuilder, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.QUERY_BUILDER_FORFIRST).queryBuilder(mQueryBuilder.getOrmBuilder()).after(afterDoingBackground).postBack(resultCallback);
    }

    static <TModel> DBOp createDBOpCreateOrUpdate(Dao mDao, TModel model, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.CREATE_OR_UPDATE).createOrUpdateModel(model).after(afterDoingBackground).postBack(resultCallback);
    }

    static <TargetId> DBOp createDBOpDeleteForId(Dao mDao, TargetId id, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.DELETE_FORID).targetId(id).after(afterDoingBackground).postBack(resultCallback);
    }

    static DBOp createDBOpCustom(Dao mDao, ICustomDBOp customDBOp, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.CUSTOM).op(customDBOp).after(afterDoingBackground).postBack(resultCallback);
    }

    static DBOp createDBOpCustomBatch(Dao mDao, ICustomDBOp customDBOp, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp(mDao, AtomAction.CUSTOM_BATCH).op(customDBOp).after(afterDoingBackground).postBack(resultCallback);
    }

    static DBOp createDBOpDeleteBuilder(Dao mDao, DBAsyncDeleteBuilder mDeleteBuilder, IAfterDoingBackground afterDoingBackground, IOnPostExecute resultCallback) {
        return new DBOp<>(mDao, DBOp.AtomAction.DELETE_BUILDER).deleteBuilder(mDeleteBuilder.getOrmBuilder()).after(afterDoingBackground).postBack(resultCallback);
    }
}
