package com.youngtech.fabartists.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.youngtech.fabartists.DTO.WalletHistory;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.ui.fragment.Wallet;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;

import java.util.ArrayList;

/**
 * Created by VARUN on 01/01/19.
 */

public class AdapterWalletHistory extends RecyclerView.Adapter<AdapterWalletHistory.MyViewHolder> {

    private Wallet wallet;
    private Context mContext;
    private ArrayList<WalletHistory> walletHistoryList;


    public AdapterWalletHistory(Wallet wallet, ArrayList<WalletHistory> walletHistoryList) {
        this.wallet = wallet;
        mContext = wallet.getActivity();
        this.walletHistoryList = walletHistoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_wallet_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (walletHistoryList.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.tvAmount.setText("+" + walletHistoryList.get(position).getCurrency_type() + " " + walletHistoryList.get(position).getAmount());
            holder.tvAmount.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.ivImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.credit));
        } else {
            holder.tvAmount.setText("-" + walletHistoryList.get(position).getCurrency_type() + " " + walletHistoryList.get(position).getAmount());
            holder.tvAmount.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.ivImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.debit));
        }
        holder.tvDate.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(walletHistoryList.get(position).getCreated_at()))));
        holder.tvPaidReceive.setText(walletHistoryList.get(position).getDescription());

    }


    @Override
    public int getItemCount() {

        return walletHistoryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextViewBold tvAmount, tvPaidReceive;
        private CustomTextView tvDate;
        private ImageView ivImage;

        public MyViewHolder(View view) {
            super(view);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvPaidReceive = view.findViewById(R.id.tvPaidReceive);
            tvDate = view.findViewById(R.id.tvDate);
            ivImage = view.findViewById(R.id.ivImage);


        }
    }


}
