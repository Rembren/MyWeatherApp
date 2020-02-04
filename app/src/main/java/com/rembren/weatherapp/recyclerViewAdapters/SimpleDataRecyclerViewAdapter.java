package com.rembren.weatherapp.recyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rembren.weatherapp.DatabaseHelper;
import com.rembren.weatherapp.R;

public class SimpleDataRecyclerViewAdapter extends RecyclerView.Adapter<SimpleDataRecyclerViewAdapter.DBViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    DatabaseHelper DBHelper ;

    public SimpleDataRecyclerViewAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        mContext = context;
        DBHelper = new  DatabaseHelper(context);
    }

    @NonNull
    @Override
    public SimpleDataRecyclerViewAdapter.DBViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item, parent, false);
        return new SimpleDataRecyclerViewAdapter.DBViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleDataRecyclerViewAdapter.DBViewHolder holder, final int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        holder.city.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.CITY_NAME)));
        holder.address.setText(mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.CITY_ADDRESS)));
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked on " + (position + 1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {

        SQLiteDatabase db = DBHelper.getWritableDatabase();
        mCursor.moveToPosition(position);
        long id = mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.ITEM_ID));
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ITEM_ID + "=" + id, null);
        notifyDataSetChanged();
    }


    public class DBViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView city;
        TextView address;
        LinearLayout parent;


        public DBViewHolder(View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city_name);
            address = itemView.findViewById(R.id.address);
            parent = itemView.findViewById(R.id.parent_layout);

            parent.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 0, "Delete");
            menu.add(this.getAdapterPosition(), 2, 1, "Sort");
        }

    }
}