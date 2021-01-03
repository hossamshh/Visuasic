package com.example.visuasic.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visuasic.R;
import com.example.visuasic.model.Entities.ColorCommand;
import com.example.visuasic.viewModel.ControlViewModel;

import java.util.List;

public class ColorCommandsAdapter extends RecyclerView.Adapter<ColorCommandsAdapter.ColorCommandViewHolder> {
    private final LayoutInflater mInflater;
    private List<ColorCommand> colorCommands;

    private ControlViewModel controlViewModel;
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;

    public ColorCommandsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ColorCommandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_item, parent, false);
        return new ColorCommandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorCommandViewHolder holder, int position) {
        if (colorCommands != null) {
            final ColorCommand current = colorCommands.get(position);

            Drawable drawable = holder.colorPrev.getBackground();
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrappedDrawable, current.getRgb() == 0xffffff? 0xf2f2f2: current.getRgb());

            holder.command.setText(current.getCommand());

            holder.clickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int r = Color.red(current.getRgb()) / 85;
                    int g = Color.green(current.getRgb()) /85;
                    int b = Color.blue(current.getRgb()) / 85;

                    redSeekBar.setProgress(r);
                    greenSeekBar.setProgress(g);
                    blueSeekBar.setProgress(b);

                    controlViewModel.setCurrentColor(r, g, b);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controlViewModel.deleteColorCommand(current);
                }
            });
        } else {
            holder.colorPrev.setBackgroundColor(0);
            holder.command.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (colorCommands != null)
            return colorCommands.size();
        else return 0;
    }

    public void setObjects(List<ColorCommand> colorCommands, ControlViewModel controlViewModel, SeekBar redSeekBar, SeekBar greenSeekBar, SeekBar blueSeekBar) {
        this.colorCommands = colorCommands;
        notifyDataSetChanged();

        this.controlViewModel = controlViewModel;
        this.redSeekBar = redSeekBar;
        this.greenSeekBar = greenSeekBar;
        this.blueSeekBar = blueSeekBar;
    }

    public class ColorCommandViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout clickLayout;
        private View colorPrev;
        private TextView command;
        private ImageButton deleteButton;


        public ColorCommandViewHolder(@NonNull View itemView) {
            super(itemView);
            clickLayout = itemView.findViewById(R.id.clickToSelect);
            colorPrev = itemView.findViewById(R.id.colorBox);
            command = itemView.findViewById(R.id.colorPhrase);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
