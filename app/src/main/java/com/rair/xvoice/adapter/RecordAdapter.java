package com.rair.xvoice.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rair.xvoice.R;
import com.rair.xvoice.bean.Record;

import java.util.List;

/**
 * @author Rair
 * @date 2018/4/20
 * <p>
 * desc:
 */
public class RecordAdapter extends BaseQuickAdapter<Record, BaseViewHolder> {

    public RecordAdapter(int layoutResId, @Nullable List<Record> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Record item) {
        helper.setText(R.id.tv_record, item.getContent());
        helper.addOnClickListener(R.id.iv_play);
        helper.addOnClickListener(R.id.iv_export);
    }
}
