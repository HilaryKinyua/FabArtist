package com.youngtech.fabartists.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youngtech.fabartists.DTO.GetCommentDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.ProjectUtils;

import java.util.ArrayList;

/**
 * Created by VARUN on 01/01/19.
 */

public class
AdapterViewComment extends BaseAdapter {
    private Context mContext;
    private ArrayList<GetCommentDTO> getCommentDTOList;
    private UserDTO userDTO;
    private ImageView ivImageD;
    private CustomTextView tvCloseD, tvNameD;
    private Dialog dialogImg;

    public AdapterViewComment(Context mContext, ArrayList<GetCommentDTO> getCommentDTOList, UserDTO userDTO) {
        this.mContext = mContext;
        this.getCommentDTOList = getCommentDTOList;
        this.userDTO = userDTO;

    }

    @Override
    public int getCount() {
        return getCommentDTOList.size();
    }

    @Override
    public Object getItem(int postion) {
        return getCommentDTOList.get(postion);
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        //ViewHolder holder = new ViewHolder();
        if (!getCommentDTOList.get(position).getSend_by().equalsIgnoreCase(userDTO.getUser_id())) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_view_comment, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_view_comment_my, parent, false);

        }

        CustomTextView textViewMessage = (CustomTextView) view.findViewById(R.id.textViewMessage);
        CustomTextView textViewTime = (CustomTextView) view.findViewById(R.id.textViewTime);
        CustomTextView tvName = (CustomTextView) view.findViewById(R.id.tvName);
        ImageView ivView = (ImageView) view.findViewById(R.id.ivView);

        if (getCommentDTOList.get(position).getChat_type().equalsIgnoreCase("2")) {
            ivView.setVisibility(View.VISIBLE);
        } else {
            ivView.setVisibility(View.GONE);
        }

        Glide.with(mContext).
                load(getCommentDTOList.get(position).getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivView);

        textViewMessage.setText(getCommentDTOList.get(position).getMessage());
        tvName.setText(getCommentDTOList.get(position).getSender_name());

        ivView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogshare(position);
            }
        });
        try {

            textViewTime.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(getCommentDTOList.get(position).getDate()))));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    public void dialogshare(int pos) {
        dialogImg = new Dialog(mContext, android.R.style.Theme_Dialog);
        dialogImg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogImg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogImg.setContentView(R.layout.dailog_image_view);


        tvCloseD = (CustomTextView) dialogImg.findViewById(R.id.tvCloseD);
        tvNameD = (CustomTextView) dialogImg.findViewById(R.id.tvNameD);

        ivImageD = (ImageView) dialogImg.findViewById(R.id.ivImageD);
        dialogImg.show();
        dialogImg.setCancelable(false);

        Glide.with(mContext).
                load(getCommentDTOList.get(pos).getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImageD);

        tvNameD.setText(getCommentDTOList.get(pos).getSender_name());
        tvCloseD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImg.dismiss();

            }
        });

    }

}
