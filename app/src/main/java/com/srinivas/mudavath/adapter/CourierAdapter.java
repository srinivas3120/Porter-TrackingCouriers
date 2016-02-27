package com.srinivas.mudavath.adapter;

import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.srinivas.mudavath.network.MyApplication;
import com.srinivas.mudavath.network.VolleySingleton;
import com.srinivas.mudavath.pojo.CourierItem;
import com.srinivas.mudavath.porter.R;

import java.util.ArrayList;

/**
 * Created by Mudavath Srinivas on 21-02-2016.
 */
public class CourierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View.OnClickListener clickListener;
    private ArrayList<CourierItem> courierItems=new ArrayList<CourierItem>();
    VolleySingleton volleySingleton=null;
    ImageLoader imageLoader=null;


    int defaultImage;
    private String searchString=null;

    public CourierAdapter(ArrayList<CourierItem> courierItems, View.OnClickListener clickListener){
        this.courierItems=courierItems;
        this.clickListener=clickListener;

        volleySingleton=VolleySingleton.getInstance();
        imageLoader=volleySingleton.getImageLoader();

        defaultImage= R.drawable.profile_pic_default_profile_pic;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.courier_item,parent, false);
        return new ViewHolderForCourierItem(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourierItem courierItem = courierItems.get(position);
        final ViewHolderForCourierItem viewHolderForCourierItem = (ViewHolderForCourierItem) holder;
        Resources res = MyApplication.getAppContext().getResources();

        viewHolderForCourierItem.tv_name.setText(courierItem.getName());
        viewHolderForCourierItem.tv_price.setText(String.format(res.getString(R.string.price), courierItem.getValue()));
        viewHolderForCourierItem.tv_weight.setText(String.format(res.getString(R.string.weight), courierItem.getWeight()));
        viewHolderForCourierItem.iv_item_pic.setImageResource(R.drawable.profile_pic_default_profile_pic);

        imageLoader.get(courierItem.getUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    viewHolderForCourierItem.iv_item_pic.setImageBitmap(response.getBitmap());
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                if (defaultImage != 0) {
                    viewHolderForCourierItem.iv_item_pic.setImageResource(R.drawable.profile_pic_default_profile_pic);
                }
            }
        });

        viewHolderForCourierItem.cv_courier_element.setTag(position);
    }

    @Override
    public int getItemCount() {
        return courierItems.size();
    }

    private class ViewHolderForCourierItem extends RecyclerView.ViewHolder {

        private ImageView iv_item_pic;
        private TextView tv_name;
        private TextView tv_price;
        private TextView tv_weight;
        private CardView cv_courier_element;

        public ViewHolderForCourierItem(View itemView) {
            super(itemView);

            iv_item_pic= (ImageView) itemView.findViewById(R.id.iv_item_pic);
            tv_name= (TextView) itemView.findViewById(R.id.tv_name);
            tv_price= (TextView) itemView.findViewById(R.id.tv_price);
            tv_weight= (TextView) itemView.findViewById(R.id.tv_weight);
            cv_courier_element= (CardView) itemView.findViewById(R.id.cv_courier_element);

            cv_courier_element.setOnClickListener(clickListener);

        }
    }
}
