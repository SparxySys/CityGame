package be.pxl.citygame;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import be.pxl.citygame.data.GameContent;
import be.pxl.citygame.data.Player;
import be.pxl.citygame.data.database.GameDB;
import be.pxl.citygame.data.database.GameDbHelper;
import be.pxl.citygame.providers.Providers;



public class MainActivity extends ActionBarActivity {

    private static final int PRIMARY_CONTENT_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Loads the providers that have been declared in settings.xml
        Providers.load(getApplication());

        ((CityGameApplication)getApplicationContext()).setActivity(this);

        GameDbHelper helper = new GameDbHelper(getApplicationContext());
        SQLiteDatabase sqlDb = helper.getReadableDatabase();
        Cursor cur = sqlDb.query(GameDB.Games.TABLE_NAME, null, null, null, null, null, null, null);

        Player offlinePlayer = new Player("!!offline", getApplication());

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            int id = 0;
            boolean completed = false;
            int score = 0;
            for(int i = 0; i < cur.getColumnCount(); i++) {
                if( cur.getColumnName(i).equals(GameDB.Games.COL_GID) ) {
                    id = cur.getInt(i);
                } else if( cur.getColumnName(i).equals(GameDB.Games.COL_SCORE) ) {
                    score = cur.getInt(i);
                } else if( cur.getColumnName(i).equals(GameDB.Games.COL_COMPLETED) ) {
                    completed = cur.getInt(i) == 1;
                }
            }

            GameContent content = new GameContent("localdata");
            content.setId(id);
            content.setCompleted(completed);
            content.setScore(score);

            offlinePlayer.getGames().add(content);
        }
        cur.close();
    }

    public void handleBtnStart(View v) {
        startGame(PRIMARY_CONTENT_ID);
    }

    public void startGame(int id) {
        // TODO: Christina gaat dit doen: Check if game is completed or was interrupted, and block/resume game
        // Download data
        Providers.getGameContentProvider().getGameContentById(id);

        // Switch to next activity
        CityGameApplication context = (CityGameApplication) getApplicationContext();
        Intent intent = new Intent(context, NextLocationActivity.class);
        intent.putExtra("gameId", id);
        intent.putExtra("questionId", 0);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scanQR(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        //customize
        //integrator.addExtra("PROMPT_MESSAGE", "SCANNER_START");
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null)
        {
            // QR code should contain application name + gamecontent  in JSON
            String contents = result.getContents();
            if(contents != null)
            {
                try {
                    JSONObject object = new JSONObject(contents);
                    String app = object.getString("appname");
                    if( app.equals("be.pxl.citygame") ) {
                        int gameContentId = object.getInt("gameContentId");
                        startGame(gameContentId);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.QR_invalid, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.QR_invalid, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void goToLogin(View v)
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}

