package com.example.pr5moskvina

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private var movies: List<Movie>,
    private val onEditClick: (Movie) -> Unit,
    private val onDeleteClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvYear: TextView = view.findViewById(R.id.tvYear)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.tvTitle.text = movie.title
        holder.tvYear.text = "Год: ${movie.year}"

        holder.btnEdit.setOnClickListener { onEditClick(movie) }
        holder.btnDelete.setOnClickListener { onDeleteClick(movie) }
    }

    override fun getItemCount() = movies.size

    fun updateList(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}