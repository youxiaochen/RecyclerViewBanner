package you.chen.bannertest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import chen.you.banner.BannerPager;
import chen.you.banner.IndicatorView;
import chen.you.banner.transformer.ZoomOutTransformer;
import you.chen.bannertest.bean.BannerBean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    BannerPager bp0;
    IndicatorView iv0;

    BannerPager bp1;
    IndicatorView iv1;
    TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bp0 = findViewById(R.id.bp0);
        iv0 = findViewById(R.id.iv0);
        bp0.setLifecycleOwner(this);
        bp0.setupWithIndicator(iv0);

        bp1 = findViewById(R.id.bp1);
        iv1 = findViewById(R.id.iv1);
        bp1.setLifecycleOwner(this);
        bp1.setupWithIndicator(iv1);
        bp1.setPageTransformer(new ZoomOutTransformer());

        bp0.setAdapter(new TestAdapter(this, BannerBean.test2()));

        adapter = new TestAdapter(this);
        bp1.setAdapter(adapter);

        findViewById(R.id.bt1).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        findViewById(R.id.bt3).setOnClickListener(this);
        findViewById(R.id.bt4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt1:
                adapter.setNewData(BannerBean.test2());
                break;
            case R.id.bt2:
                adapter.setNewData(BannerBean.test1());
                break;
            case R.id.bt3:
                adapter.clear();
                break;
            case R.id.bt4:
                startActivity(new Intent(this, TwoActivity.class));
                break;
        }
    }
}
