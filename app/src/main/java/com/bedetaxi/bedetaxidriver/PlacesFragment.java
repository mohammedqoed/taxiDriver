package com.bedetaxi.bedetaxidriver;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlacesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlacesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView a;
    TextView b;
    TextView c;
    TextView d;

    TextView aa;
    TextView bb;
    TextView cc;
    TextView dd;

    SharedPreferencesManager sharedPreferencesManager;
    Firebase distenation;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PlacesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlacesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlacesFragment newInstance(String param1, String param2) {
        PlacesFragment fragment = new PlacesFragment();
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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_places, container, false);
        sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        distenation = new Firebase("https://taxihere.firebaseio.com/Drivers/"+sharedPreferencesManager.getUserID()+"/Tracking/manualDestination");
        a = (TextView) root.findViewById(R.id.a);
        b = (TextView) root.findViewById(R.id.b);
        c = (TextView) root.findViewById(R.id.c);
        d = (TextView) root.findViewById(R.id.d);

        aa = (TextView) root.findViewById(R.id.aa);
        bb = (TextView) root.findViewById(R.id.bb);
        cc = (TextView) root.findViewById(R.id.cc);
        dd = (TextView) root.findViewById(R.id.dd);

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(a.getText().toString());
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(b.getText().toString());
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(c.getText().toString());
            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(d.getText().toString());
            }
        });

        aa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(aa.getText().toString());
            }
        });
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(bb.getText().toString());
            }
        });
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(cc.getText().toString());
            }
        });
        dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDestination(dd.getText().toString());
            }
        });



        return root;
    }
    public void getDestination(String text){
        distenation.setValue(text);
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        ((MainActivity)getActivity()).startRecorder.setImageResource(R.drawable.close);
        ((MainActivity)getActivity()).status=false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
