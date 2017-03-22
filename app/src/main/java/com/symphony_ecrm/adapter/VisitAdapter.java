package com.symphony_ecrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.symphony_ecrm.R;
import com.symphony_ecrm.model.CRMModel;

import java.util.ArrayList;

/**
 * Created by Ankit on 6/20/2016.
 */
public class VisitAdapter extends BaseAdapter {
    private ArrayList<CRMModel> visitList;
    private Context context;


    public VisitAdapter(ArrayList<CRMModel> visitList, Context context) {
        this.visitList = visitList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return visitList.size();
    }

    @Override
    public Object getItem(int i) {
        return visitList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Holder holder;
        if (view == null) {
            holder = new Holder();
            final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_visit_reportlist, null);
            holder.tvCompanyName = (TextView) view.findViewById(R.id.row_visi_reportlist_tvCompany);
            holder.tvStatus = (TextView) view.findViewById(R.id.row_visi_reportlist_tvStatus);
            holder.tvCheckInLatLng = (TextView) view.findViewById(R.id.row_visi_reportlist_tvCheckInLatLng);
            holder.tvCheckInTieStamp = (TextView) view.findViewById(R.id.row_visi_reportlist_tvCheckInTimeStamp);
            holder.tvCheckoutLatLng = (TextView) view.findViewById(R.id.row_visi_reportlist_tvCheckoutLatLng);
            holder.tvCheckoutTimeStamp = (TextView) view.findViewById(R.id.row_visi_reportlist_tvCheckOutTimeStamp);
            holder.view = (View) view.findViewById(R.id.row_visi_reportlist_checkStatusBar);
            // holder.textViewAddress = (TextView) view.findViewById(R.id.row_customer_list_tv_address);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.tvCompanyName.setText(visitList.get(i).getCompanyname());


        if (visitList.get(i).getCheckStatus() == 1 && visitList.get(i).getCheckFlag() == 1) {

            holder.tvStatus.setText("Success");
            holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (visitList.get(i).getCheckStatus() == 0 && visitList.get(i).getIsSendtoServer() == 1) {


            holder.tvStatus.setText("Sync Pending");
            holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else if (visitList.get(i).getIsSendtoServer() == 0) {

            holder.tvStatus.setText("Not Sync to Server");
            holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        holder.tvCheckInLatLng.setText(visitList.get(i).getCheckInLat() + "," + visitList.get(i).getCheckInLong());
        holder.tvCheckoutLatLng.setText(visitList.get(i).getCheckOutLat() + "," + visitList.get(i).getCheckOutLong());
        holder.tvCheckInTieStamp.setText(visitList.get(i).getCheckInTimeStemp());
        holder.tvCheckoutTimeStamp.setText(visitList.get(i).getCheckOutTimeStemp());
        return view;
    }

    private class Holder {
        private TextView tvCompanyName;
        private TextView tvStatus;
        private TextView tvCheckInLatLng;
        private TextView tvCheckInTieStamp;
        private TextView tvCheckoutLatLng;
        private TextView tvCheckoutTimeStamp;
        private View view;
    }
}
