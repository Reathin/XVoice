package com.rair.xvoice.base;

import android.app.Application;
import android.graphics.Color;

import com.rair.xvoice.db.LiteOrmInstance;

import es.dmoral.toasty.Toasty;

/**
 * @author Rair
 * @date 2018/4/20
 * <p>
 * desc:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LiteOrmInstance.init(this);
        Toasty.Config.getInstance()
                .setInfoColor(Color.parseColor("#fe7098"))
                .apply();
    }
}
