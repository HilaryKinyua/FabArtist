package com.youngtech.fabartists.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.utils.CustomButton;
import com.youngtech.fabartists.utils.CustomEditText;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class AddMoney extends AppCompatActivity implements View.OnClickListener {
    private String TAG = AddMoney.class.getSimpleName();
    private Context mContext;
    private CustomEditText etAddMoney;
    private CustomTextView tv1000, tv1500, tv2000;
    private CustomButton cbAdd;
    float rs = 0;
    float rs1 = 0;
    float final_rs = 0;
   private HashMap<String, String> parmas = new HashMap<>();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String amt = "";
    private String currency = "";
    private CustomTextView tvWallet;
    private ImageView ivBack;
    private Dialog dialog;
    private LinearLayout llPaypall, llStripe, llCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        mContext = AddMoney.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
         parmas.put(Consts.USER_ID, userDTO.getUser_id());
        setUiAction();
    }

    public void setUiAction() {
        tvWallet = findViewById(R.id.tvWallet);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().hasExtra(Consts.AMOUNT)) {
            amt = getIntent().getStringExtra(Consts.AMOUNT);
            currency = getIntent().getStringExtra(Consts.CURRENCY);

            tvWallet.setText(currency + " " + amt);
        }

        cbAdd = findViewById(R.id.cbAdd);
        cbAdd.setOnClickListener(this);

        etAddMoney = findViewById(R.id.etAddMoney);
        etAddMoney.setSelection(etAddMoney.getText().length());

        tv1000 = findViewById(R.id.tv1000);
        tv1000.setOnClickListener(this);

        tv1500 = findViewById(R.id.tv1500);
        tv1500.setOnClickListener(this);

        tv2000 = findViewById(R.id.tv2000);
        tv2000.setOnClickListener(this);

        tv1000.setText("+ " + currency + " 1000");
        tv1500.setText("+ " + currency + " 1500");
        tv2000.setText("+ " + currency + " 2000");
    }

    @Override
    public void onClick(View v) {
        if (etAddMoney.getText().toString().trim().equalsIgnoreCase("")) {
            rs1 = 0;

        } else {
            rs1 = Float.parseFloat(etAddMoney.getText().toString().trim());

        }

        switch (v.getId()) {
            case R.id.tv1000:
                rs = 1000;
                final_rs = rs1 + rs;
                etAddMoney.setText(final_rs + "");
                etAddMoney.setSelection(etAddMoney.getText().length());
                break;
            case R.id.tv1500:
                rs = 1500;
                final_rs = rs1 + rs;
                etAddMoney.setText(final_rs + "");
                etAddMoney.setSelection(etAddMoney.getText().length());
                break;
            case R.id.tv2000:
                rs = 2000;
                final_rs = rs1 + rs;
                etAddMoney.setText(final_rs + "");
                etAddMoney.setSelection(etAddMoney.getText().length());
                break;
            case R.id.cbAdd:
                if (etAddMoney.getText().toString().length() > 0 && Float.parseFloat(etAddMoney.getText().toString().trim())>0) {
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        parmas.put(Consts.AMOUNT, ProjectUtils.getEditTextValue(etAddMoney));
                        dialogPayment();


                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_concation));
                    }
                } else {
                    ProjectUtils.showLong(mContext, getResources().getString(R.string.val_money));
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (prefrence.getValue(Consts.SURL).equalsIgnoreCase(Consts.PAYMENT_SUCCESS)) {
            prefrence.clearPreferences(Consts.SURL);
            finish();
        } else if (prefrence.getValue(Consts.FURL).equalsIgnoreCase(Consts.PAYMENT_FAIL)) {
            prefrence.clearPreferences(Consts.FURL);
            finish();
        }else if (prefrence.getValue(Consts.SURL).equalsIgnoreCase(Consts.PAYMENT_SUCCESS_paypal)) {
            prefrence.clearPreferences(Consts.SURL);
            addMoney();
        }else if (prefrence.getValue(Consts.FURL).equalsIgnoreCase(Consts.PAYMENT_FAIL_Paypal)) {
            prefrence.clearPreferences(Consts.FURL);
            finish();
        }
    }


    public void addMoney() {
        new HttpsRequest(Consts.ADD_MONEY_API, parmas, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showLong(mContext, msg);
                    finish();
                } else {
                    ProjectUtils.showLong(mContext, msg);
                }
            }
        });
    }




    public void dialogPayment() {
        dialog = new Dialog(mContext/*, android.R.style.Theme_Dialog*/);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_payment_option);


        ///dialog.getWindow().setBackgroundDrawableResource(R.color.black);
        llPaypall = (LinearLayout) dialog.findViewById(R.id.llPaypall);
        llStripe = (LinearLayout) dialog.findViewById(R.id.llStripe);
        llCancel = (LinearLayout) dialog.findViewById(R.id.llCancel);

        dialog.show();
        dialog.setCancelable(false);
        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        llPaypall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Consts.MAKE_PAYMENT_paypal +"amount=" + ProjectUtils.getEditTextValue(etAddMoney)+"&userId="+ userDTO.getUser_id();
                Intent in2 = new Intent(mContext, PaymetWeb.class);
                in2.putExtra(Consts.PAYMENT_URL, url);
                startActivity(in2);
                dialog.dismiss();

            }
        });
        llStripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Consts.MAKE_PAYMENT + userDTO.getUser_id() + "/" + ProjectUtils.getEditTextValue(etAddMoney);
                Intent in2 = new Intent(mContext, PaymetWeb.class);
                in2.putExtra(Consts.PAYMENT_URL, url);
                startActivity(in2);
                dialog.dismiss();

            }
        });

    }

}
