package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;

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
        // Nếu bạn có thuộc tính favorite (star), thì set ngôi sao vàng/xám
        holder.imgStar.setImageResource(word.isFavorite() ? R.drawable.ic_star : R.drawable.ic_star_border);
        holder.imgStar.setColorFilter(word.isFavorite() ? 0xFFFFEB3B : 0xFFB0BEC5);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvPhonetic, tvExplanation, tvExample;
        ImageView imgStar;
        CardView cardView;

        public WordViewHolder(View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            tvExplanation = itemView.findViewById(R.id.tvExplanation);
            tvExample = itemView.findViewById(R.id.tvExample);
            imgStar = itemView.findViewById(R.id.imgStar);
            cardView = (CardView) itemView;
        }
    }
}