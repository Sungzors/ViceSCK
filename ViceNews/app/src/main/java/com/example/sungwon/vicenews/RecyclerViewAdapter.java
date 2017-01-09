package com.example.sungwon.vicenews;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Random;

public class RecyclerViewAdapter extends CursorRecyclerViewAdapter<RecyclerViewAdapter.ViewHolder> {
    Context context;

    public RecyclerViewAdapter(Context context, Cursor cursor) {

        super(context, cursor);
        this.context = context;
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public CardView cardView;


        public ViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.textView);
//            textDetailView = (TextView) view.findViewById(R.id.textDetailView);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }



    private final static int FADE_DURATION = 700;

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {

        String text = cursor.getString(cursor.getColumnIndex(ViceDBHelper.VICENEWS_TITLE));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean simple = sp.getBoolean("captions_switch", true);
        boolean viewswitch = sp.getBoolean("view_switch", true);
        int substrval = (viewswitch)? 40: 20;
        String newtext = (simple&&text.length()>substrval)? text.substring(0,substrval) + "...":text;
        viewHolder.textView.setText(newtext);
//        viewHolder.textDetailView.setText(cursor.getString(cursor.getColumnIndex(ViceDBHelper.VICENEWS_BODY)));
        //TODO: WILL THIS WORK?
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(ViceDBHelper.VICENEWS_THUMBNAIL))).into(viewHolder.imageView);

        setFadeAnimation(viewHolder.cardView);
        setScaleAnimation(viewHolder.cardView);
        setAnimation(viewHolder.cardView, lastPosition);


        final Integer position = cursor.getPosition();
        viewHolder.cardView.setTag(position);
        //INTENT TO DETAIL VIEW
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                Cursor cursor = getCursor();
                int position = ((Integer) view.getTag()).intValue();
                cursor.moveToPosition(position);
                String title = cursor.getString(cursor.getColumnIndex(ViceDBHelper.VICENEWS_TITLE));
                String body = cursor.getString(cursor.getColumnIndex(ViceDBHelper.VICENEWS_AUTHOR));
                String image = cursor.getString(cursor.getColumnIndex(ViceDBHelper.VICENEWS_THUMBNAIL));
                intent.putExtra("title", title);
                intent.putExtra("body", body);
                intent.putExtra("image", image);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                    View image2 = view.findViewById(R.id.imageView);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity) view.getContext()), image2, "imageViewTransition");
                    view.getContext().startActivity(intent, options.toBundle());
                }else{
                    view.getContext().startActivity(intent);
                }
            }
        });
    }
//ANIMATIONS
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

}
