package unpsjb.pm.fi.ars;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

public class RatingDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_title_rating);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View viewContent = inflater.inflate(R.layout.rating_dialog_layout,null);

        builder.setView(viewContent);

        final RatingBar rtngBar = (RatingBar) viewContent.findViewById(R.id.rtngBar2);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity activity = (MainActivity) getActivity();

                if ( rtngBar != null ) {
                    activity.ratingPhrase(rtngBar.getRating());
                }

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
