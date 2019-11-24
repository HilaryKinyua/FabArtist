package com.youngtech.fabartists.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.youngtech.fabartists.DTO.ProductDTO;
import com.youngtech.fabartists.R;
import com.youngtech.fabartists.https.HttpsRequest;
import com.youngtech.fabartists.interfacess.Consts;
import com.youngtech.fabartists.interfacess.Helper;
import com.youngtech.fabartists.ui.fragment.Services;
import com.youngtech.fabartists.utils.CustomTextView;
import com.youngtech.fabartists.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VARUN on 01/01/19.
 */

public class AdapterServices extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Services services;
    LayoutInflater mLayoutInflater;
    private ArrayList<ProductDTO> productDTOList;
    private Context context;
    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private HashMap<String, String> parms = new HashMap<>();
    private String TAG = AdapterServices.class.getSimpleName();
    private DialogInterface dialog_book;

    public AdapterServices(Services services, ArrayList<ProductDTO> productDTOList) {
        this.services = services;
        context = services.getActivity();
        this.productDTOList = productDTOList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_camera, parent, false);
            return new ViewHolder1(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_services, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_CAMERA:
                final ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                viewHolder1.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        services.addServices();
                    }
                });
                break;

            case TYPE_NORMAL:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                final int pos = position - 1;
                if (productDTOList.get(pos).getProduct_name().equalsIgnoreCase("")){
                    myViewHolder.itemView.setVisibility(View.GONE);
                }
                myViewHolder.CTVproductname.setText(productDTOList.get(pos).getProduct_name());
                myViewHolder.CTVproductprice.setText(productDTOList.get(pos).getCurrency_type() + productDTOList.get(pos).getPrice());
                Glide.with(context).
                        load(productDTOList.get(pos).getProduct_image())
                        .placeholder(R.drawable.bg)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(myViewHolder.IVproduct);

                myViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parms.put(Consts.PRODUCT_ID, productDTOList.get(pos).getId());
                        deleteDialog();
                    }
                });

                break;

        }

    }

    @Override
    public int getItemCount() {
        return productDTOList.size()+1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView IVproduct, ivDelete;
        public CustomTextView CTVproductname, CTVproductprice;


        public MyViewHolder(View view) {
            super(view);


            IVproduct = (ImageView) itemView.findViewById(R.id.IVproduct);
            CTVproductname = itemView.findViewById(R.id.CTVproductname);
            CTVproductprice = itemView.findViewById(R.id.CTVproductprice);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);


        }
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder {

        public ViewHolder1(View v) {
            super(v);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_CAMERA;
        } else {
            return TYPE_NORMAL;
        }
    }
    public void deleteDialog() {
        try {
            new AlertDialog.Builder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(context.getResources().getString(R.string.delete_service))
                    .setMessage(context.getResources().getString(R.string.delete_service_msg))
                    .setCancelable(false)
                    .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            deleteGallery();

                        }
                    })
                    .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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

    public void deleteGallery() {
        new HttpsRequest(Consts.DELETE_PRODUCT_API, parms, context).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    services.getParentData();
                } else {
                    ProjectUtils.showLong(context, msg);
                }
            }
        });
    }

}