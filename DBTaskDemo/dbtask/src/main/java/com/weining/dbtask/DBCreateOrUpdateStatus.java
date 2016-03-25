package com.weining.dbtask;

import com.j256.ormlite.dao.Dao;

/**
 * 对Ormlite的CreateOrUpdateStatus封装
 * Created by xueweining on 2015/4/7.
 */
public class DBCreateOrUpdateStatus {
    private boolean created;
    private boolean updated;
    private int numLinesChanged;
    public DBCreateOrUpdateStatus(Dao.CreateOrUpdateStatus createOrUpdateStatus) {
        this.created = createOrUpdateStatus.isCreated();
        this.updated = createOrUpdateStatus.isUpdated();
        this.numLinesChanged = createOrUpdateStatus.getNumLinesChanged();
    }
    public boolean isCreated() {
        return created;
    }
    public boolean isUpdated() {
        return updated;
    }
    public int getNumLinesChanged() {
        return numLinesChanged;
    }
}
