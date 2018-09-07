package you.chen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import you.chen.adapter.ListAdapter;

/**
 * Created by you on 2017/8/31.
 */

public class Activity2 extends AppCompatActivity {

    RecyclerView rv;

    public static void lanuch(Context context) {
        context.startActivity(new Intent(context, Activity2.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ListAdapter(this));

    }



}
