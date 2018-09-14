package com.example.com.zomato;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    //declare all require variables
    RecyclerView recylerView;
    MyAdapter myAdapter;
    MyTask myTask;
    LinearLayoutManager manager;
    ArrayList<Hotel> al;


    //create a class for recycler adapter

    public class MyTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("B42", "1");
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("B42", "2");
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                //Extra code  for Zomato
                con.setRequestProperty("Accept","application/json");
                con.setRequestProperty("user-key","cefe91da5252ed4e26db3ca4c9bb213e");


                //code of extra code

                InputStream is = con.getInputStream();
                InputStreamReader ir = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(ir);
                /*********************/
                String str = br.readLine();
                StringBuilder sb = new StringBuilder();
                while(str!=null){
                    sb.append(str);
                    str = br.readLine();
                }
                return sb.toString();//return json data of estratunts
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("B42", "3");

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("B42", "4");
            Log.d("B42","SERVER FECTH DATA"+s);

            try {
                JSONObject obj = new JSONObject(s);
                JSONArray arr = obj.getJSONArray("nearby_restaurants");
                for(int i=0;i<=arr.length();i++){

                    JSONObject temp = arr.getJSONObject(i);
                    JSONObject restaraunt = temp.getJSONObject("restaurant");
                    String hotel_name = restaraunt.getString("name");
                    JSONObject location = restaraunt.getJSONObject("location");
                    String hotel_locality = location.getString("locality");
                    String hotel_lat = location.getString("latitude");
                    String hotel_lon = location.getString("longitude");
                    String hotel_ofers = restaraunt.getString("cuisines");
                    String image_url = restaraunt.getString("thumb");
                    JSONObject usr = restaraunt.getJSONObject("user_rating");
                    String hotel_rating = usr.getString("aggregate_rating");
                    //NOW we got all detils ,let us insert into arry list

                    Hotel h = new Hotel(image_url,hotel_name,hotel_locality,hotel_ofers,hotel_lat,hotel_lon,hotel_rating);
                    al.add(h);
                    // NOw notify adapter
                    myAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my, container, false);
        recylerView = v.findViewById(R.id.recylcerview);
        manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        myAdapter = new MyAdapter();
        recylerView.setAdapter(myAdapter);
        recylerView.setLayoutManager(manager);
        // Now let us start async task
        myTask = new MyTask();
        al = new ArrayList<Hotel>();//add this line

        //now pass zomato web service url to asysc task for restraurants

        myTask.execute("https://developers.zomato.com/api/v2.1/geocode?lat=12.8984&lon=77.61987");

        return v;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row,viewGroup,false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Hotel h = al.get(i);
            viewHolder.tv1.setText(h.getHotel_name());//display hotel name on tv1
            viewHolder.tv2.setText(h.getHotel_locality());//locality on tv2
            viewHolder.tv3.setText(h.getHotel_offers());//offers on tv3
            viewHolder.rb1.setRating(Float.parseFloat(h.getHotel_rating()));
            //display image onto imageview using glide library

            Glide.with(getActivity()).load(h.getImage_url()).into(viewHolder.iv1);

        }

        @Override
        public int getItemCount() {
            return al.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView iv1;
            public TextView tv1,tv2,tv3;
            public CardView cv1;
            public RatingBar rb1;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                iv1 = itemView.findViewById(R.id.imageView1);
                tv1 = itemView.findViewById(R.id.textView1);
                tv2 = itemView.findViewById(R.id.textView2);
                tv3 = itemView.findViewById(R.id.textView3);
                rb1 = itemView.findViewById(R.id.ratingBar1);

            }
        }
    }
}
