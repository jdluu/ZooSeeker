package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    private ExhibitViewModel viewModel;
    private ExhibitListAdapter adapter;

    private AutoCompleteTextView searchBar;
    private TextView deleteSearchBtn;
    private Button searchBtn;
    private Button  getDirectionsBtn;
    private TextView numPlanned;
    private Button clearBtn;
    private Button showCheckedBtn;
    private Button returnToSearchBtn;

    @SuppressLint("StaticFieldLeak")
    static MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);

        adapter = new ExhibitListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleAdded);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitListItems);

        recyclerView = findViewById(R.id.rvExhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.searchBar);
        deleteSearchBtn = findViewById(R.id.deleteBtn);
        searchBtn = findViewById(R.id.searchButton);
        getDirectionsBtn = findViewById(R.id.getDirectionsButton);
        numPlanned = findViewById(R.id.counter);
        clearBtn = findViewById(R.id.clearExhibitsBtn);
        showCheckedBtn = findViewById(R.id.showCheckedBtn);
        returnToSearchBtn = findViewById(R.id.returnBtn);

        setNumPlanned();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null) {
                    if (charSequence.length() != 0) {
                        displaySearch(String.valueOf(charSequence));
                    }
                    else {
                        displayAll();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        deleteSearchBtn.setOnClickListener(this::deleteSearch);
        searchBtn.setOnClickListener(this::searchExhibit);
        clearBtn.setOnClickListener(this::uncheckList);
        showCheckedBtn.setOnClickListener(this::showChecked);
        returnToSearchBtn.setOnClickListener(this::returnToSearch);
        getDirectionsBtn.setOnClickListener(this::getDirectionsClicked);
    }

    private void deleteSearch(View view) {
        searchBar.getText().clear();
        displayAll();
    }

    private void searchExhibit(View view) {
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            Utilities.showAlert(this, "Please enter a valid exhibit!");
            return;
        }
        displaySearch(search);
    }


    private void uncheckList(View view) {
        uncheck();
        setNumPlanned();
    }

    private void showChecked(View view) {
        List<ExhibitItem> checkedExhibits = ExhibitList.getCheckedExhibits();
        adapter.setExhibitListItems(checkedExhibits);
        showCheckedBtn.setVisibility(View.INVISIBLE);
        returnToSearchBtn.setVisibility(View.VISIBLE);
    }

    private void returnToSearch(View view) {
        displayAll();
        returnToSearchBtn.setVisibility(View.INVISIBLE);
        showCheckedBtn.setVisibility(View.VISIBLE);
    }

    public static MainActivity getInstance() {
        return main;
    }

    void getDirectionsClicked(View view) {
        List<ExhibitItem> toVisit = ExhibitList.getCheckedExhibits();

        if (toVisit.size() == 0) {
            Utilities.showAlert(this, "Select Exhibit(s) Before Continuing!");
            return;
        }

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.JSON");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.JSON");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.JSON");

        DirectionTracker dt = new DirectionTracker(g, vInfo, eInfo);
        dt.getDirections(toVisit);

        Intent directionIntent = new Intent(this, DirectionActivity.class);
        startActivity(directionIntent);
    }

    public List<ExhibitItem> getExhibits() {
        return viewModel.getAllExhibits();
    }

    @SuppressLint("SetTextI18n")
    private void setNumPlanned() {
        numPlanned.setText("Planned " + ExhibitList.getNumChecked() + " Exhibit(s)");
    }

    public void uncheck() {
        List<ExhibitItem> checkedExhibits = ExhibitList.getCheckedExhibits();
        for (ExhibitItem item : checkedExhibits) {
            viewModel.uncheckList(item);
        }
    }

    private void displayAll() {
        List<ExhibitItem> allExhibits = ExhibitList.getAllExhibits();
        adapter.setExhibitListItems(allExhibits);
    }

    private void displaySearch(String search) {
        List<ExhibitItem> searchLists = ExhibitList.getSearchItems(search);
        adapter.setExhibitListItems(searchLists);
    }
}
