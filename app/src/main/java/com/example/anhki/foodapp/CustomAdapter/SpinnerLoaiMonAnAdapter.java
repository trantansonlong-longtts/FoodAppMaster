package com.example.anhki.foodapp.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anhki.foodapp.DTO.LoaiMonAnDTO;

import java.util.List;

public class SpinnerLoaiMonAnAdapter extends BaseAdapter {
    private final Context context;
    private final List<LoaiMonAnDTO> loaiMonAnDTOList;
    private final LayoutInflater inflater;

    public SpinnerLoaiMonAnAdapter(Context context, List<LoaiMonAnDTO> loaiMonAnDTOList) {
        this.context = context;
        this.loaiMonAnDTOList = loaiMonAnDTOList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return loaiMonAnDTOList.size();
    }

    @Override
    public Object getItem(int position) {
        return loaiMonAnDTOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return loaiMonAnDTOList.get(position).getMaLoai();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Hiển thị cho item đã được chọn
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Hiển thị cho các item trong danh sách thả xuống
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        LoaiMonAnDTO loaiMonAnDTO = loaiMonAnDTOList.get(position);
        if (loaiMonAnDTO != null) {
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(loaiMonAnDTO.getTenLoai());
        }

        return view;
    }
}
