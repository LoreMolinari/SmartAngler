package com.smartangler.smartangler.ui.sessions;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.smartangler.smartangler.R;
import com.smartangler.smartangler.SmartAnglerSessionHelper;

import java.util.List;

public class PhotoFrame extends DialogFragment {

    private static final String ARG_SESSION_ID = "session_id";
    private static final int MAX_PHOTOS_PER_ROW = 2;
    private static final int MAX_ROWS = 3;
    private boolean hasPhotos = false;

    public static PhotoFrame newInstance(String sessionId) {
        PhotoFrame fragment = new PhotoFrame();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_ID, sessionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String sessionId = getArguments().getString(ARG_SESSION_ID);
        List<Object[]> photos = SmartAnglerSessionHelper.loadPhotosForSession(requireContext(), sessionId);

        if (photos.isEmpty()) {
            Toast.makeText(requireContext(), "No Photos", Toast.LENGTH_SHORT).show();
            dismiss();
            return null;
        }

        hasPhotos = true;
        View view = inflater.inflate(R.layout.photo_frame, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPhotos);

        int spanCount = Math.min(photos.size(), MAX_PHOTOS_PER_ROW);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        PhotoAdapter adapter = new PhotoAdapter(photos);
        recyclerView.setAdapter(adapter);

        recyclerView.setNestedScrollingEnabled(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustDialogSize();
    }

    private void adjustDialogSize() {
        Dialog dialog = getDialog();
        if (dialog != null && getView() != null) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewPhotos);
            if (recyclerView != null && recyclerView.getAdapter() != null) {

                int dialogWidth = (int) (screenWidth * 0.9);
                int maxHeight = (int) (screenHeight * 0.8);

                dialog.getWindow().setLayout(dialogWidth, maxHeight);
            }
        }
    }


    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
        private final List<Object[]> photos;

        PhotoAdapter(List<Object[]> photos) {
            this.photos = photos;
        }

        @NonNull
        @Override
        public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
            Object[] photo = photos.get(position);
            byte[] imageData = (byte[]) photo[2];

            Glide.with(holder.itemView.getContext())
                    .load(imageData)
                    .centerCrop()
                    .into(holder.imageView);
        }


        @Override
        public int getItemCount() {
            return photos.size();
        }

        class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            PhotoViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageViewPhoto);
            }
        }
    }
}
