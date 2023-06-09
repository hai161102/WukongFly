package com.haiprj.haigame.wukongfly.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.haiprj.haigame.wukongfly.Const;
import com.haiprj.haigame.wukongfly.R;
import com.haiprj.haigame.wukongfly.base.utils.GameSharePreference;
import com.haiprj.haigame.wukongfly.databinding.LayoutColorBinding;
import com.haiprj.haigame.wukongfly.gamemodel.ColorModel;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ColorHolder> {
    private final Context context;
    private final List<ColorModel> list = new ArrayList<>();

    public ColorPickerAdapter(Context context) {
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     */
    @SuppressLint("NotifyDataSetChanged")
    public void update(List<ColorModel> colorModels) {
        this.list.clear();
        this.list.addAll(colorModels);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ColorPickerAdapter.ColorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorHolder(LayoutInflater.from(context).inflate(R.layout.layout_color, parent, false));
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override  instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ColorPickerAdapter.ColorHolder holder, int position) {
        holder.loadData(list.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ColorHolder extends RecyclerView.ViewHolder {
        LayoutColorBinding binding;
        public ColorHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }

        @SuppressLint("NotifyDataSetChanged")
        public void loadData(ColorModel colorModel) {
            int visInt = colorModel.isSelect() ? View.VISIBLE : View.GONE;
            if (colorModel.isSelect()) {
                GameSharePreference.getInstance().setInt(Const.PLAYER_COLOR, colorModel.getResColor());
            }
            binding.chooseColor.setVisibility(visInt);
            binding.colorPick.setCardBackgroundColor(context.getColor(colorModel.getResColor()));

            binding.getRoot().setOnClickListener(v -> {
                for (ColorModel model : list) {
                    model.setSelect(false);
                }
                colorModel.setSelect(true);
                notifyDataSetChanged();
            });
        }
    }
}
