package com.example.joseantonio.indicadoresmovil.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joseantonio.indicadoresmovil.Modelos.GridItem;
import com.example.joseantonio.indicadoresmovil.R;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<GridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            holder.courseTextView = (TextView) row.findViewById(R.id.grid_item_course);
            //holder.idcurso = (TextView) row.findViewById(R.id.grid_item_id);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));
        holder.courseTextView.setText(Html.fromHtml(item.getCourse()));
       // holder.idcurso.setText(Html.fromHtml(item.getId()));


//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);

        row.setId(Integer.parseInt(item.getId()));
       // Picasso.with(mContext).load(item.getImage()).into(holder.imageView);



        String posicion=item.getId();
        String actual= Integer.toString(position);


        if (item.getAlerta().equals("danger")){
            row.setBackgroundColor(Color.parseColor("#e53935"));

            // Log.d("row",String.valueOf(row.getId()));
            holder.titleTextView.setTextColor(Color.WHITE);
            holder.courseTextView.setTextColor(Color.WHITE);

        }

        if (item.getAlerta().equals("success")){
            row.setBackgroundColor(Color.parseColor("#66bb6a"));

            // Log.d("row",String.valueOf(row.getId()));
            holder.titleTextView.setTextColor(Color.WHITE);
            holder.courseTextView.setTextColor(Color.WHITE);


        }




        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        TextView courseTextView;
        //TextView idcurso;
    }
}