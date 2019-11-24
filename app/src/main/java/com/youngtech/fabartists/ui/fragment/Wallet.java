package com.youngtech.fabartists.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.DTO.WalletHistory;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.activity.AddMoney;
import com.youngtech.fabartists.ui.activity.BaseActivity;
import com.youngtech.fabartists.ui.adapter.AdapterWalletHistory;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Wallet extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private LinearLayout llAddMoney;
    private CustomTextView tvAll, tvDebit, tvCredit;
    private CustomTextView tvAllSelect, tvDebitSelect, tvCreditSelect;
    private AdapterWalletHistory adapterWalletHistory;
    private ArrayList<WalletHistory> walletHistoryList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String TAG = Wallet.class.getSimpleName();
    private RecyclerView RVhistorylist;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private String status = "";

    HashMap<String, String> parms;
    HashMap<String, String> parmsGetWallet = new HashMap<>();
    private CustomTextView tvWallet;
    private String amt = "";
    private String currency = "";
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wallet, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.ic_wallet));
        parmsGetWallet.put(Consts.USER_ID, userDTO.getUser_id());

        parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        tvWallet = v.findViewById(R.id.tvWallet);

        tvAll = v.findViewById(R.id.tvAll);
        tvAll.setOnClickListener(this);

        tvDebit = v.findViewById(R.id.tvDebit);
        tvDebit.setOnClickListener(this);

        tvCredit = v.findViewById(R.id.tvCredit);
        tvCredit.setOnClickListener(this);

        tvAllSelect = v.findViewById(R.id.tvAllSelect);
        tvDebitSelect = v.findViewById(R.id.tvDebitSelect);
        tvCreditSelect = v.findViewById(R.id.tvCreditSelect);

        llAddMoney = v.findViewById(R.id.llAddMoney);
        llAddMoney.setOnClickListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        tvNo = v.findViewById(R.id.tvNo);
        RVhistorylist = v.findViewById(R.id.RVhistorylist);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVhistorylist.setLayoutManager(mLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAddMoney:
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    Intent in = new Intent(getActivity(), AddMoney.class);
                    in.putExtra(Consts.AMOUNT, amt);
                    in.putExtra(Consts.CURRENCY, currency);
                    startActivity(in);
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
            case R.id.tvAll:
                setSelected(true, false, false);
                parms = new HashMap<>();
                parms.put(Consts.USER_ID, userDTO.getUser_id());
                getHistroy();
                break;
            case R.id.tvCredit:
                setSelected(false, false, true);
                status = "0";
                parms = new HashMap<>();
                parms.put(Consts.USER_ID, userDTO.getUser_id());
                parms.put(Consts.STATUS, status);
                getHistroy();
                break;
            case R.id.tvDebit:
                setSelected(false, true, false);
                status = "1";
                parms = new HashMap<>();
                parms.put(Consts.USER_ID, userDTO.getUser_id());
                parms.put(Consts.STATUS, status);
                getHistroy();
                break;
        }
    }

    public void getHistroy() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_WALLET_HISTORY_API, parms, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVhistorylist.setVisibility(View.VISIBLE);
                    try {
                        walletHistoryList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<WalletHistory>>() {
                        }.getType();
                        walletHistoryList = (ArrayList<WalletHistory>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    RVhistorylist.setVisibility(View.GONE);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getWallet();
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(getActivity())) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getHistroy();

                                        } else {
                                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );

    }

    public void getWallet() {
        new HttpsRequest(Consts.GET_WALLET_API, parmsGetWallet, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        amt = response.getJSONObject("data").getString("amount");
                        currency = response.getJSONObject("data").getString("currency_type");
                        tvWallet.setText(currency + " " + amt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                }
            }
        });
    }

    public void showData() {
        if (walletHistoryList.size() > 0) {
            tvNo.setVisibility(View.GONE);
            RVhistorylist.setVisibility(View.VISIBLE);

            adapterWalletHistory = new AdapterWalletHistory(Wallet.this, walletHistoryList);
            RVhistorylist.setAdapter(adapterWalletHistory);
        } else {
            tvNo.setVisibility(View.VISIBLE);
            RVhistorylist.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getHistroy();
    }

    public void setSelected(boolean firstBTN, boolean secondBTN, boolean thirdBTN) {
        if (firstBTN) {
            tvAllSelect.setVisibility(View.VISIBLE);
            tvDebitSelect.setVisibility(View.GONE);
            tvCreditSelect.setVisibility(View.GONE);

        }
        if (secondBTN) {
            tvDebitSelect.setVisibility(View.VISIBLE);
            tvAllSelect.setVisibility(View.GONE);
            tvCreditSelect.setVisibility(View.GONE);

        }
        if (thirdBTN) {
            tvCreditSelect.setVisibility(View.VISIBLE);
            tvAllSelect.setVisibility(View.GONE);
            tvDebitSelect.setVisibility(View.GONE);

        }
        tvAllSelect.setSelected(firstBTN);
        tvDebitSelect.setSelected(secondBTN);
        tvCreditSelect.setSelected(secondBTN);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }
}
