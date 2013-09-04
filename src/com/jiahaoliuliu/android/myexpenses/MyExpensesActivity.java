package com.jiahaoliuliu.android.myexpenses;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class MyExpensesActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_expenses);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar_my_expenses);
        // Title
        actionBar.setTitle(R.string.title_my_expenses);
        actionBar.setTitleGravity(Gravity.CENTER);
        actionBar.setTitleSize(22.0f);
        
        actionBar.setHomeAction(new IntentAction(this,
                                                 new Intent(this, NewExpensesActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                                 R.drawable.content_new,
                                                 IntentAction.SLIDE_RIGHT_TO_LEFT));
        
        /*
        actionBar.addAction(new IntentAction(this,
                                             createShareIntent(),
                                             R.drawable.ic_launcher,
                                             IntentAction.FADE_IN_FADE_OUT));
        */
        actionBar.addAction(new SortAction());
    }

    /*
    private Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared from the ActionBar widget.");
        return Intent.createChooser(intent, "Share");
    }
    */

    private class SortAction extends AbstractAction {

        public SortAction() {
            super(R.drawable.collections_sort_by_size);
        }

        @Override
        public void performAction(View view) {
            Toast.makeText(MyExpensesActivity.this,
                    "Sort elements by...", Toast.LENGTH_SHORT).show();
        }

    }

}