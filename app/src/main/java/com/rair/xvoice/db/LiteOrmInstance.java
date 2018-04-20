package com.rair.xvoice.db;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
/**
 * Created by Rair on 2017/7/25.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */

public class LiteOrmInstance {

    private static LiteOrm liteOrm;

    private LiteOrmInstance() {
    }

    public static void init(Context context) {
        DataBaseConfig config = new DataBaseConfig(context, "XVOICE");
        config.debugged = true;
        config.dbVersion = 1;
        config.onUpdateListener = null;
        if (liteOrm == null) {
            liteOrm = LiteOrm.newSingleInstance(config);
        }
    }

    public static LiteOrm getLiteOrm() {
        return liteOrm;
    }
}