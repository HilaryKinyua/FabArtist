package com.youngtech.fabartists.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youngtech.fabartists.DTO.TicketCommentDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.adapter.AdapterViewCommentTicket;
import com.youngtech.fabartists.utils.CustomEditText;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class CommentOneByOne extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String TAG = CommentOneByOne.class.getSimpleName();
    private ListView lvComment;
    private CustomEditText etMessage;
    private ImageView buttonSendMessage, IVback, emojiButton;
    private AdapterViewCommentTicket adapterViewCommentTicket;
    private String id = "";
    private ArrayList<TicketCommentDTO> ticketCommentDTOSList;
    private InputMethodManager inputManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmojiconEditText edittextMessage;
    private EmojIconActions emojIcon;
    private RelativeLayout relative;
    private Context mContext;
    private HashMap<String, String> parmsGet = new HashMap<>();
    private CustomTextViewBold tvNameHedar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String ticket_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_one_by_one);
        mContext = CommentOneByOne.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getIntent().hasExtra(Consts.TICKET_ID)) {

            ticket_id = getIntent().getStringExtra(Consts.TICKET_ID);

            parmsGet.put(Consts.TICKET_ID, ticket_id);
            parmsGet.put(Consts.USER_ID, userDTO.getUser_id());


        }
        setUiAction();

    }


    public void setUiAction() {
        tvNameHedar = (CustomTextViewBold) findViewById(R.id.tvNameHedar);
        relative = (RelativeLayout) findViewById(R.id.relative);
        edittextMessage = (EmojiconEditText) findViewById(R.id.edittextMessage);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        lvComment = (ListView) findViewById(R.id.lvComment);
        etMessage = (CustomEditText) findViewById(R.id.etMessage);
        buttonSendMessage = (ImageView) findViewById(R.id.buttonSendMessage);
        IVback = (ImageView) findViewById(R.id.IVback);
        buttonSendMessage.setOnClickListener(this);
        IVback.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.e("Runnable", "FIRST");
                                        if (NetworkManager.isConnectToInternet(mContext)) {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getComment();

                                        } else {
                                            ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                                        }
                                    }
                                }
        );

        emojIcon = new EmojIconActions(this, relative, edittextMessage, emojiButton, "#495C66", "#DCE1E2", "#E6EBEF");
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

    }

    public boolean validateMessage() {
        if (edittextMessage.getText().toString().trim().length() <= 0) {
            edittextMessage.setError(getResources().getString(R.string.val_comment));
            edittextMessage.requestFocus();
            return false;
        } else {
            edittextMessage.setError(null);
            edittextMessage.clearFocus();
            return true;
        }
    }

    public void submit() {
        if (!validateMessage()) {
            return;
        } else {
            try {
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {

            }
            if (NetworkManager.isConnectToInternet(mContext)) {
                doComment();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSendMessage:
                submit();
                break;
            case R.id.IVback:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getComment();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

    public void getComment() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_TICKET_COMMENTS_API, parmsGet, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    try {
                        ticketCommentDTOSList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<TicketCommentDTO>>() {
                        }.getType();
                        ticketCommentDTOSList = (ArrayList<TicketCommentDTO>) new Gson().fromJson(response.getJSONArray("ticket_comment").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                }
            }
        });
    }

    public void showData() {
        adapterViewCommentTicket = new AdapterViewCommentTicket(mContext, ticketCommentDTOSList, userDTO);
        lvComment.setAdapter(adapterViewCommentTicket);
        lvComment.setSelection(ticketCommentDTOSList.size() - 1);
    }


    public void doComment() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.ADD_TICKET_COMMENTS_API, getParamDO(), mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    edittextMessage.setText("");
                    getComment();
                } else {
                }
            }
        });
    }

    public HashMap<String, String> getParamDO() {
        HashMap<String, String> values = new HashMap<>();
        values.put(Consts.TICKET_ID, ticket_id);
        values.put(Consts.USER_ID, userDTO.getUser_id());
        values.put(Consts.COMMENT, ProjectUtils.getEditTextValue(edittextMessage));
        Log.e("POST", values.toString());
        return values;
    }

}
