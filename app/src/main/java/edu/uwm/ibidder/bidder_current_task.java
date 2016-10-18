package edu.uwm.ibidder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link bidder_current_task.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link bidder_current_task#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bidder_current_task extends android.support.v4.app.Fragment {


  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bidder_current_task, container, false);
    }


}
