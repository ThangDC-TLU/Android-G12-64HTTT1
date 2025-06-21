package vn.edu.tlu.dinhcaothang.ezilish.activities;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWordActivity extends AppCompatActivity {
    private EditText etWord, etPhonetic, etExplanation, etExample, etMeaning;
    private Button btnAddWord;
    private ImageView btnBack;

    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        etWord = findViewById(R.id.etWord);
        etPhonetic = findViewById(R.id.etPhonetic);
        etExplanation = findViewById(R.id.etExplanation);
        etExample = findViewById(R.id.etExample);
        etMeaning = findViewById(R.id.etMeaning);
        btnAddWord = findViewById(R.id.btnAddWord);
        btnBack = findViewById(R.id.btnBack);

        topicId = getIntent().getStringExtra("topicId");

        // Khi ấn back
        btnBack.setOnClickListener(v -> finish());

        // Tự động kiểm tra từ và sinh phonetic bằng AI (giả lập)
        etWord.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString().trim();
                if (word.isEmpty()) {
                    etPhonetic.setText("");
                    etPhonetic.setError(null);
                    return;
                }
                // Sử dụng AI (hoặc API) để kiểm tra từ và lấy phonetic
                getPhoneticWithAI(word);
            }
        });

        btnAddWord.setOnClickListener(v -> addWord());
    }

    // Hàm giả lập gọi AI, bạn có thể thay thế bằng API thực tế
    private void getPhoneticWithAI(String word) {
        // Chỉ kiểm tra ký tự tiếng Anh, bạn có thể dùng từ điển hoặc API thực tế
        if (!word.matches("^[a-zA-Z]+$")) {
            etPhonetic.setText("");
            etPhonetic.setError("Invalid word!");
            Toast.makeText(this, "Từ không hợp lệ, vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
        } else {
            // Ví dụ lấy phonetic bằng AI, ở đây giả lập bằng /word/
            // Thực tế bạn dùng API, ví dụ Oxford API hoặc AI model
            String phonetic = "/" + word.toLowerCase() + "/";
            etPhonetic.setText(phonetic);
            etPhonetic.setError(null);
        }
    }

    private void addWord() {
        String word = etWord.getText().toString().trim();
        String phonetic = etPhonetic.getText().toString().trim();
        String explanation = etExplanation.getText().toString().trim();
        String example = etExample.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        if (word.isEmpty() || phonetic.isEmpty() || explanation.isEmpty() || example.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference wordsRef = FirebaseDatabase.getInstance().getReference("words");
        String id = wordsRef.push().getKey();
        Word w = new Word(id, word, phonetic, meaning, example, explanation, topicId);

        wordsRef.child(id).setValue(w, (error, ref) -> {
            if (error == null) {
                Toast.makeText(this, "Word added!", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại
            } else {
                Toast.makeText(this, "Failed to add word!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}