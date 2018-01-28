package com.example.gebruiker.sportapp.Homepage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gebruiker.sportapp.R;


/**
 * @author BOOT-05
 *
 * Laat een FAQ en help Pagina zien. (zie de xml voor de tekst)
 */
public class hulpEnInfo extends Fragment {


    public hulpEnInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hulp_en_info, container, false);
    }

}
