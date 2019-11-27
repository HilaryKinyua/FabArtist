package com.youngtech.fabartists.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youngtech.fabartists.DTO.ChatListDTO;
import com.youngtech.fabartists.DTO.GetCommentDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.adapter.AdapterViewComment;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ImageCompression;
import com.youngtech.fabartists.utils.MainFragment;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class OneTwoOneChat extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    private String TAG = OneTwoOneChat.class.getSimpleName();
    private ListView lvComment;
    private ImageView buttonSendMessage, IVback, emojiButton;
    private AdapterViewComment adapterViewComment;
    private String id = "";
    private ArrayList<GetCommentDTO> getCommentDTOList;
    private  InputMethodManager inputManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmojiconEditText edittextMessage;
    private  EmojIconActions emojIcon;
    private RelativeLayout relative;
    private Context mContext;
    private ChatListDTO chatListDTO;
    private  HashMap<String, String> parmsGet = new HashMap<>();
    private CustomTextViewBold tvNameHedar;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    IntentFilter intentFilter = new IntentFilter();

    private LinearLayout mContainerActions, mContainerImg;
    private boolean actions_container_visible = false, img_container_visible = false;;
    private ImageView mActionImage, mPreviewImg, mDeleteImg, mActionContainerImg;

    BottomSheet.Builder builder;
    Uri picUri;
    int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2;
    int CROP_CAMERA_IMAGE = 3, CROP_GALLERY_IMAGE = 4;
    String imageName;
    String pathOfImage;
    Bitmap bm;
    ImageCompression imageCompression;
    byte[] resultByteArray;
    File file;
    Bitmap bitmap = null;
    private HashMap<String, File> paramsFile = new HashMap<>();
    HashMap<String, String> values = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_two_one_chat);
        mContext = OneTwoOneChat.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        intentFilter.addAction(Consts.BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);

        if (getIntent().hasExtra(Consts.CHAT_LIST_DTO)){
            chatListDTO = (ChatListDTO) getIntent().getSerializableExtra(Consts.CHAT_LIST_DTO);

            parmsGet.put(Consts.ARTIST_ID, userDTO.getUser_id());
            parmsGet.put(Consts.USER_ID, chatListDTO.getUser_id());
        }
        setUiAction();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Value " + prefrence.getIntValue("Value"));
    }

    public void setUiAction() {
        mContainerActions = (LinearLayout) findViewById(R.id.container_actions);
        mContainerActions.setVisibility(View.GONE);
        mActionImage = (ImageView) findViewById(R.id.addFilesImg);
        mActionImage.setOnClickListener(this);

        mContainerImg = (LinearLayout) findViewById(R.id.container_img);
        mContainerImg.setVisibility(View.GONE);

        mPreviewImg = (ImageView) findViewById(R.id.previewImg);
        mDeleteImg = (ImageView) findViewById(R.id.deleteImg);
        mActionContainerImg = (ImageView) findViewById(R.id.actionContainerImg);


        tvNameHedar = (CustomTextViewBold) findViewById(R.id.tvNameHedar);
        tvNameHedar.setText(chatListDTO.getUserName());
        relative = (RelativeLayout) findViewById(R.id.relative);
        edittextMessage = (EmojiconEditText) findViewById(R.id.edittextMessage);
        emojiButton = (ImageView) findViewById(R.id.emojiButton);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        lvComment = (ListView) findViewById(R.id.lvComment);
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
                hideActionsContainer();

                if (img_container_visible)
                {
                    mActionContainerImg.setVisibility(View.GONE);
                }

            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });



   /*     if (pathOfImage != null && pathOfImage.length() > 0)
        {
            mPreviewImg.setImageURI(Uri.fromFile(new File(pathOfImage)));
            showImageContainer();
        }
*/
        mDeleteImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                picUri = null;
                pathOfImage = "";

                hideImageContainer();
            }
        });

        mActionContainerImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (actions_container_visible)
                {
                    hideActionsContainer();
                    return;
                }

                showActionsContainer();
            }
        });


        builder = new BottomSheet.Builder(OneTwoOneChat.this).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.select_img));
        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.camera_cards:
                        if (ProjectUtils.hasPermissionInManifest(OneTwoOneChat.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(OneTwoOneChat.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                try {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    File file = getOutputMediaFile(1);
                                    if (!file.exists()) {
                                        try {
                                            ProjectUtils.pauseProgressDialog();
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.asd", newFile);
                                        picUri = FileProvider.getUriForFile(OneTwoOneChat.this.getApplicationContext(), OneTwoOneChat.this.getApplicationContext().getPackageName() + ".fileprovider", file);
                                    } else {
                                        picUri = Uri.fromFile(file); // create
                                    }

                                    prefrence.setValue(Consts.IMAGE_URI_CAMERA, picUri.toString());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                                    startActivityForResult(intent, PICK_FROM_CAMERA);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        break;
                    case R.id.gallery_cards:
                        if (ProjectUtils.hasPermissionInManifest(OneTwoOneChat.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(OneTwoOneChat.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                File file = getOutputMediaFile(1);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                picUri = Uri.fromFile(file);

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_pic)), PICK_FROM_GALLERY);

                            }
                        }
                        break;
                    case R.id.cancel_cards:
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        break;
                }
            }
        });
    }


    private File getOutputMediaFile(int type) {
        String root = Environment.getExternalStorageDirectory().toString();
        File mediaStorageDir = new File(root, Consts.APP_NAME);
        /**Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    Consts.APP_NAME + timeStamp + ".png");

            imageName = Consts.APP_NAME + timeStamp + ".png";
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_CAMERA_IMAGE) {
            if (data != null) {
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(OneTwoOneChat.this);
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            showImageContainer();
                            Glide.with(OneTwoOneChat.this).load("file://" + imagePath)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mPreviewImg);
                            try {
                                // bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                                file = new File(imagePath);
                                paramsFile.put(Consts.IMAGE,file);
                                Log.e("image", imagePath);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CROP_GALLERY_IMAGE) {
            if (data != null) {
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                try {
                    bm = MediaStore.Images.Media.getBitmap(OneTwoOneChat.this.getContentResolver(), picUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(OneTwoOneChat.this);
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            showImageContainer();
                            Glide.with(OneTwoOneChat.this).load("file://" + imagePath)
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(mPreviewImg);
                            Log.e("image", imagePath);
                            try {
                                file = new File(imagePath);
                                paramsFile.put(Consts.IMAGE,file);
                                Log.e("image", imagePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            if (picUri != null) {
                picUri = Uri.parse(prefrence.getValue(Consts.IMAGE_URI_CAMERA));
                startCropping(picUri, CROP_CAMERA_IMAGE);
            } else {
                picUri = Uri.parse(prefrence
                        .getValue(Consts.IMAGE_URI_CAMERA));
                startCropping(picUri, CROP_CAMERA_IMAGE);
            }
        }
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri tempUri = data.getData();
                Log.e("front tempUri", "" + tempUri);
                if (tempUri != null) {
                    startCropping(tempUri, CROP_GALLERY_IMAGE);
                } else {

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void startCropping(Uri uri, int requestCode) {
        Intent intent = new Intent(OneTwoOneChat.this, MainFragment.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }

    public void showActionsContainer()
    {
        actions_container_visible = true;
        mContainerActions.setVisibility(View.VISIBLE);
        mActionContainerImg.setBackgroundResource(R.drawable.ic_close_container_action);
    }

    public void hideActionsContainer()
    {
        actions_container_visible = false;
        mContainerActions.setVisibility(View.GONE);
        mActionContainerImg.setBackgroundResource(R.drawable.ic_open_container_action);
    }

    public void showImageContainer()
    {
        img_container_visible = true;
        mContainerImg.setVisibility(View.VISIBLE);
        mActionContainerImg.setVisibility(View.GONE);
    }

    public void hideImageContainer()
    {
        img_container_visible = false;
        mContainerImg.setVisibility(View.GONE);
        mActionContainerImg.setVisibility(View.VISIBLE);
        mActionContainerImg.setBackgroundResource(R.drawable.ic_open_container_action);
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
            case R.id.addFilesImg:
                hideActionsContainer();
                builder.show();
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
        new HttpsRequest(Consts.GET_CHAT_API, parmsGet, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                if (flag) {
                    try {
                        getCommentDTOList = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<GetCommentDTO>>() {
                        }.getType();
                        getCommentDTOList = (ArrayList<GetCommentDTO>) new Gson().fromJson(response.getJSONArray("my_chat").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                }
            }
        });
    }

    public void showData()
    {
        adapterViewComment = new AdapterViewComment(mContext, getCommentDTOList, userDTO);
        lvComment.setAdapter(adapterViewComment);
        lvComment.setSelection(getCommentDTOList.size() - 1);
    }


    public void doComment() {
        values.put(Consts.USER_ID, chatListDTO.getUser_id());
        values.put(Consts.ARTIST_ID, userDTO.getUser_id());
        values.put(Consts.MESSAGE, ProjectUtils.getEditTextValue(edittextMessage));
        values.put(Consts.SEND_BY, userDTO.getUser_id());
        values.put(Consts.SENDER_NAME, userDTO.getName());

        if (file != null){
            values.put(Consts.CHAT_TYPE,"2");
        }else {
            values.put(Consts.CHAT_TYPE,"1");
        }
        new HttpsRequest(Consts.SEND_CHAT_API, values,paramsFile, mContext).imagePost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    edittextMessage.setText("");
                    hideImageContainer();
                    getComment();
                    file = null;
                    pathOfImage = "";
                } else {
                }
            }
        });
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Consts.BROADCAST)) {
                getComment();
                Log.e("BROADCAST","BROADCAST");
                Log.e(TAG, "Value " + prefrence.getIntValue("Value"));

            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
