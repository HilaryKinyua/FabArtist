package com.youngtech.fabartists.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.youngtech.fabartists.DTO.ArtistBooking;
import com.youngtech.fabartists.DTO.UserDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.network.NetworkManager;
import com.youngtech.fabartists.preferences.SharedPrefrence;
import com.youngtech.fabartists.ui.activity.BaseActivity;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.CustomTextViewBold;
import com.youngtech.fabartists.utils.ProjectUtils;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerBooking extends Fragment implements View.OnClickListener {
    private String TAG = CustomerBooking.class.getSimpleName();
    private View view;
    private CircleImageView ivArtist;
    private CustomTextViewBold tvName;
    private CustomTextView tvLocation,tvDescription,tvDate;
    private RelativeLayout llTime;
    private Chronometer chronometer;
    private LinearLayout llACDE, llAccept, llDecline, llSt, llStart, llCancel, llFinishJob,llWork;

    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private HashMap<String, String> paramsGetBooking = new HashMap<>();
    private HashMap<String, String> paramsBookingOp;
    private HashMap<String, String> paramsDecline;
    private ArtistBooking artistBooking;
    private CardView cardData, cardNoRequest;
    private MapView mMapView;
    private GoogleMap googleMap;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_customer_booking, container, false);

        baseActivity.headerNameTV.setText(getResources().getString(R.string.customer_booking));
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        paramsGetBooking.put(Consts.ARTIST_ID, userDTO.getUser_id());
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location buttont
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)));
                googleMap.addMarker(new MarkerOptions().position(sydney).title(userDTO.getName()).snippet(userDTO.getAddress()).title("My Location"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        setUiAction(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            getBooking();
        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void setUiAction(View v) {
        cardData = v.findViewById(R.id.cardData);
        cardNoRequest = v.findViewById(R.id.cardNoRequest);
        llACDE = v.findViewById(R.id.llACDE);
        llTime = v.findViewById(R.id.llTime);
        llSt = v.findViewById(R.id.llSt);
        llFinishJob = v.findViewById(R.id.llFinishJob);
        llWork = v.findViewById(R.id.llWork);

        ivArtist = v.findViewById(R.id.ivArtist);
        tvName = v.findViewById(R.id.tvName);
        tvDate = v.findViewById(R.id.tvDate);
        tvLocation = v.findViewById(R.id.tvLocation);
        tvDescription = v.findViewById(R.id.tvDescription);
        chronometer = v.findViewById(R.id.chronometer);
        llAccept = v.findViewById(R.id.llAccept);
        llDecline = v.findViewById(R.id.llDecline);
        llStart = v.findViewById(R.id.llStart);
        llCancel = v.findViewById(R.id.llCancel);

        llAccept.setOnClickListener(this);
        llDecline.setOnClickListener(this);
        llStart.setOnClickListener(this);
        llCancel.setOnClickListener(this);
        llFinishJob.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAccept:
                if (NetworkManager.isConnectToInternet(getActivity())) {

                    booking("1");
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
            case R.id.llDecline:
                ProjectUtils.showDialog(getActivity(), getResources().getString(R.string.dec_cpas), getResources().getString(R.string.decline_msg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        decline();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, false);

                break;
            case R.id.llStart:
                if (NetworkManager.isConnectToInternet(getActivity())) {

                    booking("2");
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
            case R.id.llCancel:
                ProjectUtils.showDialog(getActivity(), getResources().getString(R.string.dec_cpas), getResources().getString(R.string.decline_msg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        decline();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, false);
                break;
            case R.id.llFinishJob:
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    booking("3");
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                }
                break;
        }

    }


    public void getBooking() {
        new HttpsRequest(Consts.CURRENT_BOOKING_API, paramsGetBooking, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    cardData.setVisibility(View.VISIBLE);
                    cardNoRequest.setVisibility(View.GONE);
                    try {

                        artistBooking = new Gson().fromJson(response.getJSONObject("data").toString(), ArtistBooking.class);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                        cardData.setVisibility(View.GONE);
                        cardNoRequest.setVisibility(View.VISIBLE);
                    }

                } else {
                    cardData.setVisibility(View.GONE);
                    cardNoRequest.setVisibility(View.VISIBLE);
                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {
                            googleMap = mMap;

                            // For showing a move to my location button
                            googleMap.setMyLocationEnabled(true);

                            // For dropping a marker at a point on the Map
                            LatLng sydney = new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)));
                             googleMap.addMarker(new MarkerOptions().position(sydney).title(userDTO.getName()).snippet(userDTO.getAddress()).title("My Location"));

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(14).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });

                }


            }
        });
    }

    public void showData() {
        tvName.setText(artistBooking.getUserName());
        tvLocation.setText(artistBooking.getAddress());
        tvDate.setText(ProjectUtils.changeDateFormate1(artistBooking.getBooking_date())+" "+artistBooking.getBooking_time());
        Glide.with(getActivity()).
                load(artistBooking.getUserImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivArtist);
        tvDescription.setText(artistBooking.getDescription());
        if (artistBooking.getBooking_type().equalsIgnoreCase("0")||artistBooking.getBooking_type().equalsIgnoreCase("3")){

            if (artistBooking.getBooking_flag().equalsIgnoreCase("0")) {
                llACDE.setVisibility(View.VISIBLE);
                llTime.setVisibility(View.GONE);
                llSt.setVisibility(View.GONE);
                llFinishJob.setVisibility(View.GONE);

            } else if (artistBooking.getBooking_flag().equalsIgnoreCase("1")) {
                llSt.setVisibility(View.VISIBLE);
                llACDE.setVisibility(View.GONE);
                llTime.setVisibility(View.GONE);
                llFinishJob.setVisibility(View.GONE);
            } else if (artistBooking.getBooking_flag().equalsIgnoreCase("3")) {

                llSt.setVisibility(View.GONE);
                llACDE.setVisibility(View.GONE);
                llTime.setVisibility(View.VISIBLE);
                llFinishJob.setVisibility(View.VISIBLE);
                llWork.setVisibility(View.GONE);

                SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

                try {
                    Date dt;

                    if(artistBooking.getWorking_min().equalsIgnoreCase("0")){
                        dt = sdf.parse("0.1");

                    }else {
                        dt = sdf.parse(artistBooking.getWorking_min());

                    }
                    sdf = new SimpleDateFormat("HH:mm:ss");
                    System.out.println(sdf.format(dt));
                    int min = dt.getHours() * 60 + dt.getMinutes();
                    int sec = dt.getSeconds();
                    chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000));
                    chronometer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    LatLng sydney = new LatLng(Double.parseDouble(artistBooking.getC_latitude()), Double.parseDouble(artistBooking.getC_longitude()));
                    googleMap.addMarker(new MarkerOptions().position(sydney).title(artistBooking.getUserName()).snippet(artistBooking.getAddress()));

                    // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });

        }else if(artistBooking.getBooking_type().equalsIgnoreCase("2")) {

            if (artistBooking.getBooking_flag().equalsIgnoreCase("0")) {
                llACDE.setVisibility(View.VISIBLE);
                llTime.setVisibility(View.GONE);
                llSt.setVisibility(View.GONE);
                llFinishJob.setVisibility(View.GONE);
                llWork.setVisibility(View.GONE);

            } else if (artistBooking.getBooking_flag().equalsIgnoreCase("1")) {
                llSt.setVisibility(View.VISIBLE);
                llACDE.setVisibility(View.GONE);
                llTime.setVisibility(View.GONE);
                llFinishJob.setVisibility(View.GONE);
                llWork.setVisibility(View.GONE);
            } else if (artistBooking.getBooking_flag().equalsIgnoreCase("3")) {

                llSt.setVisibility(View.GONE);
                llACDE.setVisibility(View.GONE);
                llTime.setVisibility(View.VISIBLE);
                llFinishJob.setVisibility(View.VISIBLE);
                llWork.setVisibility(View.GONE);

                SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

                try {
                    Date dt;

                    if(artistBooking.getWorking_min().equalsIgnoreCase("0")){
                        dt = sdf.parse("0.1");

                    }else {
                        dt = sdf.parse(artistBooking.getWorking_min());

                    }
                    sdf = new SimpleDateFormat("HH:mm:ss");
                    System.out.println(sdf.format(dt));
                    int min = dt.getHours() * 60 + dt.getMinutes();
                    int sec = dt.getSeconds();
                    chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000));
                    chronometer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;

                    // For showing a move to my location button
                    googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    LatLng sydney = new LatLng(Double.parseDouble(artistBooking.getC_latitude()), Double.parseDouble(artistBooking.getC_longitude()));
                    googleMap.addMarker(new MarkerOptions().position(sydney).title(artistBooking.getUserName()).snippet(artistBooking.getAddress()));

                    // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }




    }

    public void booking(String req) {
        paramsBookingOp = new HashMap<>();
        paramsBookingOp.put(Consts.BOOKING_ID, artistBooking.getId());
        paramsBookingOp.put(Consts.REQUEST, req);
        paramsBookingOp.put(Consts.USER_ID, artistBooking.getUser_id());
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.BOOKING_OPERATION_API, paramsBookingOp, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(getActivity(), msg);
                    getBooking();


                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }

    public void decline() {
        paramsDecline = new HashMap<>();
        paramsDecline.put(Consts.USER_ID, userDTO.getUser_id());
        paramsDecline.put(Consts.BOOKING_ID, artistBooking.getId());
        paramsDecline.put(Consts.DECLINE_BY, "1");
        paramsDecline.put(Consts.DECLINE_REASON, "Busy");
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.DECLINE_BOOKING_API, paramsDecline, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(getActivity(), msg);
                    getBooking();

                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }
}
