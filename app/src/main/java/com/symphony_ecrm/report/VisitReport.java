package com.symphony_ecrm.report;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.symphony_ecrm.E_CRM;
import com.symphony_ecrm.R;
import com.symphony_ecrm.adapter.VisitAdapter;
import com.symphony_ecrm.model.CRMModel;
import com.symphony_ecrm.pager.FragmentTitle;

import java.util.ArrayList;

/**
 * Created by Ankit on 6/20/2016.
 */
public class VisitReport extends Fragment implements FragmentTitle {
    private View view;
    private ArrayList<CRMModel> visitList;
    private ListView lvVisit;
    private E_CRM e_sampark;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        e_sampark = (E_CRM) getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.visit_reportlist, null);
        lvVisit = (ListView) view.findViewById(R.id.visit_reportlist_lvVisit);
        tvEmpty = (TextView) view.findViewById(R.id.visit_reportlist_tvEmpty);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("REFRESH"));
        loaddata();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void loaddata() {

        visitList = e_sampark.getSymphonyDB().getVisitList();
        if (visitList != null && visitList.size() > 0) {
            VisitAdapter visitAdapter = new VisitAdapter(visitList, getActivity());
            lvVisit.setAdapter(visitAdapter);
            lvVisit.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            lvVisit.setDividerHeight(0);
            lvVisit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), VisitDetailActivity.class);
                    intent.putExtra("CRMID", visitList.get(i).getCrmId());
                    if (visitList.get(i).getIsSendtoServer() == 1) {
                        intent.putExtra("ISVIEWONLY", true);
                    } else {
                        intent.putExtra("ISVIEWONLY", false);
                    }
                    getActivity().startActivity(intent);
                }
            });
        } else {
            lvVisit.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getTitle() {
        return "CRM VISIT";
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("REFRESH")) {
                loaddata();
            }
        }
    };
}
