package unpsjb.pm.fi.ars;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewHolderPhrase extends RecyclerView.ViewHolder {

    public TextView tvPhrase;
    public TextView tvAuthor;
    public RatingBar rtngBar;


    public ViewHolderPhrase(View itemView) {
        super(itemView);

        tvPhrase = itemView.findViewById(R.id.item_phrase);
        tvAuthor = itemView.findViewById(R.id.item_author);
        rtngBar = itemView.findViewById(R.id.rtngBar1);

    }
}
