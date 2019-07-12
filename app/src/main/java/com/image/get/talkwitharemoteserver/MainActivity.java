package com.image.get.talkwitharemoteserver;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;



public class MainActivity extends AppCompatActivity {
    
    private EditText editTextUser;
    private EditText editTextPretty;
    private Button buttonUser;
    private Button buttonPretty;
    private Spinner spinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String stringUser;
    private String stringPretty;
    private String ImageUrl;
    private String stringTag;
    private ImageView imageView;
    private TextView textView;
    private ProgressDialog progress;


 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        buttonUser = (Button) findViewById(R.id.userBotton);
        buttonUser.setVisibility(View.INVISIBLE);
        buttonPretty = (Button) findViewById(R.id.butonPRN);
        buttonPretty.setVisibility(View.INVISIBLE);
        textView = (TextView) findViewById(R.id.txtViewId);
        textView.setVisibility(View.INVISIBLE);
        spinner = (Spinner) findViewById(R.id.imgNameSpinner);
        spinner.setVisibility(View.INVISIBLE);
        imageView = (ImageView) findViewById(R.id.imgProfile);
        imageView.setVisibility(View.INVISIBLE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        stringTag = sharedPreferences.getString("user_tag", "TAG");
        stringTag = stringTag == null ? "" : stringTag;
        editTextUser = (EditText) findViewById(R.id.UserNameTxt);
        editTextUser.setVisibility(View.INVISIBLE);
        editTextPretty = (EditText) findViewById(R.id.editTextPRN);
        editTextPretty.setVisibility(View.INVISIBLE);
        buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyServer serverInterface = ServerHolder.getInstance().serverInterface;
                final String new_user = editTextUser.getText().toString();
                if (new_user.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Enter a valid username and hit the submit button!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Call<TokenResponse> call = serverInterface.getUserToken(new_user);

                    call.enqueue(new Callback<TokenResponse>() {
                        @Override
                        public void onResponse(Call<TokenResponse> call,
                                               Response<TokenResponse> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "code: " + String.valueOf(response.code() + ", try again!"),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                dataSet(response, new_user);
                                getUserDataFromServer();
                                editPrettyName();
                                chooseImageUrl();                            }
                        }
                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Error Occurred " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        if (stringTag.equals("TAG")) {
            editTextUser.setVisibility(View.VISIBLE);
            buttonUser.setVisibility(View.VISIBLE);
        }

        getUserDataFromServer();
        editPrettyName();
        chooseImageUrl();
    }

    private void enqueue2(Call<UserResponse> call)
    {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "code: " + String.valueOf(response.code() + ", try again!"),
                            Toast.LENGTH_LONG).show();
                    ImageUrl = "";
                    editor.putString("image_url", ImageUrl);
                    editor.apply();
                }
                else
                {
                    editor.putString("image_url", ImageUrl);
                    editor.apply();
                    imageView.setVisibility(View.VISIBLE);
                    Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + ImageUrl).into(imageView);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error Occurred " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                ImageUrl = "";
                editor.putString("image_url", ImageUrl);
                editor.apply();
            }
        });
    }

  

    private void enqueue3(Call<UserResponse> call)
    {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "code: " + String.valueOf(response.code() + ", try again!"),
                            Toast.LENGTH_LONG).show();
                } else {
                    User data = response.body().data;
                    stringPretty = data.pretty_name;
                    ImageUrl = data.image_url;
                    editor.putString("editTextPretty", stringPretty);
                    editor.putString("image_url", ImageUrl);
                    editor.apply();
                    stringUser = sharedPreferences.getString("user_name", "");
                    textView.setVisibility(View.VISIBLE);
                    welcomeMsg();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error Occurred " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    private void editPrettyName() {
        if (!stringTag.equals("TAG")) {
            editTextPretty.setVisibility(View.VISIBLE);
            buttonPretty.setVisibility(View.VISIBLE);
            prettyName();
        }
    }


    private void progressSetting(Response<UserResponse> response)
    {
        progress.dismiss();
        User data = response.body().data;
        stringPretty = data.pretty_name;
        ImageUrl = data.image_url;
        editor.putString("editTextPretty", stringPretty);
        editor.putString("image_url", ImageUrl);
        editor.apply();
        stringUser = sharedPreferences.getString("user_name", "");
        editTextUser.setVisibility(View.GONE);
        buttonUser.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }


    private void enqueue4(Call<UserResponse> call)
    {
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful()) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "code: " + String.valueOf(response.code() + ", try again!"),
                            Toast.LENGTH_LONG).show();
                } else {
                    progressSetting(response);

                    welcomeMsg();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Error Occurred " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                

            }
        });
    }

    private void welcomeMsg()
    {
        textView.setText((stringPretty == null || stringPretty.equals("")) ?
                "welcome, " + stringUser  : "welcome again, " + editTextPretty.getText().toString());
    }

    private void selectLis()
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                ImageUrl = "/images/" + selection + ".png";
                MyServer serverInterface = ServerHolder.getInstance().serverInterface;
                SetUserProfileImageRequest request = new SetUserProfileImageRequest();
                request.image_url = ImageUrl;
                Call<UserResponse> call = serverInterface.chooseProfileImage(request, "token "
                        + stringTag);
                enqueue2(call);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { return; }
        });
    }

    private void getUserDataFromServer() {
        if (!stringTag.equals("TAG")) {
            MyServer serverInterface = ServerHolder.getInstance().serverInterface;
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
            Call<UserResponse> call = serverInterface.getUserResponse("token " + stringTag);
            enqueue4(call);
        }
    }

    private void dataSet(Response<TokenResponse> response, String new_user)
    {
        String data = response.body().data;
        stringTag = data;
        editor.putString("user_tag", data);
        editor.putString("user_name", new_user);
        editor.apply();
        editTextUser.setVisibility(View.GONE);
        buttonUser.setVisibility(View.GONE);
    }
    
    private void chooseImageUrl() {
        if (!stringTag.equals("TAG"))
        {
            spinner.setVisibility(View.VISIBLE);
            String[] items = new String[]{"crab", "unicorn", "alien", "robot", "octopus", "frog"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            spinner.setAdapter(adapter);
            selectLis();
        }
    }

    private void prettyName()
    {
        buttonPretty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPrettyName = editTextPretty.getText().toString();
                if (newPrettyName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter a valid pretty name and hit the submit button!",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    MyServer serverInterface = ServerHolder.getInstance().serverInterface;
                    SetUserPrettyNameRequest request = new SetUserPrettyNameRequest();
                    request.pretty_name = newPrettyName;
                    Call<UserResponse> call = serverInterface.postPrettyName(request, "token " + stringTag);
                    enqueue3(call);
                }
            }
        });
    }

    public interface MyServer{

        @GET("/users/{user_name}/token")
        Call<TokenResponse> getUserToken(@Path("user_name") String userName);

        @GET("/user")
        Call<UserResponse> getUserResponse(@Header("Authorization") String token);

        @Headers({
                "Content-Type:application/json"
        })
        @POST("/user/edit/")
        Call<UserResponse> postPrettyName(@Body SetUserPrettyNameRequest request,
                                          @Header("Authorization") String token);

        @Headers({
                "Content-Type:application/json"
        })
        @POST("/user/edit/")
        Call<UserResponse> chooseProfileImage(@Body SetUserProfileImageRequest request,
                                              @Header("Authorization") String token);

    }

    public class SetUserPrettyNameRequest { 
        String pretty_name;
    }
    public class SetUserProfileImageRequest { 
        String image_url; 
    }
    public class TokenResponse {
        String data; 
    }
    public class User {
        String pretty_name;
        String image_url;
    }
    public class UserResponse { 
        User data;
    }


}
