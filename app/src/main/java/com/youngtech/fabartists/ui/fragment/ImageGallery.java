package com.youngtech.fabartists.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.youngtech.fabartists.DTO.ArtistDetailsDTO;
import com.youngtech.fabartists.DTO.GalleryDTO;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.databinding.DailogArGallryBinding;
import com.youngtech.fabartists.databinding.DailogArProductBinding;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.adapter.AdapterGallery;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ImageCompression;
import com.youngtech.fabartists.utils.MainFragment;
import com.youngtech.fabartists.utils.ProjectUtils;
import com.youngtech.fabartists.utils.TouchImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ImageGallery extends Fragment implements View.OnClickListener {
    private String TAG = ImageGallery.class.getSimpleName();
    private View view;
    private ArtistDetailsDTO artistDetailsDTO;
    private RecyclerView rvGallery;
    private ArrayList<GalleryDTO> galleryList;
    private AdapterGallery adapterGallery;
    private Bundle bundle;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout llBack;
    private TouchImageView ivZoom;
    private RelativeLayout rlZoomImg, rlView;
    private ImageView ivClose;
    private CustomTextViewBold tvNotFound;
    private ArtistProfile parentFrag;
    private HashMap<String, String> paramsUpdate;
    private Dialog dialogEditGallery;
    private HashMap<String, File> paramsFile;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
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
    DailogArGallryBinding binding1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_image_gallery, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parentFrag = ((ArtistProfile) ImageGallery.this.getParentFragment());
        bundle = this.getArguments();
        artistDetailsDTO = (ArtistDetailsDTO) bundle.getSerializable(Consts.ARTIST_DTO);
        showUiAction(view);
        return view;
    }

    public void showUiAction(View v) {
        rvGallery = (RecyclerView) v.findViewById(R.id.rvGallery);
        tvNotFound = (CustomTextViewBold) v.findViewById(R.id.tvNotFound);
        llBack = (LinearLayout) v.findViewById(R.id.llBack);
        ivZoom = (TouchImageView) v.findViewById(R.id.ivZoom);
        rlZoomImg = (RelativeLayout) v.findViewById(R.id.rlZoomImg);
        rlView = (RelativeLayout) v.findViewById(R.id.rlView);
        ivClose = (ImageView) v.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);

        builder = new BottomSheet.Builder(getActivity()).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.select_img));
        builder.listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.camera_cards:
                        if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                                        picUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);
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
                        if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(getActivity(), PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

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

        showData();

    }


    public void showData() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        galleryList = new ArrayList<>();
        galleryList = artistDetailsDTO.getGallery();

        if (galleryList.size()<=0){
            galleryList.add(new GalleryDTO());
        }
        if (galleryList.size() > 0) {
            tvNotFound.setVisibility(View.GONE);
            rlView.setVisibility(View.VISIBLE);
            adapterGallery = new AdapterGallery(ImageGallery.this, galleryList);
            rvGallery.setLayoutManager(gridLayoutManager);
            rvGallery.setAdapter(adapterGallery);
        } else {
            tvNotFound.setVisibility(View.VISIBLE);
            rlView.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivClose:
                rlZoomImg.setVisibility(View.GONE);
                break;
        }
    }

    public void showImg(String imgURL) {
        rlZoomImg.setVisibility(View.VISIBLE);
        Glide
                .with(getActivity())
                .load(imgURL)
                .placeholder(R.drawable.dummyuser_image)
                .into(ivZoom);
    }

    public void getParentData() {
        parentFrag.getArtist();
    }

    public void addGalleryClick() {
        dialogGallery();
    }

    public void dialogGallery() {
        paramsUpdate = new HashMap<>();
        paramsFile = new HashMap<>();
        dialogEditGallery = new Dialog(getActivity()/*, android.R.style.Theme_Dialog*/);
        dialogEditGallery.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditGallery.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding1 = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dailog_ar_gallry, null, false);
        dialogEditGallery.setContentView(binding1.getRoot());
        dialogEditGallery.show();
        dialogEditGallery.setCancelable(false);


        binding1.etImageGallD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
        binding1.tvNoGall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditGallery.dismiss();

            }
        });
        binding1.tvYesGall.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paramsUpdate.put(Consts.USER_ID, userDTO.getUser_id());
                        paramsFile.put(Consts.IMAGE, file);

                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            if (binding1.etImageGallD.getText().toString().length() > 0) {
                                addGallery();

                            } else {
                                ProjectUtils.showLong(getActivity(), getResources().getString(R.string.val_iamg_ad));
                            }
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
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
                    imageCompression = new ImageCompression(getActivity());
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            try {
                                // bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                                file = new File(imagePath);
                                binding1.etImageGallD.setText(imagePath);
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
                    bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(getActivity());
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                        @Override
                        public void processFinish(String imagePath) {
                            Log.e("image", imagePath);
                            try {
                                file = new File(imagePath);

                                binding1.etImageGallD.setText(imagePath);

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
        Intent intent = new Intent(getActivity(), MainFragment.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }

    public void addGallery() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.ADD_GALLERY_API, paramsUpdate, paramsFile, getActivity()).imagePost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(getActivity(), msg);
                    parentFrag.getArtist();
                    dialogEditGallery.dismiss();
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }

}
