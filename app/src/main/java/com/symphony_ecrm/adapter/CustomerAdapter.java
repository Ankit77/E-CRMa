package com.symphony_ecrm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.symphony_ecrm.R;
import com.symphony_ecrm.model.CustomerListModel;
import com.symphony_ecrm.register.SettingFragment;

import java.util.ArrayList;

/**
 * Created by user on 26-Jun-16.
 */
public class CustomerAdapter extends BaseAdapter {
    private ArrayList<CustomerListModel> cusList;
    private Context context;
    private myClickListner mmyClickListner;

    public void setMmyClickListner(myClickListner mmyClickListner) {
        this.mmyClickListner = mmyClickListner;
    }

    public CustomerAdapter(ArrayList<CustomerListModel> cusList, Context context) {
        this.cusList = cusList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cusList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            holder = new Holder();
            final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_fragment_customerlist, null);
            holder.tvCustomerName = (TextView) view.findViewById(R.id.row_fragment_customerlist_tvCusName);
            holder.rlMain = (RelativeLayout) view.findViewById(R.id.row_fragment_customerlist_rlmain);
            holder.tvAddress = (TextView) view.findViewById(R.id.row_fragment_customerlist_tvCustaddress);
            holder.tvContact = (TextView) view.findViewById(R.id.row_fragment_customerlist_tvcontact);
            holder.tvTown = (TextView) view.findViewById(R.id.row_fragment_customerlist_tvCustown);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.tvCustomerName.setText(cusList.get(i).getCustomername());
        holder.tvAddress.setText(cusList.get(i).getAddress());
        holder.tvContact.setText(cusList.get(i).getContact());
        holder.tvTown.setText(cusList.get(i).getTown());

        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mmyClickListner != null) {
                    mmyClickListner.onClick(i);
                }
            }
        });
        return view;
    }

    private class Holder {
        private TextView tvCustomerName;
        private TextView tvAddress;
        private TextView tvContact;
        private TextView tvTown;
        private RelativeLayout rlMain;
    }

    public interface myClickListner {
        public void onClick(int position);
    }
}
