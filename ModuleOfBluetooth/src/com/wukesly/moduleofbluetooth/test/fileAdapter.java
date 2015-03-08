package com.wukesly.moduleofbluetooth.test;

import java.util.List;

import com.wukesly.moduleofbluetooth.R;



import android.app.Activity;
import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 123
 *
 */
public class fileAdapter extends BaseAdapter {

    private List<myFile> list;
    private Context context;

    public fileAdapter(Context context,List<myFile> list) {
        // TODO Auto-generated constructor stub
        super();
        this.context = context;
        this.list = list;
        //Log.i("into", list.size()+"适配�?"+context.getPackageName());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        if(arg0<list.size()&&arg0>=0)
            return list.get(arg0);
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(position<= list.size())
        {

            View view = ((Activity)context).getLayoutInflater().inflate(
                    R.layout.songinfo_show_layout, null);

            ImageView imageview = (ImageView)view.findViewById(
                    R.id.songinfo_imgv);

            TextView FileName_textview = (TextView)view.findViewById(
                    R.id.songinfo_nameTv);

            TextView FilePath_textview = (TextView)view.findViewById(
                    R.id.songinfo_singerTv);

            imageview.setBackgroundResource(list.get(position).getImageId());

            FileName_textview.setText(list.get(position).getFilename());
            FilePath_textview.setText(list.get(position).getFilePath());

            return view;
        }
        else
            return null;
    }

    public void setMyFileList(List<myFile> list)
    {
        this.list = list;
    }

}
