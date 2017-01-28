package nl.jhnbos.meetmeapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private String email;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BACK BUTTON
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Instantiating variables
        email = this.getIntent().getStringExtra("Email");

        setupTabs();
    }

    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF METHODS

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Groups"), 0, true);
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"), 1, false);

        //replace default fragment
        replaceFragment(new GroupFragment());

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());

                if (tab.getPosition() == 0) {
                    replaceFragment(new GroupFragment());
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new ContactFragment());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
        });
    }

    private void onTabTapped(int position) {
        switch (position) {
            case 0:
                // Do something when first tab is tapped here
                break;
            default:
                //Toast.makeText(this, "Tapped " + position, Toast.LENGTH_SHORT);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_main, fragment);

        transaction.commit();
    }

    //END OF METHODS
    /*-----------------------------------------------------------------------------------------------------*/
    //BEGIN OF LISTENERS


    //END OF LISTENERS
}
