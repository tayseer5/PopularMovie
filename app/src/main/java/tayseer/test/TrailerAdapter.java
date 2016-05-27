package tayseer.test;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pc on 10/20/2015.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    private Context mContext;
    private ArrayList<Trailer> mTrailer = new ArrayList<Trailer>();
    private  int layoutResourceId;

    public TrailerAdapter(Context mContext, int layoutResourceId, ArrayList<Trailer> mTrailer) {
        super(mContext, layoutResourceId, mTrailer);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mTrailer = mTrailer;
    }
    public void setListData(ArrayList<Trailer> mTrailer){
        Log.d("set", "set");
        this.mTrailer=mTrailer;
        Log.d("set","set");
        notifyDataSetChanged();

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("get","get");
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            //holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.textView = (TextView) row.findViewById(R.id.trailer);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Trailer item = mTrailer.get(position);
        holder.textView.setText(item.getName());
        return row;
    }

    static class ViewHolder {
        // TextView titleTextView;
        TextView textView;
    }
}
