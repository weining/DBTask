package com.weining.dbtaskdemo;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by weining on 16/3/25.
 */
public class User {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String userName;

    @DatabaseField
    public String avatar;

}
