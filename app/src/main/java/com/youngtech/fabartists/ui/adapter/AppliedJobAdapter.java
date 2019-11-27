package com.youngtech.fabartists.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youngtech.fabartists.DTO.AppliedJobDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.ui.fragment.AppliedJobsFrag;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class AppliedJobAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AppliedJobAdapter.class.getSimpleName();
    private HashMap<String, String> params;
    private HashMap<String, String> paramsStart;
    private DialogInterface dialog_book;
    private AppliedJobsFrag appliedJobsFrag;
    private ArrayList<AppliedJobDTO> objects = null;
    private ArrayList<AppliedJobDTO> appliedJobDTOSList;
    private UserDTO userDTO;
    private Context mContext;
    private LayoutInflater inflater;
    SimpleDateFormat sdf1, timeZone;
    private Date date;
    int CALL_PERMISSION = 101;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    public AppliedJobAdapter(AppliedJobsFrag appliedJobsFrag, ArrayList<AppliedJobDTO> objects, UserDTO userDTO, LayoutInflater inflater) {
        this.appliedJobsFrag = appliedJobsFrag;
        this.objects = objects;
        this.appliedJobDTOSList = new ArrayList<AppliedJobDTO>();
        this.appliedJobDTOSList.addAll(objects);
        this.userDTO = userDTO;
        this.mContext = appliedJobsFrag.getActivity();
        this.inflater = inflater;
        sdf1 = new SimpleDateFormat(Consts.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Consts.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);

        date = new Date();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_applied_job, parent, false);
            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            vh = new MyViewHolderSection(v);
        }
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, final int position) {
        if (holderMain instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holderMain;

            holder.tvDate.setText(mContext.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getJob_date()) + " " + objects.get(position).getTime());
            holder.tvJobId.setText(objects.get(position).getJob_id());
            holder.tvName.setText(objects.get(position).getUser_name());
            holder.tvDescription.setText(objects.get(position).getDescription());
            holder.tvAddress.setText(objects.get(position).getUser_address());
            holder.tvEmail.setText(objects.get(position).getUser_email());
            holder.tvMobile.setText(objects.get(position).getUser_mobile());

            holder.tvCategory.setText(objects.get(position).getCategory_name());
            holder.tvTitle.setText(mContext.getResources().getString(R.string.job_title) + " " + objects.get(position).getTitle());
            holder.tvPrice.setText(objects.get(position).getCurrency_symbol() + objects.get(position).getPrice());

            holder.tvMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ProjectUtils.hasPermissionInManifest(mContext, CALL_PERMISSION, Manifest.permission.CALL_PHONE)) {
                        if (objects.get(position).getUser_mobile().equalsIgnoreCase("")) {
                            ProjectUtils.showToast(mContext, "Mobile number not available");
                        } else {

                            try {

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + objects.get(position).getUser_mobile()));
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                appliedJobsFrag.startActivity(callIntent);

                            } catch (Exception e) {
                                Log.e("Exception", "" + e);
                            }
                        }
                    }
                }
            });
            Glide.with(mContext).
                    load(objects.get(position).getUser_image())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProfile);

            if (objects.get(position).getStatus().equalsIgnoreCase("0")) {
                holder.llDecline.setVisibility(View.VISIBLE);
                holder.llStart.setVisibility(View.GONE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.pending));
                holder.llInpro.setVisibility(View.GONE);
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_red));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("1")) {
                holder.llDecline.setVisibility(View.GONE);
                holder.llStart.setVisibility(View.VISIBLE);
                holder.llInpro.setVisibility(View.GONE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.confirm));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("2")) {
                holder.llDecline.setVisibility(View.GONE);
                holder.llStart.setVisibility(View.GONE);
                holder.llInpro.setVisibility(View.GONE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.com));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("3")) {
                holder.llDecline.setVisibility(View.GONE);
                holder.llStart.setVisibility(View.GONE);
                holder.llInpro.setVisibility(View.GONE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.rej));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("5")) {
                holder.llDecline.setVisibility(View.GONE);
                holder.llStart.setVisibility(View.GONE);
                holder.llInpro.setVisibility(View.VISIBLE);
                holder.tvStatus.setText(mContext.getResources().getString(R.string.inprogres));
                holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
            }


            holder.llDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    params = new HashMap<>();
                    params.put(Consts.AJ_ID, objects.get(position).getAj_id());
                    params.put(Consts.STATUS, "3");
                    rejectDialog(mContext.getResources().getString(R.string.reject), mContext.getResources().getString(R.string.reject_msg));
                }
            });

            holder.llStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paramsStart = new HashMap<>();
                    paramsStart.put(Consts.USER_ID, objects.get(position).getUser_id());
                    paramsStart.put(Consts.ARTIST_ID, objects.get(position).getArtist_id());
                    paramsStart.put(Consts.DATE_STRING, sdf1.format(date).toString().toUpperCase());
                    paramsStart.put(Consts.TIMEZONE, timeZone.format(date));
                    paramsStart.put(Consts.PRICE, objects.get(position).getPrice());
                    paramsStart.put(Consts.JOB_ID, objects.get(position).getJob_id());
                    startDialog(mContext.getResources().getString(R.string.start), mContext.getResources().getString(R.string.start_app));
                }
            });

        } else {
            MyViewHolderSection view = (MyViewHolderSection) holderMain;
            view.tvSection.setText(objects.get(position).getSection_name());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return this.objects.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextView tvDate, tvStatus, tvName, tvDescription, tvMobile, tvEmail, tvAddress, tvCategory, tvTitle;
        public CircleImageView ivProfile;
        public LinearLayout llStatus, llDecline, llStart, llInpro;
        public CustomTextViewBold tvPrice, tvJobId;

        public MyViewHolder(View view) {
            super(view);
            llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            llStart = view.findViewById(R.id.llStart);
            tvDate = view.findViewById(R.id.tvDate);
            tvJobId = view.findViewById(R.id.tvJobId);
            tvName = view.findViewById(R.id.tvName);
            tvDescription = view.findViewById(R.id.tvDescription);
            ivProfile = view.findViewById(R.id.ivProfile);
            llDecline = view.findViewById(R.id.llDecline);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvMobile = view.findViewById(R.id.tvMobile);
            llInpro = view.findViewById(R.id.llInpro);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvTitle = view.findViewById(R.id.tvTitle);
        }
    }

    public void reject() {

        new HttpsRequest(Consts.JOB_STATUS_ARTIST_API, params, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    appliedJobsFrag.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void startJob() {
        new HttpsRequest(Consts.START_JOB_API, paramsStart, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    appliedJobsFrag.gotos();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void rejectDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            reject();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            startJob();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(appliedJobDTOSList);
        } else {
            for (AppliedJobDTO appliedJobDTO : appliedJobDTOSList) {
                if (appliedJobDTO.getUser_name().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(appliedJobDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        public CustomTextView tvSection;

        public MyViewHolderSection(View view) {
            super(view);
            tvSection = view.findViewById(R.id.tvSection);
        }
    }


}