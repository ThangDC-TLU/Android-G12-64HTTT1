package vn.edu.tlu.dinhcaothang.ezilish.activities;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Thêm import cho Retrofit
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tlu.dinhcaothang.ezilish.activities.OxfordResponse;
import vn.edu.tlu.dinhcaothang.ezilish.activities.OxfordApi;

public class AddWordActivity extends AppCompatActivity {
    private EditText etWord, etPhonetic, etExplanation, etExample, etMeaning;
    private Button btnAddWord;
    private ImageView btnBack;

    private String topicId;

    // Khai báo OxfordApi
    private OxfordApi oxfordApi;

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

        // Khởi tạo Retrofit & OxfordApi
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://od-api-sandbox.oxforddictionaries.com/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        oxfordApi = retrofit.create(OxfordApi.class);

        // Khi ấn back
        btnBack.setOnClickListener(v -> finish());

        // Tự động kiểm tra từ và sinh phonetic bằng Oxford API
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
                getPhoneticWithOxford(word);
            }
        });

        btnAddWord.setOnClickListener(v -> addWord());
    }

    // Hàm gọi Oxford API lấy phiên âm
    private void getPhoneticWithOxford(String word) {
        if (!word.matches("^[a-zA-Z]+$")) {
            etPhonetic.setText("");
            etPhonetic.setError("Invalid word!");
            Toast.makeText(this, "Từ không hợp lệ, vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        oxfordApi.getEnglishWord(word.toLowerCase()).enqueue(new Callback<OxfordResponse>() {
            @Override
            public void onResponse(Call<OxfordResponse> call, Response<OxfordResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().results != null && !response.body().results.isEmpty()) {
                    String ipa = null;
                    for (OxfordResponse.Result result : response.body().results) {
                        if (result.lexicalEntries != null) {
                            for (OxfordResponse.LexicalEntry lex : result.lexicalEntries) {
                                if (lex.entries != null) {
                                    for (OxfordResponse.Entry entry : lex.entries) {
                                        if (entry.pronunciations != null) {
                                            for (OxfordResponse.Pronunciation pro : entry.pronunciations) {
                                                if (pro.phoneticSpelling != null) {
                                                    ipa = pro.phoneticSpelling;
                                                    break;
                                                }
                                            }
                                        }
                                        if (ipa != null) break;
                                    }
                                }
                                if (ipa != null) break;
                            }
                        }
                        if (ipa != null) break;
                    }
                    if (ipa != null && !ipa.isEmpty()) {
                        etPhonetic.setText("/" + ipa + "/");
                        etPhonetic.setError(null);
                    } else {
                        etPhonetic.setText("");
                        etPhonetic.setError("No phonetic found!");
                        Toast.makeText(AddWordActivity.this, "Không tìm thấy phiên âm!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    etPhonetic.setText("");
                    etPhonetic.setError("Not a valid English word!");
                    Toast.makeText(AddWordActivity.this, "Không phải từ tiếng Anh hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OxfordResponse> call, Throwable t) {
                etPhonetic.setText("");
                etPhonetic.setError("Error connecting API!");
                Toast.makeText(AddWordActivity.this, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
            }
        });
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