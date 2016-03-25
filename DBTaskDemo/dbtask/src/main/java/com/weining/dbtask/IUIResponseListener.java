package com.weining.dbtask;

/**
 * Ui层的回调，临时接口
 * Created by xueweining on 2015/4/9.
 */
public interface IUIResponseListener<TResult> {
    public void onResponse(boolean isSuccessful, TResult result);
}
