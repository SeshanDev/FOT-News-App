package com.example.fotnewsapp.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fotnewsapp.R;
import com.example.fotnewsapp.adapter.NewsAdapter;
import com.example.fotnewsapp.databinding.FragmentHomeBinding;
import com.example.fotnewsapp.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<NewsItem> newsList = new ArrayList<>();
        newsList.add(new NewsItem(R.drawable.logo, "Galle take firm control of NSL final", "29 Apr 2025",
                "Galle took firm control when they reduced their opponents Dambulla to 154 for five on the second day of the National Super League four-day cricket tournament final, continued at the SSC Grounds in Maitland Place yesterday."));
        newsList.add(new NewsItem(R.drawable.facebook, "Title 2", "28 Apr 2025",
                "Short description for news 2 goes here, with some longer details. Galle took firm control when they reduced their opponents Dambulla to 154 for five...Galle took firm control when they reduced their opponents Dambulla to 154 for five on the second day of the National Super League four-day cricket tournament final, continued at the SSC Grounds in Maitland Place yesterday."));
        newsList.add(new NewsItem(R.drawable.google, "Title 3", "27 Apr 2025",
                "Another news body example that can be expanded and collapsed.Galle took firm control when they reduced their opponents Dambulla to 154 for five on the second day of the National Super League four-day cricket tournament final, continued at the SSC Grounds in Maitland Place yesterday."));
        newsList.add(new NewsItem(R.drawable.background, "Title 4", "26 Apr 2025",
                "More detailed news content here to test the expandable feature.Galle took firm control when they reduced their opponents Dambulla to 154 for five on the second day of the National Super League four-day cricket tournament final, continued at the SSC Grounds in Maitland Place yesterday."));


        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new NewsAdapter(newsList));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

