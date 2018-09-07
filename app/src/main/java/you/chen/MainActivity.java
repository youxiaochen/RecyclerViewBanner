package you.chen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import you.chen.adapter.TestAdapter;
import you.chen.banner.BannerIndicator;
import you.chen.banner.BannerLayoutManager;
import you.chen.banner.BannerPager;
import you.chen.bean.BannerBean;

public class MainActivity extends Activity implements View.OnClickListener {

    BannerPager bp;
    BannerIndicator bi;
    TestAdapter adapter;

    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bp = (BannerPager) findViewById(R.id.bp);
        bi = (BannerIndicator) findViewById(R.id.bi);
        et = (EditText) findViewById(R.id.et);


        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
        findViewById(R.id.bt5).setOnClickListener(this);

        bp.setLayoutManager(new BannerLayoutManager(this));
        adapter = new TestAdapter(this);
        bp.setBannerAdapter(adapter);
        bp.addOnPageChangeListener(new BannerPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                bi.setCurrentIndicator(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:
                adapter.setNewDatas(BannerBean.test1());
                bi.setIndicatorCount(adapter.getItemCount());
                bp.notifyDataSetChanged();
                break;
            case R.id.bt2:
                adapter.setNewDatas(BannerBean.test2());
                bi.setIndicatorCount(adapter.getItemCount());
                bp.notifyDataSetChanged();
                break;
            case R.id.bt3:
                adapter.setNewDatas(null);
                bi.setIndicatorCount(adapter.getItemCount());
                bp.notifyDataSetChanged();
                break;
            case R.id.bt4:
                bp.setCurrentItem(position(et.getText().toString().trim()));
                break;
            case R.id.bt5:
                Activity2.lanuch(this);
                break;
        }
    }

    private int position(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return -1;
        }
    }


}
