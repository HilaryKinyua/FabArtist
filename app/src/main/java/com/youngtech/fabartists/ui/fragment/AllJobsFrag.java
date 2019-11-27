package com.youngtech.fabartists.ui.fragment;

import android.app.Activity;
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
import com.youngtech.fabartists.DTO.AllJobsDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.activity.BaseActivity;
import com.youngtech.fabartists.ui.adapter.AllJobsAdapter;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllJobsFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private String TAG = AllJobsFrag.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private AllJobsAdapter allJobsAdapter;
    private ArrayList<AllJobsDTO> allJobsDTOList;
    private ArrayList<AllJobsDTO> allJobsDTOListSection;
    private ArrayList<AllJobsDTO> allJobsDTOListSection1;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private LayoutInflater myInflater;
    private SearchView svSearch;
    private RelativeLayout rlSearch;
    private BaseActivity baseActivity;
    HashMap<String, String> parms = new HashMap<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_jobs, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        myInflater = LayoutInflater.from(getActivity());
        parms.put(Consts.ARTIST_ID, userDTO.getUser_id());
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
                try {
                    if (newText.length() > 0) {
                        allJobsAdapter.filter(newText.toString());

                    }
                } catch (Exception e) {

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
        new HttpsRequest(Consts.GET_ALL_JOB_API, parms, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    baseActivity.ivSearch.setVisibility(View.VISIBLE);
                    try {
                        allJobsDTOList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<AllJobsDTO>>() {
                        }.getType();
                        allJobsDTOList = (ArrayList<AllJobsDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
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

    public void showData() {
        allJobsAdapter = new AllJobsAdapter(AllJobsFrag.this, allJobsDTOList, userDTO, myInflater);
        RVhistorylist.setAdapter(allJobsAdapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    @Override
    public void onRefresh() {
        getjobs();
        rlSearch.setVisibility(View.GONE);
    }

    public void setSection() {
        HashMap<String, ArrayList<AllJobsDTO>> has = new HashMap<>();
        allJobsDTOListSection = new ArrayList<>();
        for (int i = 0; i < allJobsDTOList.size(); i++) {


            if (has.containsKey(ProjectUtils.changeDateFormate1(allJobsDTOList.get(i).getJob_date()))) {
                allJobsDTOListSection1 = new ArrayList<>();
                allJobsDTOListSection1 = has.get(ProjectUtils.changeDateFormate1(allJobsDTOList.get(i).getJob_date()));
                allJobsDTOListSection1.add(allJobsDTOList.get(i));
                has.put(ProjectUtils.changeDateFormate1(allJobsDTOList.get(i).getJob_date()), allJobsDTOListSection1);


            } else {
                allJobsDTOListSection1 = new ArrayList<>();
                allJobsDTOListSection1.add(allJobsDTOList.get(i));
                has.put(ProjectUtils.changeDateFormate1(allJobsDTOList.get(i).getJob_date()), allJobsDTOListSection1);
            }
        }

        for (String key : has.keySet()) {
            AllJobsDTO allJobsDTO = new AllJobsDTO();
            allJobsDTO.setSection(true);
            allJobsDTO.setSection_name(key);
            allJobsDTOListSection.add(allJobsDTO);
            allJobsDTOListSection.addAll(has.get(key));

        }


        showData();

    }

}
