package com.example.joseantonio.indicadoresmovil.Adapters;

import android.app.Activity;
import android.content.Context;
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

/**
 * Created by luis  manuel cobian on 18/01/2017.
 */


//SIRVE PARA VALIDAR LA FOTO DE LOS TEMAS , CARGAS
public class GridViewIndicadores extends ArrayAdapter<GridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    public GridViewIndicadores(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData) {
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
            holder.courseTextView.setVisibility(View.INVISIBLE);
            //holder.courseTextId = (TextView) row.findViewById(R.id.grid_item_course) ;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
            //  holder.courseTextView.getText();
        }

        GridItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));
//      holder.courseTextView.setText(Html.fromHtml(item.getCourse()));
//   holder.courseTextId.setText(Html.fromHtml(item.getId()));
        row.setId(Integer.parseInt(item.getId()));
        //row.setBackgroundColor(Color.parseColor("#18A608"));
        if(item.getTitle().equals("SALUD")){
            holder.imageView.setImageResource(R.drawable.salud);

        }
        if(item.getTitle().equals("EDUCACION")){
            holder.imageView.setImageResource(R.drawable.educat);
        }
//        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
        TextView courseTextView;
        TextView courseTextId;


    }
}