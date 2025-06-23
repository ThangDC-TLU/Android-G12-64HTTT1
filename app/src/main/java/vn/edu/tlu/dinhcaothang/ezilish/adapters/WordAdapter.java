package vn.edu.tlu.dinhcaothang.ezilish.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.models.Word;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
    private List<Word> wordList;

    public WordAdapter(List<Word> wordList) {
        this.wordList = wordList;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_card, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.tvWord.setText(word.getEnglish());
        holder.tvPhonetic.setText(word.getPhonetic());
        holder.tvExplanation.setText(word.getExplanation());
        holder.tvExample.setText(word.getExample());
        holder.tvVietnamese.setText(word.getMeaning() == null ? "" : word.getMeaning());

        // Nếu bạn có thuộc tính favorite (star), thì set ngôi sao vàng/xám
        holder.imgStar.setImageResource(word.isFavorite() ? R.drawable.ic_star : R.drawable.ic_star_border);
        holder.imgStar.setColorFilter(word.isFavorite() ? 0xFFFFEB3B : 0xFFB0BEC5);

        // Reset trạng thái flip về mặt trước khi bind lại view
        holder.layoutFront.setVisibility(View.VISIBLE);
        holder.layoutBack.setVisibility(View.GONE);
        holder.frameFlip.setRotationY(0);

        holder.frameFlip.setOnClickListener(v -> {
            if (holder.layoutFront.getVisibility() == View.VISIBLE) {
                holder.frameFlip.animate()
                        .rotationY(90)
                        .setDuration(150)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.layoutFront.setVisibility(View.GONE);
                                holder.layoutBack.setVisibility(View.VISIBLE);
                                holder.frameFlip.setRotationY(-90);
                                holder.frameFlip.animate()
                                        .rotationY(0)
                                        .setDuration(150)
                                        .setListener(null)
                                        .start();
                            }
                        }).start();
            } else {
                holder.frameFlip.animate()
                        .rotationY(90)
                        .setDuration(150)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.layoutFront.setVisibility(View.VISIBLE);
                                holder.layoutBack.setVisibility(View.GONE);
                                holder.frameFlip.setRotationY(-90);
                                holder.frameFlip.animate()
                                        .rotationY(0)
                                        .setDuration(150)
                                        .setListener(null)
                                        .start();
                            }
                        }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvPhonetic, tvExplanation, tvExample, tvVietnamese;
        ImageView imgStar;
        CardView cardView;
        LinearLayout layoutFront, layoutBack;
        FrameLayout frameFlip;

        public WordViewHolder(View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvExplanation = itemView.findViewById(R.id.tvExplanation);
            tvExample = itemView.findViewById(R.id.tvExample);
            imgStar = itemView.findViewById(R.id.imgStar);
            tvVietnamese = itemView.findViewById(R.id.tvVietnamese);
            cardView = (CardView) itemView;
            frameFlip = itemView.findViewById(R.id.frameFlip);
            layoutFront = itemView.findViewById(R.id.layoutFront);
            layoutBack = itemView.findViewById(R.id.layoutBack);
        }
    }
}