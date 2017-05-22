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
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
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

public class UpdatesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int id;//para hacer algo  con push
    private GridView mGridView;
    private ProgressBar mProgressBar;
    String id_anun,total;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    String seccion;
    private String FEED_URL = "http://10.10.42.9:8080/apis/get_indicadores.php";
    private String FEED_URLs = "http://10.10.42.9:8080/apis/get_indicadores.php";
    NavigationView navigationView;
   // private String FEED_URL = "http://plancolima.col.gob.mx/apis/get_indicadores";
    //private String FEED_URLs = "http://plancolima.col.gob.mx/apis/get_indicadores";
    String  NombreCompleto,pass;
    TextView user, txtNombreCompleto;
    private Session session;
    EditText buscar;

    String indica,activar;

    TextView lista;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        final String id_indica = String.valueOf(getIntent().getExtras().getInt("id"));//recibo el di lo convierto en int
        setContentView(R.layout.activity_updates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        action();


        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_indicadores, mGridData);
        mGridView.setAdapter(mGridAdapter);

        seccion="lista";

        SharedPreferences preferencias=getSharedPreferences("menus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("back", seccion);
        editor.commit();

        //Start download
       Conexion();
        httpHandler handler = new httpHandler();

        //String txt = handler.post(FEED_URLs,busqueda,indica);

        mProgressBar.setVisibility(View.VISIBLE);

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


                GridItem item = (GridItem) parent.getItemAtPosition(position);
                final Intent intenta = new Intent(UpdatesActivity.this,Scrolling_indicador.class);
                intenta.putExtra("id",v.getId());//esta funcion jala los id
                startActivity(intenta);
              eliminar_leido(v.getId());

                //Start details activity
                mGridData.get(position);

            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();drawer.setDrawerListener(toggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);






        //RECIBO LOS PARAMETROS
        SharedPreferences prefe=getSharedPreferences("usuario", Context.MODE_PRIVATE);
        NombreCompleto=prefe.getString("mail","");
        pass=prefe.getString("pass","");

        //inserto el nombre en el nav
        View header=navigationView.getHeaderView(0);
        txtNombreCompleto = (TextView) header.findViewById(R.id.txtNombreCompleto);

        txtNombreCompleto.setText(NombreCompleto);
        final ImageView user=(ImageView)header.findViewById(R.id.users);
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

                        Picasso.with(UpdatesActivity.this)
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
                parameters.put("email",NombreCompleto);
                parameters.put("password",pass);
                return parameters;

            }
        };
        requestQueues.add(request);
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
        getMenuInflater().inflate(R.menu.updates, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
         id = item.getItemId();//este ocuco

        if (id == R.id.nav_camera) {
            // Handle the camera action
            back_inicio();

        } else if (id == R.id.nav_gallery) {
            back_inicio();

        } else if (id == R.id.nav_fav) {
            back_favoritos();

        }
        else if (id == R.id.nav_aactualizacion) {
            back_updates();

        }
        else if (id == R.id.nav_share) {
            session = new Session(this);
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

                parametros.add(new BasicNameValuePair("email",""));
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
                    back_inicio();

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
            JSONArray posts = response.optJSONArray("anuncios");
            Log.d("arr",String.valueOf(posts));
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("titulo");
                id_anun = post.optString("id_anuncio");
                String curso = post.optString("curso");
                String alerta=post.optString("alerta");
                total=alerta;
                if(curso=="null"){
                    curso="No esta definida";
                }
                item = new GridItem();
                item.setTitle(title);
                item.setCourse(curso);
                item.setId(id_anun);
                item.setAlerta("secundary");
                item.setTotal(alerta);
                //Log.d("alert",alerta);

                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void back_inicio(){
        Intent favoritos=new Intent(UpdatesActivity.this,Ver_Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(favoritos);
        finish();
    }

    public void back_login(){
        Intent favoritos=new Intent(UpdatesActivity.this,MainActivity.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(favoritos);
        finish();
    }
    public void back_favoritos(){
        Intent favoritos=new Intent(UpdatesActivity.this,Favoritos.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(favoritos);
        finish();
    }

    public  void back_temas(){

        Intent favoritos=new Intent(UpdatesActivity.this,Indicadores.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(favoritos);
        finish();

    }

    public  void back_updates(){

        Intent favoritos=new Intent(UpdatesActivity.this,UpdatesActivity.class);

        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(favoritos);
        finish();

    }



    private void logout(){
        session.setLoggedin(false);
        finish();
        back_login();
    }


    public void action(){

        ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setCustomView(R.layout.miactionbar);
        lista=(TextView)actionBar.getCustomView().findViewById(R.id.tema);
        buscar=(EditText) actionBar.getCustomView().findViewById(R.id.busqueda);
        buscar.setFocusable(true);



        lista.setText("Actualizaciones");

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

                new UpdatesActivity.AsyncHttpTask().execute(FEED_URL);
                if(mGridAdapter.isEmpty()){
                    Retorno();
                }




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
        Toast.makeText(UpdatesActivity.this, "No hay actualizacion ", Toast.LENGTH_SHORT).show();
        back_temas();
    }


    private  void  eliminar_leido( final int id){
        //aqui se agrega a favoritos
        final String id_indica = String.valueOf(id);//recibo el di lo convierto en int

        if(id<0){
            Retorno();
        }
        String json="http://10.10.42.9:8080/apis/elimina_favoritos.php";
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
                parameters.put("id",id_indica);

                return parameters;

            }
        };
        requestQueue.add(request);



    }



}
