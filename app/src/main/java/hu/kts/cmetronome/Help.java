package hu.kts.cmetronome;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andrasnemeth on 25/01/16.
 */
public class Help {

    @InjectView(R.id.help)
    TextView helpTextView;


    public Help(Activity activity) {
        ButterKnife.inject(this, activity);
    }

    public void setEnabled(boolean enabled) {
        helpTextView.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void setHelpTextByWorkoutStatus(WorkoutStatus status) {
        helpTextView.setText(getHelpTextIdByWorkoutStatus(status));
    }

    private int getHelpTextIdByWorkoutStatus(WorkoutStatus status) {
        switch (status) {
            case BEFORE_START: return R.string.help_before_start;
            case IN_PROGRESS: return R.string.help_in_progress;
            case PAUSED: return R.string.help_paused;
            case BETWEEN_SETS: return R.string.help_between_sets;
            default: return R.string.empty_string;
        }
    }
}
