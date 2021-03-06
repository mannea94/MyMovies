package com.example.manne.mymovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manne.mymovies.Activities.FavouritesActivity;
import com.example.manne.mymovies.Api.RestApi;
import com.example.manne.mymovies.Listener.OnRowClickListener;
import com.example.manne.mymovies.Model.MovieModel;
import com.example.manne.mymovies.Model.MovieResponse;
import com.example.manne.mymovies.Model.MyMovies;
import com.example.manne.mymovies.PreferenceManager;
import com.example.manne.mymovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by manne on 06.2.2018.
 */

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.ViewHolder> {

    Context context;
    ArrayList<MyMovies> myMovies = new ArrayList<>();
    MovieModel movieModel = new MovieModel();
    RestApi api = new RestApi(context);
    OnRowClickListener onRowClickListener;
    MovieResponse movieResponse = new MovieResponse();

    public void setItems(ArrayList<MyMovies> myMovies1){
        myMovies=myMovies1;
    }

    public AdapterFavourite(Context context1, MovieModel movieModel_, OnRowClickListener onRowClickListener_){
        context=context1;
        movieModel=movieModel_;
        onRowClickListener=onRowClickListener_;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Inflate the custom layout
        View view = inflater.inflate(R.layout.recycler_view_row_favorites, parent, false);
        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyMovies myMovie = movieModel.results.get(position);
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+myMovie.getPoster_path())
                .into(holder.mainImage);
        holder.movieTitle.setText(myMovie.getTitle());
        holder.ratingText.setText(myMovie.getVote_average());
        holder.session_id=PreferenceManager.getSessionID(context);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movieResponse.media_type="movie";
                movieResponse.media_id=Integer.valueOf(movieModel.results.get(position).getId());
                movieResponse.favorite=false;
                Call<MyMovies> call = api.postFavouriteMovie(holder.session_id, "application/json;charset=utf-8;", movieResponse);
                call.enqueue(new Callback<MyMovies>() {
                    @Override
                    public void onResponse(Call<MyMovies> call, Response<MyMovies> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            movieModel.results.remove(position);
                            notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyMovies> call, Throwable t) {

                    }
                });
            }
        });
        holder.mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRowClickListener.onRowClick(myMovie, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieModel.results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.mainImage)
        ImageView mainImage;
        @BindView(R.id.ratingImageDelete)
        ImageView delete;
        @BindView(R.id.movieTitle)
        TextView movieTitle;
        @BindView(R.id.ratingImage)
        ImageView ratingImage;
        @BindView(R.id.ratingText)
        TextView ratingText;
        String session_id;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
