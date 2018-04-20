package com.rair.xvoice;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rair.xvoice.adapter.RecordAdapter;
import com.rair.xvoice.bean.Record;
import com.rair.xvoice.db.LiteOrmInstance;
import com.yzy.voice.VoiceBuilder;
import com.yzy.voice.VoicePlay;
import com.yzy.voice.VoiceTextTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.ll_play)
    LinearLayout llPlay;
    @BindView(R.id.switch_mode)
    Switch switchMode;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.rv_record_list)
    RecyclerView rvRecordList;
    private boolean isNumber;
    private ArrayList<Record> datas;
    private RecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        datas = new ArrayList<>();
        datas.addAll(LiteOrmInstance.getLiteOrm().query(Record.class));
        adapter = new RecordAdapter(R.layout.layout_record_item, datas);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);

        rvRecordList.setLayoutManager(new LinearLayoutManager(this));
        adapter.bindToRecyclerView(rvRecordList);
        adapter.setEmptyView(R.layout.layout_empty);
        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> isNumber = isChecked);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        Record record = datas.get(position);
        VoicePlay.with(this).play(record.getAmount(), isNumber);
    }

    @OnClick({R.id.ll_play, R.id.iv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_play:
                String amount = etAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    Toasty.info(this, "请输入金额").show();
                    return;
                }
                VoicePlay.with(this).play(amount, isNumber);
                String content = getContentString(amount);
                Record record = new Record();
                record.setAmount(amount);
                record.setContent(content);
                LiteOrmInstance.getLiteOrm().insert(record);
                datas.add(record);
                adapter.notifyDataSetChanged();
                etAmount.setText(null);
                break;
            case R.id.iv_clear:
                LiteOrmInstance.getLiteOrm().delete(Record.class);
                datas.clear();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private String getContentString(String amount) {
        VoiceBuilder voiceBuilder = new VoiceBuilder.Builder()
                .start("success")
                .money(amount)
                .unit("yuan")
                .checkNum(isNumber)
                .builder();
        StringBuilder text = new StringBuilder()
                .append("输入金额: ").append(amount)
                .append("\n");
        if (isNumber) {
            text.append("全数字式: ").append(VoiceTextTemplate.genVoiceList(voiceBuilder).toString());
        } else {
            text.append("中文样式: ").append(VoiceTextTemplate.genVoiceList(voiceBuilder).toString());
        }
        return text.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AlertDialog.Builder(this).setTitle("关于")
                .setMessage("在抖音上看到很多支付宝到账语音播报的视频，感觉此乃装逼神器，就有了这个应用." +
                        "\n\n作者：Rair\n微博：@Rairmmd\nGithub:Rairmmd\n同时感谢[YzyCoding]的开源实现.")
                .setPositiveButton("Rair的小群", (dialog, which) -> joinQQGroup("wSSf3_kFHNTnFd7iT498eRPDcQ4HsVwd"))
                .setNegativeButton("知道了", null).show();
        return super.onOptionsItemSelected(item);
    }

    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private long exitTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toasty.info(this, "再按一次退出").show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
