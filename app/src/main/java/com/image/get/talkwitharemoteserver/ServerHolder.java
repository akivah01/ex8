package com.image.get.talkwitharemoteserver;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHolder {

    private static ServerHolder inst = null;
    public final MainActivity.MyServer serverInterface;

    private ServerHolder(MainActivity.MyServer serverInterface) {
        this.serverInterface = serverInterface;
    }

    public synchronized static ServerHolder getInstance(){
        if(inst != null) {
            return inst;
        }
        else {
            OkHttpClient okHC = new OkHttpClient.Builder().build();

            Retrofit retrofit = new Retrofit.Builder().client(okHC)
                    .baseUrl("http://hujipostpc2019.pythonanywhere.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            inst = new ServerHolder(retrofit.create(MainActivity.MyServer.class));
            return inst;
        }
    }

}

