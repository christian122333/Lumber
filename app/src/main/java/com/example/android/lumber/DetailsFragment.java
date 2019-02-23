package com.example.android.lumber;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SinglePostFragment extends Fragment {
    private View singlePostFragmentView = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        singlePostFragmentView = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void showPost(String title, String content){
        TextView address = singlePostFragmentView.findViewById(R.id.post_address);
        TextView price = singlePostFragmentView.findViewById(R.id.post_price);
        TextView stories = singlePostFragmentView.findViewById(R.id.post_stories);
        TextView bedrooms = singlePostFragmentView.findViewById(R.id.post_bedrooms);
        TextView bathrooms = singlePostFragmentView.findViewById(R.id.post_bathrooms);
        TextView footage = singlePostFragmentView.findViewById(R.id.post_square_footage);
    }
}
