package com.weining.dbtask;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicInteger;

import com.weining.asynctask.CommonUniqueId;
import com.weining.asynctask.TaskParallel;
import com.weining.asynctask.TaskPriority;

/**
 * DBOp的发起者
 * Created by xueweining on 2015/3/30.
 */
public class DBTaskManager {
    private static DBTaskManager sInstance = null;
    private TaskParallel mTaskParallelA = null;
    private TaskParallel mTaskParallelB = null;
    private Handler mHandler = null;
    private AtomicInteger mHandlerMessageCount = null;
    private final int HANDLER_ID_READ_LIMIT = 1;

    public static DBTaskManager getInstance() {
        if (null == sInstance) {
            synchronized (DBTaskManager.class) {
                if (null == sInstance) {
                    sInstance = new DBTaskManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 同步执行DBOp
     * @param dbAtom
     * @return
     */
    public DBOp execute(DBOp dbAtom) {
        new DBOpWorker(dbAtom).execute();
        return dbAtom;
    }

    /**
     * 异步执行DBOp
     * @param operate
     * @return
     */
    public boolean executeAsync(DBOp operate) {
        if (operate == null) {
            return false;
        }
        if (Looper.myLooper() != null && Looper.getMainLooper() == Looper.myLooper()) {
            doAsyncExecute(operate);
        } else {
            mHandlerMessageCount.incrementAndGet();
            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_ID_READ_LIMIT, operate));
        }
        return true;
    }

    private DBTaskManager() {
        mTaskParallelA = new TaskParallel(TaskParallel.ParallelType.SERIAL, CommonUniqueId.gen());
        mTaskParallelB = new TaskParallel(TaskParallel.ParallelType.SERIAL, CommonUniqueId.gen());
        mHandlerMessageCount = new AtomicInteger(0);
        mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HANDLER_ID_READ_LIMIT:
                        mHandlerMessageCount.decrementAndGet();
                        if (msg.obj != null && msg.obj instanceof DBOp) {
                            doAsyncExecute((DBOp) (msg.obj));
                        }
                        break;
                    default:
                        break;
                }
            };
        };
    }

    private boolean doAsyncExecute(DBOp mOp) {
        DBOpTask task = new DBOpTask(mOp);
        if (Math.abs(mOp.getDao().getClass().getName().hashCode() % 2) == 0) {
            task.setParallel(mTaskParallelA);
        } else {
            task.setParallel(mTaskParallelB);
        }
        task.setPriority(TaskPriority.SUPER_HIGH);
        task.execute();
        return true;
    }
}
