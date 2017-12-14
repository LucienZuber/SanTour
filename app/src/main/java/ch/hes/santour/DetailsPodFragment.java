package ch.hes.santour;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import BLL.FirebaseClass;
import BLL.PODManager;
import Models.Difficulty;


public class DetailsPodFragment extends Fragment {
    private String TAG = "TAG";
    private ListView mListView;
    private PODManager podManager;
    private DatabaseReference mRootRef;
    private List<Difficulty> listDifficultiesBD = new ArrayList<Difficulty>();


    public DetailsPodFragment() {
        // Required empty public constructor
        mRootRef = FirebaseClass.getDatabase().getReference();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_details_pod, container, false);
        //to see the menu on the top
        setHasOptionsMenu(true);


        podManager = new PODManager();

        //set the title on the app
        getActivity().setTitle(R.string.pod_details);

        mListView = rootView.findViewById(R.id.listViewDetailsPod);
        showDetailsList();

        // button CANCEL
        Button bt_pod_details_cancel = rootView.findViewById(R.id.bt_pod_details_cancel);
        bt_pod_details_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                getFragmentManager().popBackStack();
            }
        });

        // button Save
        Button bt_pod_details_save = rootView.findViewById(R.id.bt_pod_details_save);
        bt_pod_details_save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Bundle bundle = getArguments();
                if (bundle != null) {
                    String podName = bundle.getString("podName");
                    String podDescription = bundle.getString("podDescription");
                    byte[] byteArray = bundle.getByteArray("photo");
                    Bitmap photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    podManager.createPOD(podName, podDescription, photo);

                }

                //We restart the timer
                ((MainActivity) getActivity()).restartTimer();

                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack("track", 0);

            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.language, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showDetailsList() {
        mRootRef.child("difficulties").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDifficultiesBD = new ArrayList<>();
                for (DataSnapshot diffDataSnapShot : dataSnapshot.getChildren()) {
                    Difficulty difficulty = diffDataSnapShot.getValue(Difficulty.class);
                    listDifficultiesBD.add(difficulty);
                }
                ListPodDifficulties adapter = new ListPodDifficulties(getActivity(), listDifficultiesBD);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
