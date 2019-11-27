package com.youngtech.fabartists.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youngtech.fabartists.DTO.AppliedJobDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.activity.BaseActivity;
import com.youngtech.fabartists.ui.adapter.AppliedJobAdapter;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;


import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppliedJobsFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private String TAG = AppliedJobsFrag.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private AppliedJobAdapter appliedJobAdapter;
    private ArrayList<AppliedJobDTO> appliedJobDTOSList;
    private ArrayList<AppliedJobDTO> appliedJobDTOSListSection;
    private ArrayList<AppliedJobDTO> appliedJobDTOSListSection1;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private LayoutInflater myInflater;
    private SearchView svSearch;
    private RelativeLayout rlSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_applied_jobs, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        myInflater = LayoutInflater.from(getActivity());
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {


        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        rlSearch = v.findViewById(R.id.rlSearch);
        svSearch = v.findViewById(R.id.svSearch);
        tvNo = v.findViewById(R.id.tvNo);
        RVhistorylist = v.findViewById(R.id.RVhistorylist);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVhistorylist.setLayoutManager(mLayoutManager);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    appliedJobAdapter.filter(newText.toString());

                } else {


                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(getActivity())) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getjobs();

                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );


        baseActivity.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkManager.isConnectToInternet(getActivity())) {
                    if (rlSearch.getVisibility() == View.VISIBLE) {
                        baseActivity.ivSearch.setImageResource(R.drawable.ic_search_white);
                        rlSearch.setVisibility(View.GONE);
                    } else {

                        baseActivity.ivSearch.setImageResource(R.drawable.ic_close_circle);
                        rlSearch.setVisibility(View.VISIBLE);

                    }

                } else {
                    ProjectUtils.showToast(getActivity(), getString(R.string.internet_concation));
                }


            }
        });

    }


    public void getjobs() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_APPLIED_JOB_ARTIST_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    baseActivity.ivSearch.setVisibility(View.VISIBLE);
                    try {
                        appliedJobDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<AppliedJobDTO>>() {
                        }.getType();
                        appliedJobDTOSList = (ArrayList<AppliedJobDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    RVhistorylist.setVisibility(View.GONE);
                    baseActivity.ivSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.ARTIST_ID, userDTO.getUser_id());
        return parms;
    }

    public void showData() {
        appliedJobAdapter = new AppliedJobAdapter(AppliedJobsFrag.this, appliedJobDTOSList, userDTO, myInflater);
        RVhistorylist.setAdapter(appliedJobAdapter);
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getjobs();
        rlSearch.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }


    public void gotos() {
        Intent in = new Intent(getActivity(), BaseActivity.class);
        in.putExtra(Consts.SCREEN_TAG, Consts.START_BOOKING_ARTIST_NOTIFICATION);
        startActivity(in);
        baseActivity.finish();

    }

    public void setSection() {
        HashMap<String, ArrayList<AppliedJobDTO>> has = new HashMap<>();
        appliedJobDTOSListSection = new ArrayList<>();
        for (int i = 0; i < appliedJobDTOSList.size(); i++) {


            if (has.containsKey(ProjectUtils.changeDateFormate1(appliedJobDTOSList.get(i).getJob_date()))) {
                appliedJobDTOSListSection1 = new ArrayList<>();
                appliedJobDTOSListSection1 = has.get(ProjectUtils.changeDateFormate1(appliedJobDTOSList.get(i).getJob_date()));
                appliedJobDTOSListSection1.add(appliedJobDTOSList.get(i));
                has.put(ProjectUtils.changeDateFormate1(appliedJobDTOSList.get(i).getJob_date()), appliedJobDTOSListSection1);


            } else {
                appliedJobDTOSListSection1 = new ArrayList<>();
                appliedJobDTOSListSection1.add(appliedJobDTOSList.get(i));
                has.put(ProjectUtils.changeDateFormate1(appliedJobDTOSList.get(i).getJob_date()), appliedJobDTOSListSection1);
            }
        }

        for (String key : has.keySet()) {
            AppliedJobDTO appliedJobDTO = new AppliedJobDTO();
            appliedJobDTO.setSection(true);
            appliedJobDTO.setSection_name(key);
            appliedJobDTOSListSection.add(appliedJobDTO);
            appliedJobDTOSListSection.addAll(has.get(key));

        }


        showData();

    }

}
