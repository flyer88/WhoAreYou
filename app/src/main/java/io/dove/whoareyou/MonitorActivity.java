package io.dove.whoareyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MonitorActivity extends AppCompatActivity {


    DoveMonitor mDoveMonitor;
    List<TaskStack>  mTaskStacks = null;

    RecyclerView mContentRv;

    RvAdapter mRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        mContentRv = findViewById(R.id.content_rv);
        setAdapter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setAdapter();
    }

    public void setAdapter(){
        this.mDoveMonitor = Dove.getInstance(getApplication()).getMonitor();
        mTaskStacks = this.mDoveMonitor.getTaskStacks();
        if (mRvAdapter == null) {
            mRvAdapter = new RvAdapter(this, mTaskStacks);
            mContentRv.setLayoutManager(new LinearLayoutManager(this));
            mContentRv.setAdapter(mRvAdapter);
        } else {
            mRvAdapter.setTaskStacks(mTaskStacks);
        }
    }

}
