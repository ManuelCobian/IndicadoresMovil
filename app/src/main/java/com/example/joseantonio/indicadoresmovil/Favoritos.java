package com.example.joseantonio.indicadoresmovil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

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

/**
 * Created by Luis  Manuel cobian on 18/01/2017.
 */

public class Favoritos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GridView mGridView;
    private ProgressBar mProgressBar;
    String id_anun;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private String FEED_URL = "http://plancolima.col.gob.mx/apis/get_favoritos";
    private String FEED_URLs = "http://plancolima.col.gob.mx/apis/get_favoritos";
    String text,id_fav,id_indica;
    EditText buscar;
    String name,pass,seccion,pigs;
    private Session session;
    TextView user, txtNombreCompleto;
    NavigationView navigationView;
    public  Favoritos(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        session = new Session(this);
        ShowNotif();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        buscar=(EditText)findViewById(R.id.buscar);

        //RECIBO LOS PARAMETROS
        SharedPreferences prefe=getSharedPreferences("usuario", Context.MODE_PRIVATE);
        name=prefe.getString("mail","");
        pass=prefe.getString("pass","");

        seccion="favoritos";

        SharedPreferences preferencias=getSharedPreferences("menus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("back", seccion);
        editor.commit();


        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_favoritos, mGridData);
        mGridView.setAdapter(mGridAdapter);
        //Start download
        Conexion();

        httpHandler handler = new httpHandler();

        String txt = handler.post(FEED_URLs,name,"no");

        mProgressBar.setVisibility(View.VISIBLE);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                final Intent intenta = new Intent(Favoritos.this,Scrolling_indicador.class);

                //Start details activity

                //RECIBO LOS PARAMETROS
                SharedPreferences prefe=getSharedPreferences("id_indica", Context.MODE_PRIVATE);
                prefe.getString("id",String.valueOf(v.getId()));

                intenta.putExtra("id",v.getId());//esta funcion jala los id


                startActivity(intenta);
                mGridData.get(position);

            }
        });
        //sirve para el eliminar el contenido
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {

                 id_indica= String.valueOf(arg1.getId());//id_favoritos
                eliminar(id_indica,id_fav);
                //Log.d("v",String.valueOf(arg1.getId()));


                //set the image as wallpaper
                return true;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //inserto el nombre en el nav
        View header=navigationView.getHeaderView(0);
        txtNombreCompleto = (TextView) header.findViewById(R.id.txtNombreCompleto);



        final ImageView user=(ImageView)header.findViewById(R.id.User);

//cargar imagen
        RequestQueue requestQueues;
        String json = "http://plancolima.col.gob.mx/apis/get_login";


        requestQueues = Volley.newRequestQueue(getApplicationContext());

        StringRequest request=new StringRequest(com.android.volley.Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject o = new JSONObject(response);

                    JSONArray a = o.getJSONArray("clientes");
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject contacto=a.getJSONObject(i);
                        String nombre=contacto.getString("nombre");
                        String cargo=contacto.getString("email");
                        String email=contacto.getString("cargo");
                        String val=contacto.getString("imagen");

                        txtNombreCompleto.setText(nombre);
                        Picasso.with(Favoritos.this)
                                .load(val)
                                .into(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parameters=new HashMap<String, String>();
                Bundle bundle = getIntent().getExtras();
                String id="1";
                parameters.put("email",name);
                parameters.put("password",pass);
                return parameters;

            }
        };
        requestQueues.add(request);//cargo la imagen


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favoritos, menu);
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
            back_inicio();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            back_inicio();
        } else if (id == R.id.nav_gallery) {
            back_inicio();
        } else if (id == R.id.nav_slideshow) {
            back_favoritos();
        }
        else if (id == R.id.nav_aactualizacion) {
               back_updates();
            }
         else if (id == R.id.nav_share) {

            //aqui cierras sesion
            logout();
        }
        else if(id==R.id.nav_temas){
            back_temas();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                // Log.d(TAG, e.getLocalizedMessage());
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


            JSONArray posts = response.optJSONArray("favoritos");

            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("titulo");
                id_anun = post.optString("id_anuncio");//id indicdor
                String curso = post.optString("curso");
                id_fav=post.optString("id_fav");//id favorito
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

    public void back_inicio(){
        Intent favoritos=new Intent(Favoritos.this,Ver_Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(favoritos);
        finish();
    }

    public void back_login(){
        Intent favoritos=new Intent(Favoritos.this,MainActivity.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(favoritos);
        finish();
    }
    public void back_favoritos(){
        Intent favoritos=new Intent(Favoritos.this,Favoritos.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(favoritos);
        finish();
    }

    public  void back_temas(){

        Intent favoritos=new Intent(Favoritos.this,Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(favoritos);
        finish();

    }

    public  void back_updates(){

        Intent favoritos=new Intent(Favoritos.this,UpdatesActivity.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(favoritos);
        finish();

    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        back_login();
    }


    //elimina favoritos
        public  void eliminar(final String id_feo,String id_favor){
        RequestQueue requestQueue;

        //RECIBO LOS PARAMETROS
        SharedPreferences prefe=getSharedPreferences("usuario", Context.MODE_PRIVATE);
        final String NombreCompleto=prefe.getString("mail","");

      //  Log.d("USER",NombreCompleto);
       // Log.d("id_indica",id_favor);

        String json="http://plancolima.col.gob.mx/apis/elimina_favoritos";

        String eliminar="http://10.10.42.9:8080/apis/elimina_favoritos.php";
        requestQueue= Volley.newRequestQueue(getApplicationContext());


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
                Bundle bundle = getIntent().getExtras();
                parameters.put("id",id_feo);//es el id de indicador
                parameters.put("id_fav",NombreCompleto);


                return parameters;

            }
        };
        requestQueue.add(request);
        Toast.makeText(Favoritos.this,"Se Elimino",Toast.LENGTH_SHORT).show();
        Intent i=new Intent(this,Favoritos.class);

        startActivity(i);
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

                new Favoritos.AsyncHttpTask().execute(FEED_URL);


            }
        } else {
       /* No estas conectado a internet */
            mensaje();
            mProgressBar.setVisibility(View.INVISIBLE);
            //logout();
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
        Toast.makeText(Favoritos.this, "No Tiene Favoritos  ", Toast.LENGTH_SHORT).show();
        back_temas();
    }

    public String  ShowNotif(){
        RequestQueue requestQueue;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        String json="http://10.10.42.9:8080/apis/count_alertas.php";
        StringRequest request=new StringRequest(Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject o = new JSONObject(response);

                    Log.d("dates",String.valueOf(response));
                    JSONArray a = o.getJSONArray("registros");
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject contacto=a.getJSONObject(i);

                        pigs =contacto.getString("not");

                        if (!pigs.equals("0")) {
                            navigationView.getMenu().getItem(4).setChecked(true).setTitle("Actualizaciones"+" "+pigs);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>parameters=new HashMap<String, String>();
                Bundle bundle = getIntent().getExtras();
                String id="1";
                parameters.put("id",id);


                return parameters;

            }
        };
        requestQueue.add(request);
        return pigs;
    }



}

