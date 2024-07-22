package com.bedetaxi.bedetaxidriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDataFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<PropertyInfo> Info;
    WebAPI api;
    String output;
    public String UserPhone="";
    public String DriverID="";
    SharedPreferencesManager sharedPreferencesManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDataFragment newInstance(String param1, String param2) {
        UserDataFragment fragment = new UserDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user_data, container, false);
        Bundle bundle = getArguments();
        String userName = bundle.getString("userName");
        String Dest="";
        String pickUp="";
        final String userPhone = bundle.getString("userPhone");
        UserPhone=userPhone;
        if(bundle.getString("PickupPoint")!=null ||bundle.getString("DestinationPoint")!=null ) {
             pickUp = bundle.getString("PickupPoint");
             Dest = bundle.getString("DestinationPoint");
        }



        TextView t = (TextView) rootView.findViewById(R.id.name);
        TextView phone = (TextView) rootView.findViewById(R.id.phone);

        TextView pick = (TextView) rootView.findViewById(R.id.pick);
        TextView dest = (TextView) rootView.findViewById(R.id.dis);
        Button cancel = (Button) rootView.findViewById(R.id.cancel);
        t.setText(userName);
        phone.setText(userPhone);
        pick.setText(pickUp);
        dest.setText(Dest);
        Button button = (Button) rootView.findViewById(R.id.call);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              new  SendNotification().execute();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(rootView.getContext());
                // Setting Dialog Message
                alertDialog.setMessage("هل انت متأكد بانك تريد إلغاء الطلب ؟  ");
                // On pressing Settings button
                alertDialog.setPositiveButton("نعم",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                            /*
                                            IsInRequest = false;
                                             Info = getCancelInfo(hashMap.get("orderID"));
                                            WebAPI api = new WebAPI(mapFragment.getContext(),"cancelRequest",Info);
                                            String output = api.call();

                                            Intent i = new Intent(MainActivity.this, cancel.class);
                                            startActivity(i);
                                            */
                                dialog.cancel();
                                if (!checkNetworkConnection()) {
                                    Toast.makeText(getActivity().getApplicationContext(), "الرجاء فحص الاتصال بالانترنت", Toast.LENGTH_LONG).show();

                                }else
                                    new CancelOrder().execute();
                            }
                        });

                // on pressing cancel button
                alertDialog.setNegativeButton("لا",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                // Showing Alert Message
                alertDialog.show();

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    public List<PropertyInfo> getSnedNotificationDetails (String id,String userPhone){
        List<PropertyInfo> info = new ArrayList<PropertyInfo>();
        PropertyInfo DriverID = new PropertyInfo();
        DriverID.setName("DriverID");
        DriverID.setValue(id);// Generally array index starts from 0 not 1
        DriverID.setType(String.class);

        info.add(DriverID);
        PropertyInfo UserPhone = new PropertyInfo();
        UserPhone.setName("UserPhone");
        UserPhone.setValue(userPhone);// Generally array index starts from 0 not 1
        UserPhone.setType(String.class);

        info.add(UserPhone);

        return info;
    }



    public List<PropertyInfo> getCancelInfo (String id){
        List<PropertyInfo> info = new ArrayList<PropertyInfo>();
        PropertyInfo orderID = new PropertyInfo();
        orderID.setName("OrderID");
        orderID.setValue(id);// Generally array index starts from 0 not 1
        orderID.setType(String.class);

        info.add(orderID);
        return info;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class CancelOrder extends AsyncTask<Void,Void,String> {
        ProgressDialog progressDialog;
        String My_Output;

        @Override
        protected String doInBackground(Void... params) {
            MainActivity.isInRequest = false;
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(MainActivity.context);

            String orderId = sharedPreferencesManager.getOrder();
            Info = getCancelInfo(orderId);
            api = new WebAPI(MainActivity.context, "cancelRequest", Info);
            output = api.call_request();
            My_Output = output;
            return My_Output;
        }


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.context);
            progressDialog.setTitle("تحميل...");
            progressDialog.setMessage("الرجاء الانتظار ... ");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        @Override
        protected void onPostExecute(String s) {
            if (My_Output.trim().isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.context, "خطأ بالاتصال", Toast.LENGTH_LONG).show();

            } else {
              //  MainActivity.fragmentTransaction.remove(MainActivity.fragment);
              //  MainActivity.fragmentTransaction.commit();
                if(((MainActivity)getActivity()).timer!=null)
                ((MainActivity)getActivity()).FinishRecorder();




                sharedPreferencesManager = new SharedPreferencesManager(MainActivity.context);
                sharedPreferencesManager.setAvailability(true);
                MainActivity.taxiStatus = true;
                MainActivity.updateAvailabltyButton();
                MainActivity.availableButton.setEnabled(true);
                getActivity().getFragmentManager().beginTransaction().remove(UserDataFragment.this).commit();




            }


        }
    }


    class SendNotification extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String output;


        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> props;
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(MainActivity.context);
            String DriverID = sharedPreferencesManager.getUserID();
props = getSnedNotificationDetails(DriverID,UserPhone);
            WebAPI webAPI = new WebAPI(MainActivity.context,"sendArrivalMessage",props);
            output = webAPI.call_request();
            return output;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.context);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please Wait ... ");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (output == null || output.trim().isEmpty()){

                showAlertDialog("خطأ","لم نستطلع الاتصال بالسيرفر , يرجى التأكد من الاتصال بالانترنت");
            }else {

                JSONArray data = null;
                try {
                    data = new JSONArray(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String status = null;
                try {
                    status = data.getJSONObject(0).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equalsIgnoreCase("success")) {
                    Toast.makeText(MainActivity.context, "success", Toast.LENGTH_LONG).show();
                    ((MainActivity)getActivity()).RecorderRun();

                }else if(status.equalsIgnoreCase("failed"))
                {
                    Toast.makeText(MainActivity.context, "failed", Toast.LENGTH_LONG).show();

                }
            }


        }
    }
    public void showAlertDialog (String title,String message){

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "حسناً",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }



}
