package com.youngtech.fabartists.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youngtech.fabartists.DTO.AllJobsDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.fragment.AllJobsFrag;
import com.youngtech.fabartists.utils.CustomEditText;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class AllJobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AllJobsAdapter.class.getSimpleName();
    private HashMap<String, String> params = new HashMap<>();
    private Dialog dialogApplyJob;
    private Context mContext;
    private AllJobsFrag allJobsFrag;
    private ArrayList<AllJobsDTO> objects = null;
    private ArrayList<AllJobsDTO> allJobsDTOList;
    private UserDTO userDTO;
    private LayoutInflater inflater;
    private CustomEditText etAboutD, etPriceD;
    private CustomTextViewBold tvYesAbout, tvNoAbout;
    private SharedPrefrence prefrence;

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    public AllJobsAdapter(AllJobsFrag allJobsFrag, ArrayList<AllJobsDTO> objects, UserDTO userDTO, LayoutInflater inflater) {
        this.allJobsFrag = allJobsFrag;
        this.mContext = allJobsFrag.getActivity();
        this.objects = objects;
        this.allJobsDTOList = new ArrayList<AllJobsDTO>();
        this.allJobsDTOList.addAll(objects);
        this.userDTO = userDTO;
        this.inflater = inflater;
        prefrence = SharedPrefrence.getInstance(mContext);


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_all_jobs, parent, false);
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
            //0 = pending, 1 = confirm , 2 = complete, 3 =reject

            holder.tvJobId.setText(objects.get(position).getJob_id());
            holder.tvTitle.setText(mContext.getResources().getString(R.string.job_title) + " " + objects.get(position).getTitle());
            holder.tvDescription.setText(objects.get(position).getDescription());
            holder.tvCategory.setText(objects.get(position).getCategory_name());
            holder.tvAddress.setText(objects.get(position).getAddress());
            holder.tvName.setText(objects.get(position).getUser_name());
            holder.tvPrice.setText(objects.get(position).getCurrency_symbol() + objects.get(position).getPrice());
            holder.tvDate.setText(mContext.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getJob_date())+" "+objects.get(position).getTime());


            Glide.with(mContext).
                    load(objects.get(position).getAvtar())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivImage);
            holder.llApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    params.put(Consts.USER_ID, objects.get(position).getUser_id());
                    params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                    params.put(Consts.ARTIST_ID, userDTO.getUser_id());
                    dialogAbout();
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

        public CustomTextViewBold tvJobId, tvPrice;
        public CustomTextView tvDate, tvTitle, tvDescription, tvCategory, tvAddress, tvName;
        public RelativeLayout rlClick;
        public CircleImageView ivImage;
        public LinearLayout llApply;

        public MyViewHolder(View view) {
            super(view);
            tvJobId = view.findViewById(R.id.tvJobId);
            tvDate = view.findViewById(R.id.tvDate);
            rlClick = view.findViewById(R.id.rlClick);
            ivImage = view.findViewById(R.id.ivImage);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvCategory = view.findViewById(R.id.tvCategory);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvAddress = view.findViewById(R.id.tvAddress);
            llApply = view.findViewById(R.id.llApply);
            tvName = view.findViewById(R.id.tvName);

        }
    }


    public void dialogAbout() {
        dialogApplyJob = new Dialog(mContext/*, android.R.style.Theme_Dialog*/);
        dialogApplyJob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogApplyJob.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogApplyJob.setContentView(R.layout.dailog_apply_job);

        etPriceD = (CustomEditText) dialogApplyJob.findViewById(R.id.etPriceD);
        etAboutD = (CustomEditText) dialogApplyJob.findViewById(R.id.etAboutD);
        tvYesAbout = (CustomTextViewBold) dialogApplyJob.findViewById(R.id.tvYesAbout);
        tvNoAbout = (CustomTextViewBold) dialogApplyJob.findViewById(R.id.tvNoAbout);

        etPriceD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        });

        dialogApplyJob.show();
        dialogApplyJob.setCancelable(false);

        tvNoAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogApplyJob.dismiss();

            }
        });
        tvYesAbout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        params.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(etAboutD));
                        params.put(Consts.PRICE, ProjectUtils.getEditTextValue(etPriceD));
                        submitPersonalProfile();

                    }
                });
    }

    public void applyJob() {

        new HttpsRequest(Consts.APPLIED_JOB_API, params, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                dialogApplyJob.dismiss();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);

                    allJobsFrag.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(allJobsDTOList);
        } else {
            for (AllJobsDTO allJobsDTO : allJobsDTOList) {
                if (allJobsDTO.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(allJobsDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void submitPersonalProfile() {
        if (!validation(etAboutD, mContext.getResources().getString(R.string.val_des))) {
            return;
        } else if (!validation(etPriceD, mContext.getResources().getString(R.string.val_price))) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                applyJob();
            } else {
                ProjectUtils.showToast(mContext, mContext.getResources().getString(R.string.internet_concation));
            }
        }
    }

    public boolean validation(EditText editText, String msg) {
        if (!ProjectUtils.isEditTextFilled(editText)) {
            ProjectUtils.showLong(mContext, msg);
            return false;
        } else {
            return true;
        }
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        public CustomTextView tvSection;

        public MyViewHolderSection(View view) {
            super(view);
            tvSection = view.findViewById(R.id.tvSection);
        }
    }

}