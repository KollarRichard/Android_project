package com.example.appka.mtaaaplikacia;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TwoLineArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] data;
    private final String[] data2;
    private final int layoutResourceId;
    private final String[] imgResource;
    private int cat=1;

        public TwoLineArrayAdapter(Context context, int layoutResourceId, String[] data, String data2[], String img[]) {
            super(context, layoutResourceId, data);
            this.context = context;
            this.data = data;
            this.data2 = data2;
            this.layoutResourceId = layoutResourceId;
            this.imgResource = img;
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;
           /* for (int i = 0; i<imgResource.length; i++) {
                System.out.println(imgResource[i]);
            }*/

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ViewHolder();
                holder.textView1 = (TextView)row.findViewById(R.id.firstLine);
                holder.textView2 = (TextView)row.findViewById(R.id.secondLine);
                holder.imageView = (ImageView)row.findViewById(R.id.icon);
                row.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)row.getTag();
            }
            String name = data[position].toString();
            String address = data2[position].toString();
            try {
                 cat = Integer.parseInt(imgResource[position]);
            } catch (Exception e) {
                System.out.println(e);
            }
            holder.textView1.setText(name);
            holder.textView2.setText(address);


            int pic;
            switch (cat){
                case 0: pic = R.drawable.cafe; break;
                case 1: pic = R.drawable.steak; break;
                case 2: pic = R.drawable.pizza; break;
                case 3: pic = R.drawable.beer; break;
                case 4: pic = R.drawable.vodna; break;
                case 5: pic = R.drawable.restaurant; break;
                default: pic = R.drawable.kappa;
            }
            holder.imageView.setImageResource(pic);
                return row;
            }

            static class ViewHolder
            {
                TextView textView1;
                TextView textView2;
                ImageView imageView;
            }
}

