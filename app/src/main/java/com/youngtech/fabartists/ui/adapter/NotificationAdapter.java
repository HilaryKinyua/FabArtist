package com.youngtech.fabartists.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youngtech.fabartists.DTO.NotificationDTO;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.ProjectUtils;

import java.util.ArrayList;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<NotificationDTO> notificationDTOlist;


    public NotificationAdapter(Context mContext, ArrayList<NotificationDTO> notificationDTOlist) {
        this.mContext = mContext;
        this.notificationDTOlist = notificationDTOlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapternotification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tvTitle.setText(notificationDTOlist.get(position).getTitle());
        holder.tvMsg.setText(notificationDTOlist.get(position).getMsg());
        try {
            holder.tvDay.setText(ProjectUtils.getDisplayableDay(ProjectUtils.correctTimestamp(Long.parseLong(notificationDTOlist.get(position).getCreated_at()))));
            holder.tvDate.setText(ProjectUtils.convertTimestampToTime(ProjectUtils.correctTimestamp(Long.parseLong(notificationDTOlist.get(position).getCreated_at()))));
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {

        return notificationDTOlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold tvTitle;
        public CustomTextView tvDay, tvDate, tvMsg;

        public MyViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tvTitle);
            tvDay = view.findViewById(R.id.tvDay);
            tvDate = view.findViewById(R.id.tvDate);
            tvMsg = view.findViewById(R.id.tvMsg);

        }
    }

}