package com.example.joseantonio.indicadoresmovil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Temas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//este muestra los indicadores del TEMA
//este muestra los indicadores del s
public Temas() {
    // Required empty public constructor

}

    private GridView mGridView;
    private ProgressBar mProgressBar;
    String id_anun,Titulo;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    String  NombreCompleto,seccion;
    Toolbar toolbar;
    TextView lista;
    private String FEED_URL = "http://plancolima.col.gob.mx/apis/get_indicadores_temas";
    private String FEED_URLs = "http://plancolima.col.gob.mx/apis/get_indicadores_temas";

    //private String FEED_URL = "http://10.10.42.9:8080/apis/get_indicadores_temas.php";
    // private String FEED_URLs = "http://10.10.42.9:8080/apis/get_indicadores_temas.php";



    FloatingActionButton fab;
    String name;
    String text;
    public String id_indica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tema);
      toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);


        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                GridItem item = (GridItem) parent.getItemAtPosition(position);
                final Intent intenta = new Intent(Temas.this,Scrolling_indicador.class);
                intenta.putExtra("id",v.getId());//esta funcion jala los id
                startActivity(intenta);


                //Start details activity
                mGridData.get(position);
            }
        });

        //sirve para el eliminar el contenido
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                agregar_favoritos(arg1.getId());
                return true;
            }
        });



        //RECIBO LOS PARAMETROS
        SharedPreferences prefe=getSharedPreferences("usuario", Context.MODE_PRIVATE);
        NombreCompleto=prefe.getString("mail","");



        SharedPreferences prefex=getSharedPreferences("busca", Context.MODE_PRIVATE);
        name=(prefex.getString("mail",""));

        seccion="temas";

        SharedPreferences preferencias=getSharedPreferences("menus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("back", seccion);
        editor.commit();

        //Start download
        Conexion();
        Busquedas busquedas = new Busquedas();

        String txt = busquedas.post(FEED_URLs,name);

        mProgressBar.setVisibility(View.VISIBLE);


        final String id_indica = String.valueOf(getIntent().getExtras().getInt("id"));//recibo el di lo convierto en int


        action(id_indica);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } // Habilitar up button

        regreso();

    }

    public void regreso(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent favoritos=new Intent(Temas.this,Indicadores.class);

                favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(favoritos);
                finish();
                onBackPressed();//para serrar

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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

                parametros.add(new BasicNameValuePair("email",String.valueOf(getIntent().getExtras().getInt("id"))));
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



    public void action(String id_indica){
        Log.d("PE",id_indica);
        ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.back);
        lista=(TextView)actionBar.getCustomView().findViewById(R.id.tema);
        String NAME="";

        int id=0;

        id=Integer.parseInt(id_indica);

        SharedPreferences preferencias=getSharedPreferences("tema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("id_tema",id_indica);
        editor.commit();


        if(id==1){
            NAME="Salud";

        }

        else
        if (id==2){

            NAME="Educación";

        }


        else
        if (id==3){

            NAME="Seguridad";

        }


        lista.setText(NAME);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME);

    }


    private  void  agregar_favoritos( final int id){
        //aqui se agrega a favoritos
        final String id_indica = String.valueOf(id);//recibo el di lo convierto en int

        String json="http://plancolima.col.gob.mx/apis/insert_favoritos";
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        StringRequest request=new StringRequest(Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parameters=new HashMap<String, String>();
                parameters.put("nombre", NombreCompleto);
                parameters.put("email",id_indica);


                return parameters;

            }
        };
        requestQueue.add(request);


        Toast.makeText(
                Temas.this,
                "Se agrego a favoritos",
                Toast.LENGTH_SHORT)
                .show();
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

                new Temas.AsyncHttpTask().execute(FEED_URL);

            }
        } else {
       /* No estas conectado a internet */
            mensaje();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void mensaje(){
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
        Toast.makeText(Temas.this, "No Tiene Favoritos  ", Toast.LENGTH_SHORT).show();
        regreso();
    }





}
