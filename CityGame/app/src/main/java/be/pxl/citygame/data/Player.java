package be.pxl.citygame.data;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import be.pxl.citygame.CityGameApplication;
import be.pxl.citygame.R;

/**
 * Created by Christina on 21/01/2015.
 */
public class Player {

    private int id;
    private String username;
    private String email;
    private String realname;
    private String games;
    private Application application;

    private String dialogTitle;
    private String dialogContent;

    private static final int JOB_LOGIN = 0, JOB_REGISTER = 1, JOB_UPDATE = 2;
    private int job = 0;

    public Player(String username) {
        this.username = username;
    }

    public boolean checkLogin(Application application, String password) {
        this.application = application;
        this.dialogTitle = application.getString(R.string.login_progress_title);
        this.dialogContent = application.getString(R.string.login_progress_content);
        job = this.JOB_LOGIN;
    }

    private class GetRestData extends AsyncTask<String, Void, Void> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            this.dialog = new ProgressDialog(((CityGameApplication)application).getActivity());
            this.dialog.setTitle(dialogTitle);
            this.dialog.setMessage(dialogContent);
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {
            switch(job) {
                case JOB_LOGIN:
                    tryLogin(params[0]);
                    break;
            }
            return null;
        }
    }

    private boolean tryLogin(String password) {
        DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httpPost = new HttpPost(application.getString(R.string.webservice_url) + "player/login/" + username);
        httpPost.setHeader("Content-Type", "application/json");
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            httpPost.setEntity(new StringEntity(data.toString()));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            return statusCode == HttpStatus.SC_OK;
        } catch (IOException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        } catch (JSONException e) {
            Log.e(Player.class.toString(), e.getMessage(), e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(((CityGameApplication)application).getActivity());
        builder.setTitle(R.string.login_fail_title)
                .setMessage(R.string.login_fail_content)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

        return false;
    }
}