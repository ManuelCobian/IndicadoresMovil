package com.example.joseantonio.indicadoresmovil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joseantonio.indicadoresmovil.Adapters.GridViewAdapter;
import com.example.joseantonio.indicadoresmovil.Modelos.GridItem;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


//hace la busqueda
public class GridViewActivity extends ActionBarActivity {
    private static final String TAG = GridViewActivity.class.getSimpleName();

    private GridView mGridView;
    private ProgressBar mProgressBar;
    String id_anun,seccion;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private String FEED_URL = "http://plancolima.col.gob.mx/apis/get_busqueda";
    private String FEED_URLs = "http://plancolima.col.gob.mx/apis/get_busqueda";

    FloatingActionButton fab;
    String name;
    TextView lista;
    EditText buscar;
String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab=(FloatingActionButton)findViewById(R.id.fab);
        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_indicadores, mGridData);
        mGridView.setAdapter(mGridAdapter);



        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                final Intent intenta = new Intent(GridViewActivity.this,Scrolling_indicador.class);

                //Start details activity

                //RECIBO LOS PARAMETROS
                SharedPreferences prefe=getSharedPreferences("id_indica", Context.MODE_PRIVATE);
                prefe.getString("id",String.valueOf(v.getId()));

                intenta.putExtra("id",v.getId());//esta funcion jala los id
                startActivity(intenta);
                mGridData.get(position);

            }
        });





        seccion="buscar";

        SharedPreferences preferencias=getSharedPreferences("menus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("back", seccion);
        editor.commit();

        SharedPreferences prefe=getSharedPreferences("busca", Context.MODE_PRIVATE);
        name=(prefe.getString("mail",""));
        action(name);

        Log.d("fu",name);
        //Start download
       Conexion();
        Busquedas busquedas = new Busquedas();

        String txt = busquedas.post(FEED_URLs,name);

        mProgressBar.setVisibility(View.VISIBLE);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } // Habilitar up button


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
                //onBackPressed();//para serrar
            }
        });
    }



    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(FEED_URLs);
                List<NameValuePair> parametros = new ArrayList<NameValuePair>();

                parametros.add(new BasicNameValuePair("email",name));
                httppost.setEntity(new UrlEncodedFormEntity(parametros));

                HttpResponse httpResponse = httpclient.execute(httppost);

                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
//&                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI

            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {

                Retorno();
            }

            //Hide progressbar
            mProgressBar.setVisibility(View.GONE);
        }
    }


    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("anuncios");
            Log.d("arr",String.valueOf(posts));
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("titulo");
                id_anun = post.optString("id_anuncio");
                String curso = post.optString("curso");
                String alerta=post.optString("alerta");

                if(curso=="null"){
                    curso="No esta definida";
                }
                item = new GridItem();
                item.setTitle(title);
                item.setCourse(curso);
                item.setId(id_anun);
                item.setAlerta(alerta);
                //Log.d("alert",alerta);

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back(){


        Intent favoritos=new Intent(GridViewActivity.this,Ver_Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(favoritos);
        finish();



    }


    public void action(String Result){

        ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.back);
        lista=(TextView)actionBar.getCustomView().findViewById(R.id.tema);
        buscar=(EditText) actionBar.getCustomView().findViewById(R.id.busqueda);

        lista.setText("Resultados búsqueda " +Result);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);

    }

    private void Conexion() {

        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                tipoConexion1 = true;
            }
            if (mMobile.isConnected()) {
                tipoConexion2 = true;
            }

            if (tipoConexion1 == true || tipoConexion2 == true) {
               /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */

                new GridViewActivity.AsyncHttpTask().execute(FEED_URL);
            }
        } else {
       /* No estas conectado a internet */
            confirmacion();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void confirmacion(){
        Context context=this;
        AlertDialog.Builder aler=new AlertDialog.Builder(context)
                .setTitle("Sin Conexión")
                .setMessage("Revise Su Conexión a Internet ").setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        AlertDialog alertDialog=aler.create();
        alertDialog.show();
    }

    private void Retorno(){
        Toast.makeText(GridViewActivity.this, "No Se Encontraron Temas ", Toast.LENGTH_SHORT).show();
        back();
    }


}