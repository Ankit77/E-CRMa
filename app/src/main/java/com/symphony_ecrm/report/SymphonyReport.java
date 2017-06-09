package com.symphony_ecrm.report;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.symphony_ecrm.R;
import com.symphony_ecrm.pager.TabsPagerAdapter;

public class SymphonyReport extends AppCompatActivity {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_home);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);


//		mAdapter.addPage(new CheckStatusReport());
//		mAdapter.addPage(new DistributorReport());
        mAdapter.addPage(new NotificationReport());
        mAdapter.addPage(new VisitReport());
        mAdapter.addPage(new ReportFragment());

        viewPager.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setIcon(android.R.color.transparent);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("e-CRM Report");
        // SymphonyUtils.startWipeDataAlram(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
