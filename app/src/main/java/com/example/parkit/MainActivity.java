package com.example.parkit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";
    //https://git.heroku.com/still-hamlet-70336.git

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        findViewById(R.id.navPark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleParkDialogue();
            }
        });

        findViewById(R.id.navExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleExitDialogue();
            }
        });
    }

    private void handleExitDialogue() {
        View view = getLayoutInflater().inflate(R.layout.exit_dialogue, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();
        Button exitBtn = view.findViewById(R.id.exitVehicle);
        EditText vehicleNumber = view.findViewById(R.id.exitNumber);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehicleNumber", vehicleNumber.getText().toString());

                Call<exitResult> call = retrofitInterface.executeExit(map);
                call.enqueue(new Callback<exitResult>() {
                    @Override
                    public void onResponse(Call<exitResult> call, Response<exitResult> response) {
                        if(response.code()==200){
                            exitResult result = response.body();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle("Exited "+result.getVehicleNumber());
                            builder1.setMessage("Amount to be paid: Rs "+result.getAmount());

                            builder1.show();
                        }else if (response.code()==404){
                            Toast.makeText(MainActivity.this,
                                    "Vehicle Not Found",Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<exitResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void handleParkDialogue(){
        View view = getLayoutInflater().inflate(R.layout.parkit_dialogue,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();

        Button parkBtn = view.findViewById(R.id.parkIt);
        EditText vehicleNumber = view.findViewById(R.id.entryNumber);

        parkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehicleNumber",vehicleNumber.getText().toString());

                Call<parkResult> call = retrofitInterface.executeParkit(map);
                call.enqueue(new Callback<parkResult>() {
                    @Override
                    public void onResponse(Call<parkResult> call, Response<parkResult> response) {
                        if(response.code() == 200){

                            parkResult result = response.body();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle(result.getVehicleNumber());
                            builder1.setMessage("Parked at "+result.getSlot());

                            builder1.show();
                        }else if(response.code() == 404){
                            Toast.makeText(MainActivity.this,"No Slots available",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<parkResult> call, Throwable t) {
                        Toast.makeText(MainActivity.this,t.getMessage(),
                                Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

    }
}
