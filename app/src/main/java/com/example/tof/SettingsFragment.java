package com.example.tof;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements CameraSettings {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isStaticRange = true;
    EditText rangeMinTextNumber;
    EditText rangeMaxTextNumber;
    EditText depthMaskTextNumber;
    RadioButton logRadioButton;
    CheckBox staticRangeCheckBox;
    CheckBox showBadAreaCheckBox;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        rangeMinTextNumber = view.findViewById(R.id.rangeMinTextNumber);
        rangeMaxTextNumber = view.findViewById(R.id.rangeMaxTextNumber);
        depthMaskTextNumber = view.findViewById(R.id.depthMaskTextNumber);
        logRadioButton = view.findViewById(R.id.logRadioButton);
        staticRangeCheckBox = view.findViewById(R.id.staticRangeCheckBox);
        showBadAreaCheckBox = view.findViewById(R.id.showBadAreaCheckBox);
        return view;
    }
    //onStaticRangeCheckBoxClicked

    public void onStaticRangeCheckBoxClicked(View view) {
        // Is the view now checked?
        isStaticRange = ((CheckBox) view).isChecked();
    }
    public boolean isStaticRange(){
        return staticRangeCheckBox.isChecked();
    }

    public int getRangeMin(){
        String strVal = rangeMinTextNumber.getText().toString();
        return Integer.parseInt(strVal);
    }
    public int getRangeMax(){
        String strVal = rangeMaxTextNumber.getText().toString();
        return Integer.parseInt(strVal);
    }

    public int getDepthMask() {
        String strVal = depthMaskTextNumber.getText().toString();
        return Integer.parseInt(strVal);
    }

    public boolean isLogScale() {
        return logRadioButton.isChecked();
    }

    @Override
    public boolean showbadArea() {
        return showBadAreaCheckBox.isChecked();
    }


}